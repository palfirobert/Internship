# Introduction 
This app lets the battery user login into his account.
In homepage there is a header form where the user can choose the type of plots, the tenant along with the batteries he wants to see details of.
Plots are displayed in a gallery.


# Getting Started
####1. Installation process
- clone the project from Azure
- to run the future commands you need to have Node version 20.4.0 ( with npm version 9.7.2)
- if you can't run commands in angular inside the project run the next command:
  - npm install -g @angular/cli
- run ng add @angular/material
  - then you select: Custom, No, Include and enable animations
        

####2. Software dependencies
- run these commands in the terminal:
  - cd AngularUserInterface (if you are not already in the project)
  - npm install (to install all the dependencies needed)
  
####3. Latest releases
- v0.1: receive 
- v0.2: login page
- v0.3: homepage + header with tabs + footer
- v0.4: image gallery for displaying the plots

####4. API references  
- https://picsum.photos/200/300/?random  (api for reading random pictures)
- http://localhost:4200/ (user interface)
- http://localhost:4200/login (login page)
- http://localhost:4200/homepage/sohc (homepage for sohc plots)
# Build and Test
- Method 1: Intelij configuration  
Add new configuration --> npm --> select the impicit package.json from the select --> Command: run --> Scripts: start
- Method 2: terminal  
npm start
  


# Contribute
TODO: Explain how other users and developers can contribute to make your code better. 

My code can be optimised, divided in directories in a more legible way and improving the UI elements.
