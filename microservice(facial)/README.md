# Run facial microservice
## Docker
- cd microservice(facial)
- `docker compose up` => to update code, just stop the compose and compose up again

## Local (Don't recommend this, as it was troublesome to install @tensorflow/tfjs-node)
- `npm install`, if have error try :
    - `npm rebuild @tensorflow/tfjs-node --build-from-source` => for backend, as tfjs-node need this cmd to work