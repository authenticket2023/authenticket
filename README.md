# Authenticket
## prerequisite
- Frontend (React) : Node V18 (Typescript)
- Backend(Spring Boot) : Java JDK 17 
  - You need to add `secrets.properties` to `backend/src/main/resources/` . Please contact us to get the file.
## Docker
### build and run both frontend & backend
- At root directory => `docker compose up`
### frontend
- cd to 'authenticket/frontend'
  - Build the image : `docker build -t authenticket-frontend .`
  - Run the image : `docker run -dp 127.0.0.1:3000:3000 authenticket-frontend`

### backend
- cd to 'authenticket/backend'
  - Generate jar file : `mvn clean install`  => this will build spring boot application into jar
  - Build the image : `docker build -t authenticket-backend .` => name of the jar file need match in the Dockerfile
  - Run the image : `docker run -dp 127.0.0.1:8080:8080 authenticket-backend`

### Run buildDocker.sh (replace with your own docker ID)
- cd to 'authenticket/backend'
  - `chmod +x buildDocker.sh` => make the script executable
  - `./buildDocker.sh` => run the script

## Run in AWS EC2
- `tmux new-session -s AuthenTicket` => create tmux session (need run in the session, so that after we disconnect from AWS EC2, it will still be running)
- `tmux attach -t AuthenTicket` => attach tmux session
  - `ctrl + b and d` => detach tmux session
  - Note: don't terminate the session.
- cd to `AuthenTicket` folder
  - `docker compose up` => to run the docker image (remember to update the images if needed)