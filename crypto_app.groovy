pipeline {
    agent any

    environment {
        // Docker environment parameters
        APP_NAME = 'crypto-app'
        DOCKER_REPO = 'almoglevig'
        APP_VERSION = "v1.0.0"

        APP_DIR = "./crypto_app_dir"
        // Agent environment parameters
        BUILD_AGENT = 'CISLV'  // Agent for build docker image
        K8S_DEPLOY_AGENT = 'KUBESLV'  // Agent for deploying crypto-app on Kubernetes cluster
        
        // Credential environment parameters
        DOCKER_TOKEN = credentials('docker-token')

        // Dns name
        DNS_NAME = "cryptoapp.local"
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

                    // Set Docker Image var
                    def dockerImage = "${env.DOCKER_REPO}/${env.APP_NAME}:${env.APP_VERSION}-${env.BUILD_NUMBER}"
                    
                    stage("Build '${env.APP_NAME}' Image") {
                        echo "Building the Docker image"
                        sh """
                            docker build -t ${dockerImage} ${env.APP_DIR}
                        """
                    }
                    

                    stage('Login to Docker Registry') {
                        echo "Do docker login to DockerHub"
                        sh """
                            echo ${DOCKER_TOKEN} | docker login -u ${env.DOCKER_REPO} --password-stdin
                        """
                    }

                    stage("Push '${dockerImage}' Image") {
                        echo "Pushing the '${dockerImage}' Docker image to the Dockerhub"
                        sh """
                            docker push ${dockerImage}
                        """
                    }
                }
            }
        }

        stage("Deploy Crypto_app as a pod on K8s cluster via Helm") {
            agent {
                label "${env.K8S_DEPLOY_AGENT}"
            }
            steps {
                script {
                    stage('Checkout scm') {
                        echo "Cloning the repository into ${env.K8S_DEPLOY_AGENT}"
                        checkout scm
                    }

                    stage("Generate dynamic Helm/Chart.yaml") {
                        def chartTemplate = readFile 'Chart-template.yaml'

                        def chartContent = chartTemplate.replace('{{APP_NAME}}', "${env.APP_NAME}")


                        writeFile(file: 'Helm/Chart.yaml', text: chartContent)

                        echo "Generated dynamic Helm Chart.yaml"
                    }


                    stage("Generate dynamic Helm values.yaml"){
                        echo "Generated dynamic Helm values.yaml"
 

                        def valuesTemplate = readFile 'values-template.yaml'

                        def valuesContent = valuesTemplate.replace('{{DOCKER_REPO}}', "${env.DOCKER_REPO}")
                                                      .replace('{{APP_NAME}}', "${env.APP_NAME}")
                                                      .replace('{{DOCKER_TAG}}', "${env.APP_VERSION}-${env.BUILD_NUMBER}")
                                                      .replace('{{HOST_NAME}}', "${env.DNS_NAME}")
                       
                        writeFile(file: 'Helm/values.yaml', text: valuesContent)
                    }


                    stage("Deploying '${env.APP_NAME}' on K8s cluster"){
                        echo "Deploying '${env.APP_NAME}' on K8s cluster"
                        sh """
                            helm upgrade --install ${env.APP_NAME} ./Helm
                        """
                    }

                    stage('Post Deployment') {
                        echo "Verify the web-app on the cluster"
                        sh """
                            sleep 60 && kubectl get pods -n crypto-app-ns
                        """
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
        always {
            // Clean up the workspace
            cleanWs()
        }
    }
}