# from base image node
FROM --platform=linux/arm64 node:18-alpine

# set working directory
WORKDIR /app

# Copies package.json and package-lock.json to Docker environment
COPY package*.json ./

# Installs all node packages
RUN npm install --legacy-peer-deps

# Copies everything over to Docker environment
COPY . .

# Build for production.
RUN npm run build

# Install `serve` to run the application.
RUN npm install -g serve

# Uses port which is used by the actual application
EXPOSE 3000

# Run application
# CMD [ "npm", "start" ]
CMD serve -s build