# interview_assignment
This repository has been dedicated for home asignment - Leumi


# Crypto App CI/CD Pipeline
This project defines a Jenkins Declarative Pipeline that automates the build, push, and deployment of a Dockerized web application (Crypto App) to a Kubernetes cluster using Helm.

## Prerequisites

### 1. Jenkins Setup
Please Ensure Jenkins has been installed and configured with the following:
- **Docker Pipeline Plugin** (for building and pushing Docker images)
- **Kubernetes Plugin** (for deploying to Kubernetes)
- **Git Plugin** (for checking out SCM)

Add the following Jenkins credentials:
- **DockerHub Token** (ID: `docker-token`): Your DockerHub token for authentication.

### 2. DockerHub
Set up a DockerHub account where the Docker image will be pushed.

### 3. Kubernetes Cluster
A Kubernetes cluster must be available, with Jenkins agents having access.

### 4. Install Cert-Manager in the Cluster

Install **Cert-Manager** to handle TLS certificates in your Kubernetes cluster:

```bash
helm repo add jetstack https://charts.jetstack.io
helm repo update
helm install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.10.1 \
  --set installCRDs=true
```

### 5. Workflow
The pipeline consists of the following steps:

#### 5.1 
Checkout SCM: Clones the source code repository from the specified branch into the build agent.
#### 5.2 
Build Docker Image: Using the Dockerfile from the crypto_app_dir, the pipeline builds a Docker image for the application and tags it with the application version and build number.
#### 5.3 
DockerHub Login and Push: The pipeline logs into DockerHub using the provided token, then pushes the built Docker image to the specified Docker repository.
#### 5.4
Generate Helm Files: Helm's Chart.yaml and values.yaml are dynamically generated to include the application name, Docker repository, Docker tag, and DNS name.
#### 5.5
Deploy to Kubernetes: The pipeline uses Helm to deploy the application as a pod on the Kubernetes cluster, leveraging the dynamically generated Helm files.
#### 5.6
Post-Deployment Check: Verifies the deployment by checking the status of the pods in the specified Kubernetes namespace.
