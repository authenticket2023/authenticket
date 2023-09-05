# Authenticket

## Docker
### frontend
- cd to 'authenticket/frontend'
  - Build the image : `docker build -t authenticket-frontend .`
  - Run the image : `docker run -dp 127.0.0.1:3000:3000 authenticket-frontend`

### backend
- cd to 'authenticket/backend'
  - Build the image : `docker build -t authenticket-backend .`
  - Run the image : `docker run -dp 127.0.0.1:8080:8080 authenticket-backend`