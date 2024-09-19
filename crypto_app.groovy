pipeline {
    agent none

    environment {
        // Docker environment parameters
        APP_NAME = 'crypto-app'
        DOCKER_REPO = 'almoglevig'
        APP_VERSION = "v1.0.0"
        dockerImage = '' // Set as a global parameter

        // Agent environment parameters
        BUILD_AGENT = 'CISLV'  // Agent for build docker image
        K8S_DEPLOY_AGENT = 'KUBESLV'  // Agent for deploying crypto-app on Kubernetes cluster
        
        // Credential environment parameters
        DOCKER_TOKEN = credentials('docker-token')
        KUBECONFIG = credentials('kubeconfig-credentials')  // Kubernetes credentials for remote cluster
    }

    stages {
        stage('CI stages') {
            agent {
                label "${env.BUILD_AGENT}"
            }
            steps {
                script {
                    echo "Starting Docker build process..."
                    
                    stage('Checkout scm') {
                        echo "Cloning the repository into ${env.BUILD_AGENT}"
                        checkout scm
                    }

                    stage("Build '${env.APP_NAME}' Image") {
                        echo "Building the Docker image"
                    }
                    
                    dockerImage = "${env.DOCKER_REPO}/${env.APP_NAME}:${env.APP_VERSION}-${env.BUILD_NUMBER}"

                    stage('Login to Docker Registry') {
                        echo "Do docker login to DockerHub"
                        sh """
                            echo ${DOCKERHUB_TOKEN} | docker login -u ${env.DOCKER_REPO} --password-stdin
                        """
                    }

                    stage("Push '${env.dockerImage}' Image") {
                        echo "Pushing the '${env.dockerImage}'' Docker image to the Dockerhub"
                    }
                }
            }
        }

        stage("Deploy '${env.APP_NAME}' as a pod on K8s cluster via Helm") {
            agent {
                label "${env.K8S_DEPLOY_AGENT}"
            }
            steps {
                script {
                    stage('Checkout scm') {
                        echo "Cloning the repository into ${env.BUILD_AGENT}"
                        checkout scm
                    }

                    def nodeIP = sh(script: "kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}'", returnStdout: true).trim()

                    stage("Generate dynamic Helm values.yaml"){
                        echo "Generated dynamic Helm values.yaml"
                        def valuesTemplate = readFile 'values-template.yaml'

                        def valuesContent = valuesTemplate.replace('{{DOCKER_REPO}}', "${env.DOCKER_REPO}")
                                                      .replace('{{APP_NAME}}', "${env.APP_NAME}")
                                                      .replace('{{DOCKER_TAG}}', "${env.APP_VERSION}-${env.BUILD_NUMBER}")
                        
                        writeFile(file: 'Helm/values.yaml', text: valuesContent)
                    }

                    stage("Generate dynamic Helm/Chart.yaml") {
                        def chartTemplate = readFile 'Chart-template.yaml'

                        def chartContent = chartTemplate.replace('{{APP_NAME}}', "${env.APP_NAME}")
                                                        .replace('{{CHART_VERSION}}', "${env.CHART_VERSION}")
                                                        .replace('{{APP_VERSION}}', "${env.APP_VERSION}")

                        writeFile(file: 'Helm/Chart.yaml', text: chartContent)

                        echo "Generated dynamic Helm Chart.yaml"
                    }

                    stage("Deploying '${env.APP_NAME}' on K8s cluster"){
                        echo "Deploying '${env.APP_NAME}' on K8s cluster"
                        sh """
                            helm upgrade --install ${env.APP_NAME} ./Helm
                        """
                    }

                    stage('Post Deployment') {
                        echo "Verify the web-app on the cluster"
                    }
                }
            }
        }
    }

    post {
        success {
            echo "The pipline has completed successfully."
        }
        failure {
            echo "The pipeline has been failed."
        }
    }
}