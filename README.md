# CS203 - CSD (G3T7) : Authenticket
## Our production webiste (Working best in Google Chrome)
- `http://authenticket-877250869.ap-southeast-1.elb.amazonaws.com/Home`
- Since our one is http, not https, so Google Chrome treat our website as insecure, which auto block the camera, which we need for our facial recognition and QR scanner. 
- To enable it, enter `chrome://flags/#unsafely-treat-insecure-origin-as-secure` in your chrome browser
- Enable the Insecure origins treated as secure and add our website into it, as shown in the image
![Alt text](image.png)

## Team Member : 
- [CHUA YI KAI](https://github.com/ChuaYiKai)
- [GEORGIA NG](https://github.com/georgiaxng)
- [LIM ZHENGLONG BRIAN](https://github.com/Liseon617)
- [NAUFAL SYAQIL BIN AZMI](https://github.com/nafutofu)
- [PAN MINGWEI](https://github.com/xXxPMWxXx)
- [PETRINA WONG JING TING](https://github.com/petrinawjt)
## prerequisite
- Frontend (React) : Node V18 (Typescript)
- Backend(Spring Boot) : Java JDK 17 
  - You need to add `secrets.properties` to `backend/src/main/resources/` . Please contact us to get the file.
### Accounts for development
- User
  - Account : `blzl.l.gtsj@gmail.com`
  - Password : `password`  
- Organiser
  - Account : `authenticket2023@gmail.com`
  - Password : `password`  
- Admin
  - Account : `admin@admin.com`
  - Password : `admin`   

## Docker
### build and run both frontend & backend
- At root directory => `docker compose up`
### Pull image from docker hub
- `docker pull authen2023/authenticket-frontend` => OS/arch : linux/amd64 => auto build from our CI
  - we tried to make this build as linux/arm64 as well using buildx , however as of 7 Nov 2023, we unable to make it work yet. 
  - The main reason is because our EC2 instance is t4g.small which was using ARM64 (Amazon's Graviton2 processor) architecture
  - if you want arm64 docker image => `docker pull georgiaxng/authenticket-frontend`
- `docker pull authen2023/authenticket-backend` => OS/arch : linux/arm64 => auto build from our CI
- `docker pull panmingwei/authenticket-facial` => OS/arch : linux/arm64 => manual build as we would not make change frequently 
- `docker pull liseon/authenticket-payment` => OS/arch : linux/arm64 => manual build as we would not make change frequently 
- To run these four docker image together, cd to `authenticket` and run `docker compose up` => make sure you have pull the correct docker image

## To build docker locally
### frontend
- cd to 'authenticket/frontend'
  - Build the image : `docker build -t authenticket-frontend .`
  - Run the image : `docker run -dp 127.0.0.1:3000:3000 authenticket-frontend`

### backend
- cd to 'authenticket/backend'
  - Generate jar file : `mvn clean install`  => this will build spring boot application into jar
  - Build the image : `docker build -t authenticket-backend .` => name of the jar file need match in the Dockerfile
  - Run the image : `docker run -dp 127.0.0.1:8080:8080 authenticket-backend`

### facial
- cd to 'authenticket/microservice(facial)'
  - Build the image : `docker build -t authenticket-facial .`
  - Run the image : `docker run -dp 127.0.0.1:8000:8000 authenticket-facial`

### payment
- cd to 'authenticket/microservice(payment)'
  - Build the image : `docker build -t authenticket-payment .`
  - Run the image : `docker run -dp 127.0.0.1:42424:4242 authenticket-payment`

## To run our code locally
### frontend
- cd to 'authenticket/frontend' => port 3000
  - Install dependencies : `npm install --legacy-peer-deps`
  - Run the frontend: `npm start`

### backend
- cd to 'authenticket/backend' => port 8080
  - Generate jar file : `mvn clean install`  => this will build spring boot application into jar
  - Build the image : `docker build -t authenticket-backend .` => name of the jar file need match in the Dockerfile
  - Run the image : `docker run -dp 127.0.0.1:8080:8080 authenticket-backend`

### facial
- To run this locally is very troublesome, as need to install a lot of dependencies, as the faceapi.js is running on top of tensorflow
- So we strongly recommend running it via docker
- cd to 'authenticket/microservice(payment)' => `docker compose up` 
  - this will build and run a hot reload docker => which will be able to reload the code changed without rebuild the docker image

### payment
- cd to 'authenticket/microservice(payment)' => port 4242
  - Install dependencies : `npm install`
  - Run the frontend: `npm start`