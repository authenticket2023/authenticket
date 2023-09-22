#!/bin/bash

# Build and push frontend Docker image
echo "Building and pushing frontend Docker image..."
cd frontend
docker build -t panmingwei/authenticket-frontend .
docker push panmingwei/authenticket-frontend
cd ..

# Build and push backend Docker image
echo "Building and pushing backend Docker image..."
cd backend
mvn clean install
docker build -t panmingwei/authenticket-backend .
docker push panmingwei/authenticket-backend
cd ..

echo "Done."
