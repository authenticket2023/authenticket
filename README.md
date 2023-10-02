# CS203 - CSD (G3T7) : Authenticket
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
- `docker pull panmingwei/authenticket-frontend`
- `docker pull panmingwei/authenticket-backend`
- `docker pull panmingwei/authenticket-facial`
### frontend
- cd to 'authenticket/frontend'
  - Build the image : `docker build -t authenticket-frontend .`
  - Run the image : `docker run -dp 127.0.0.1:3000:3000 authenticket-frontend`

### backend
- cd to 'authenticket/backend'
  - Generate jar file : `mvn clean install`  => this will build spring boot application into jar
  - Build the image : `docker build -t authenticket-backend .` => name of the jar file need match in the Dockerfile
  - Run the image : `docker run -dp 127.0.0.1:8080:8080 authenticket-backend`

### Run build_and_push.sh (replace with your own docker ID)
- This script will build both frontend and backend docker image and push them to Docker Hub
- cd to 'authenticket'
  - `chmod +x build_and_push.sh` => make the script executable
  - `./build_and_push.sh` => run the script

## Run in AWS EC2
- `tmux new-session -s AuthenTicket` => create tmux session (need run in the session, so that after we disconnect from AWS EC2, it will still be running)
- `tmux attach -t AuthenTicket` => attach tmux session
  - `ctrl + b and d` => detach tmux session
  - Note: don't terminate the session.
- cd to `AuthenTicket` folder
  - `docker compose up` => to run the docker image (remember to update the images if needed)
