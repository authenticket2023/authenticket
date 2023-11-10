# from base image node
FROM node:18-buster-slim

WORKDIR /usr/local/apps/myapp

#copies everything over to docker environment
COPY . .

# copy other files as well
COPY package*.json ./

# needed to install canvas
RUN npm install
RUN npm update
ENV PATH=/usr/local/myapp/node_modules/.bin:$PATH

#expose the port
EXPOSE 4242

# command to run when intantiate an image
CMD ["npm","start"]