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
        
        // credential environment parameters
        DOCKER_USERNAME = credentials('docker-username')
        DOCKER_PASSWORD = credentials('docker-password')
        KUBECONFIG = credentials('kubeconfig')  // Kubernetes credentials for remote cluster
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
                    def nodeIP = sh(script: "kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}'", returnStdout: true).trim()

                    stage("Pull ${env.dockerImage}' Image"){
                        echo "Pull the '${env.dockerImage}' Docker image from Dockerhub"
                    }

                    stage("Deploying ${env.APP_NAME}' on K8s cluster"){
                        echo "Deploying ${env.APP_NAME}' on K8s cluster"
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