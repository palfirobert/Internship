FROM node:20.4.0 as node

WORKDIR /app

COPY package.json package-lock.json

COPY . .

RUN npm config set strict-ssl=false
RUN npm config set progress=false
RUN npm config set proxy http://par9clj:RbrtPalf2001@rb-proxy-de.bosch.com:8080
RUN npm config set https-proxy http://par9clj:RbrtPalf2001@rb-proxy-de.bosch.com:8080
RUN npm config ls -l
RUN npm install --legacy-peer-deps

RUN npm install -g @angular/cli



EXPOSE 4200

CMD ["ng", "serve", "--host=0.0.0.0","--disable-host-check"]
