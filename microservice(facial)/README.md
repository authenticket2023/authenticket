#### need to install python@3.10 and below in order to run npm install without error
    - install pyenv => brew install pyenv
    
##### setup shell environment for pyenv (https://github.com/pyenv/pyenv#set-up-your-shell-environment-for-pyenv)
    - in your terminal run : (for mac)
    echo 'export PYENV_ROOT="$HOME/.pyenv"' >> ~/.bashrc
    echo 'command -v pyenv >/dev/null || export PATH="$PYENV_ROOT/bin:$PATH"' >> ~/.bashrc
    echo 'eval "$(pyenv init -)"' >> ~/.bashrc

##### pyenv commands
    -pyenv versions
    -pyenv global 2.7.18 => change your python version

#### for npm install canvas 
  - Mac OS : brew install pkg-config cairo pango libpng jpeg giflib librsvg pixman
  - Windows : https://github.com/Automattic/node-canvas/wiki/Installation:-Windows

npx tsx faceRecognition.ts => to run one file 

#### Swagger (http://localhost:8000/doc/)
To generate routing for new file, to show in the swagger UI
- Modify the endpointsFiles path in swagger.ts
- `npm run swagger-autogen` => it will generate swagger_output_autogen.json
- copy everything in the path, paste it to swagger_output.json, and modify when necessary

##### Debugging
- `npm rebuild @tensorflow/tfjs-node --build-from-source` => for backend, as tfjs-node need this cmd to work
- If still cannot install tfjs-node, can use docker hot reload 
    - cd microservice(facial)
    - `docker compose up` => to update code, just stop the compose and compose up again