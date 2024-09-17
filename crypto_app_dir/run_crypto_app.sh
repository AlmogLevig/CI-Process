#!/bin/bash

# Function to display usage
usage() {
    echo "Usage: $0 [start|stop|restart|delete|rebuild|tag] [user] [tag]"
    exit 1
}

# Function to start the project
start_project() {
    echo "Building and starting the project..."

    # Navigate to the project directory
    cd "$(dirname "$0")"
    
    # Build and run the Docker
    docker build -t coingecko-app .
    docker run -p 80:5000 --name=crypto-app -d coingecko-app

    # Check if the services are running
    echo "Checking the status of the services..."
    docker images
    sleep 5
    docker ps -a

    echo "The application should be available at http://localhost:80"
}

# Function to stop the project
stop_project() {
    echo "Stopping the project..."

    # Navigate to the project directory
    cd "$(dirname "$0")"

    # Stop the Docker container
    docker stop crypto-app

    echo "The project has been stopped."
}

# Function to restart the project
restart_project() {
    stop_project
    start_project
}

# Function to delete the project
delete_project() {
    echo "Deleting the project..."

    # Navigate to the project directory
    cd "$(dirname "$0")"
    
    # Stop the Docker Compose project
    stop_project
    
    docker rm crypto-app
    sleep 5
    docker ps -a

    # Remove Docker images    
    docker rmi $(docker images -q coingecko-app) --force
    sleep 5
    docker images
    
    echo "The project and its images have been deleted."
}

# Function to tag an existing Docker image
tag_image() {
    if [ -z "$2" ] || [ -z "$3" ]; then
        echo "Please provide a user and a tag."
        exit 1
    fi

    user=$2
    tag=$3
    new_image_name="$user/coingecko-app:$tag"

    echo "Tagging the image 'coingecko-app:latest' as '$new_image_name'"
    docker tag coingecko-app:latest $new_image_name
    echo "Image tagged successfully as '$new_image_name'."
}

case "$1" in
    start)
        start_project
        ;;
    stop)
        stop_project
        ;;
    restart)
        restart_project
        ;;
    delete)
        delete_project
        ;;
    rebuild)
        delete_project
        sleep 5
        start_project
        ;;
    tag)
        tag_image "$@"
        ;;
    *)
        usage
        ;;
esac