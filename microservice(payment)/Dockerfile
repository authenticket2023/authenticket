# from base image node
FROM node:18-alpine

WORKDIR /app

# copy other files as well
COPY package*.json ./

# needed to install canvas
RUN npm install
RUN npm update

#expose the port
EXPOSE 4242

# command to run when intantiate an image
CMD ["npm","start"]