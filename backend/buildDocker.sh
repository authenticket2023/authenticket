#!/bin/bash

# Command 1: Clean and install with Maven
mvn clean install

# Command 2: Build the Docker image
docker build -t panmingwei/authenticket-backend .

# Command 3: Run the Docker container
docker run -dp 127.0.0.1:8080:8080 panmingwei/authenticket-backend

# Command 4: Push the Docker image to a repository
docker push panmingwei/authenticket-backend