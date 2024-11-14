# Introduction 
This is a middle service for the communication between Angular and Python.

# Getting Started
1. Install
    - download maven 3.9.3
    - have maven directory path in M2_HOME environment variable (C:\Program Files\apache-maven-3.9.3)
    - have maven bin directory path in Path environment variable (C:\Program Files\apache-maven-3.9.3\bin)
    - optional: go to File -> Settings -> Build, Execution, Deployment -> Build Tools -> Maven and set Maven home path same as M2_HOME variable

    
2. Software dependencies
   - all dependencies you need are in pom.xml file so make sure you have that


3. Latest releases
   - v0.1 - first release: makes http request to python module and display in fe, displays in fe an image sent in byteArray format
   - v0.2 - security added, please run the script from the resources file, editdatabase.txt in postgres in order to get the latest version of the database thank you 
   - v0.3 - added endpoint to retrieve plots     
            IMPORTANT CHANGES:    
     - Make sure to add your credentials in aplication.properties (username/password)  
       if you want to test the new endpoint add to postman a post request to this url: http://localhost:8080/api/blob/plots  
       Example of request:  
       {  
       "tenantId": "1aa9d5e8-a182-414b-be97-1791fd89fc27",  
       "batteryIds": ["0ENPE011011000B8B0000030","0ENPE011011000B8S0000019"],  
       "plotType": "sohc"  
       }

-don't forget the authentication header
-don't forget to open python service  
     - v0.4 - created endpoints to return: 
                    -   all tenants
                    -   list of batteries (id) for a specified tenant

    -v0.5 !!!IMPORTANT!!!
    Spring boot profiles:
    - ADDED "LOCAL" PROFILE =>> ADD ANOTHER SPRING BOOT CONFIGURATION
    EDIT CONFIGURATION -> ADD NEW CONFIGURATION -> SPRING BOOT -> CHANGE THE NAME EX: JavaApplication-Local
    -> ADD MAIN CLASS -> ADD USER ENVIRONMENT VARIABLE: Name: spring.profiles.active , Value: local
    =>> THIS CONFIGURATION SHOULD BE USED AS DEFAULT FOR DEVELOPMENT (application-local.yml)
    
    - MADE THE "DEFAULT" PROFILE TO BE THE ONE USED IN DEPLOYMENT (application.yml)
    - IMPORTANT: WHEN ADDING LOCAL VARIABLES IN THE YML FILES ADD THE CODE IN BOTH 
    PROFILES AND RESPECT THE NAMING CONVENTION BETWEEN THEM
    - ADDED MAVEN PROFILES , IF YOU WANT TO RUN MAVEN COMMANDS FOR "LOCAL" CHECK THE COMBOBOX NAMED
    "local" OR IF YOU WANT TO RUN COMMANDS FOR DEPLOYMENT CHECK DEPLOYMENT

    - v0.6 - created mail service 
    
    -v0.7
        - app deployed on azure link to the site :
        - pipeline made to automaticaly deploy on merge https://angularuserinterface.azurewebsites.net/homepage
4. API references
   - for the image: http://localhost:8080/api/charts/image
   - for python (make sure the python service runs): http://localhost:8080/api/strings/hello
   - get all tenants: http://localhost:8080/api/v1/tenants
   - get tenant batteries: http://localhost:8080/api/v1/battery/tenant?tenantId=1aa9d5e8-a182-414b-be97-1791fd89fc27
# Build and Test
TODO: Describe and show how to build your code and run the tests. 

# Contribute
TODO: Explain how other users and developers can contribute to make your code better. 

If you want to learn more about creating good readme files then refer the following [guidelines](https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops). You can also seek inspiration from the below readme files:
- [ASP.NET Core](https://github.com/aspnet/Home)
- [Visual Studio Code](https://github.com/Microsoft/vscode)
- [Chakra Core](https://github.com/Microsoft/ChakraCore)