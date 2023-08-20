# Authenticket Frontend (React)

## Get Started 
### Local
- Install Node Version Manager
    - `nvm install 18` => we are using node 18 
    - `nvm ls` => to check node version installed in your machine
    - `nvm alias default 18` => to set default node version for your machine
    - `nvm use 18` => switch your node version to 18 for your current directory
- Install dependency (cd to 'authenticket/frontend')
    - `npm install` => to install required packages
        - `npm install --legacy-peer-deps` => if have error
    - `npm start` => to run frontend (localhost:3000)
### Docker
- cd to 'authenticket/frontend'
    - Build the image : `docker build -t authenticket-frontend .`
    - Run the image : `docker run -dp 127.0.0.1:3000:3000 authenticket-frontend`