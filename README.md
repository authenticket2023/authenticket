# Authenticket

## Docker
### frontend
- cd to 'authenticket/frontend'
  - Build the image : `docker build -t authenticket-frontend .`
  - Run the image : `docker run -dp 127.0.0.1:3000:3000 authenticket-frontend`

### backend
- cd to 'authenticket/backend'
  - Generate jar file : `mvn clean install`  => this will build spring boot application into jar
  - Build the image : `docker build -t authenticket-backend .` => name of the jar file need match in the Dockerfile
  - Run the image : `docker run -dp 127.0.0.1:8080:8080 authenticket-backend`
