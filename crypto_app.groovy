pipeline {
    agent none

    environment {
        // Docker environment parameters
        APP_NAME = 'crypto-app'
        DOCKER_REPO = 'almoglevig'
        APP_VERSION = "v1.0.0"
        dockerImage = '' // Set as a global parameter

        // Agent environment parameters
        DOCKER_BUILD_AGENT = ''  // Agent for build docker image
        K8S_DEPLOY_AGENT = 'k8s-server'  // Agent for deploying crypto-app on Kubernetes
        
        // credential environment parameters
        DOCKER_USERNAME = credentials('docker-username')
        DOCKER_PASSWORD = credentials('docker-password')
        KUBECONFIG = credentials('kubeconfig')  // Kubernetes credentials for remote cluster
    }

    stages {
        stage('CI stages') {
            agent {
                label "${env.DOCKER_BUILD_AGENT}"
            }
            steps {
                script {
                    echo "Starting Docker build process..."
                    
                    stage('Checkout scm') {
                        echo "Cloning the repository into ${env.DOCKER_BUILD_AGENT}"
                        checkout scm
                    }

                    stage("Build '${env.APP_NAME}' Image") {
                        echo "Building the Docker image"
                    }
                    
                    dockerImage = "${env.DOCKER_REPO}/${env.APP_NAME}:${env.APP_VERSION}-${env.BUILD_NUMBER}"

                    stage('Login to Docker Registry') {
                        echo "Do docker login to DockerHub"
                    }

                    stage("Push '${dockerImage}' Image") {
                        echo "Pushing the '${dockerImage}'' Docker image to the Dockerhub"
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

                    stage("Pull ${dockerImage}' Image"){
                        echo "pull the '${dockerImage}' Docker image to the Dockerhub"
                    }

                    stage('Post Deployment') {
                        echo "verify the web-app on the cluster"
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