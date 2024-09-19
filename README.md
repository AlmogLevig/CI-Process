# interview_assignment
This repository has been dedicated for home asignment - Leumi


prerequisites for self-signed certificates in k8s agent, please do these steps before running the pipeline:

Install Cert-Manager CRDs
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.10.1/cert-manager.crds.yaml


Install Cert-Manager in the Cluster:

Add the Cert-Manager Helm repository:
helm repo add jetstack https://charts.jetstack.io

Update your Helm repositories:
helm repo update

Install Cert-Manager in Kubernetes cluster:
helm install cert-manager jetstack/cert-manager \
  --namespace cert-manager \
  --create-namespace \
  --version v1.10.1 \
  --set installCRDs=true
