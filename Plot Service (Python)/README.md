# Introduction 
Objectives and motivation behind this project.
    This project is made to be the python server that will generate the plots for the battery data that will be later
shown to the client on the web page. App connects to blobstorage and saves the plots in the specified folder

# Getting Started
TODO: Guide users through getting your code up and running on their own system. In this section you can talk about:
1.	Installation process
    run the command :   pip install -r requirements.txt
      
 database initialisation  
 - make sure you have the latest version of postgres database system and pgadmin4 
   you can download it form here https://www.enterprisedb.com/downloads/postgres-postgresql-downloads
   now open pgadmin and create a new database with the name : batterydatabse 
   must have that name in order to work
   copy the script from the resource file : databasecreationquerry.sql and run it on the new created database
- create a new python configuration and add to the "Parameters" field the command "runserver", "Script path"
    select your manage.py file from your project, "Use specified interpreter" Phyton 3
    (Be aware of the absolute path from plot method from Services.py file)
2. Latest releases
      v0.1 - This is the first release all it can do is say hello.
      v0.2 - added new feature like : 
            1. a new architecture with domain repository and service classes + utils
            2. added functionality to save from csv data for sohcs and charge profiles 
            3. repo interfaces with save functionality
      v0.3 - The plots are saved in blobstorage
      v0.4 - created plots for SOHC_3_WORST_SOHCS_AGAINST_TIME and SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME
      v0.5 - new plot times are available like eq_params_against_time, soc and current for fast and protected charge profile against time worst cell index against time
      v0.6 - added a cron job to retrieve data from databricks and update database with new data once every 6 hours
      v0.7 - creates report about the new data retrieved from databricks and sends it to teams channel
      v0.8 - added security to endpoints the super user for testing is in the utilizatorpythonmrr.txt

3. API references
 - ^ http://127.0.0.1:8000/hello ^ this is the only api here, it says hello for testing is both a get and post method
      it returns data in json format

 - ^ http://127.0.0.1:8000/plot ^ with body 
   
       {
          "tenantId": "1aa9d5e8-a182-414b-be97-1791fd89fc27",
          "batteryIds": ["0ENPE011011000B8B0000030","0ENPE011011000B8S0000019"],
          "plotType": "SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME"
       }

    should return

       {
           "message": "Data received successfully",
           "locations": [
             "/1aa9d5e8-a182-414b-be97-1791fd89fc27/0ENPE011011000B8B0000030/sohc/sohc_0ENPE011011000B8B0000030.png",
             "/1aa9d5e8-a182-414b-be97-1791fd89fc27/0ENPE011011000B8S0000019/sohc/sohc_0ENPE011011000B8S0000019.png]
       }

        or with the other body : 
        {
          "tenantId": "1aa9d5e8-a182-414b-be97-1791fd89fc27",
          "batteryIds": ["BBA76DB39A58E1CF4823220CF2D5F113"],
          "plotType": "CHARGE_PROFILE_PROTECTED_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE"
       }
   and you should receive this :

   "message": "Data received successfully",
   "locations": [
   [
   "/1aa9d5e8-a182-414b-be97-1791fd89fc27/BBA76DB39A58E1CF4823220CF2D5F113/charge_profile/charge_profile_protected_current_against_soc_for_each_temperature_0_°C_BBA76DB39A58E1CF4823220CF2D5F113.png",
   ..... and another 55 for all temperatures
   ]
   ]
   }
   
        or 
        {
        "tenantId": "1aa9d5e8-a182-414b-be97-1791fd89fc27",
        "batteryIds": ["BBA76DB39A58E1CF4823220CF2D5F113"],
        "plotType": "CHARGE_PROFILE_FAST_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE"
        }   

    and you receive :
   "message": "Data received successfully",
   "locations": [
   [
   "/1aa9d5e8-a182-414b-be97-1791fd89fc27/BBA76DB39A58E1CF4823220CF2D5F113/charge_profile/charge_profile_fast_current_against_soc_for_each_temperature_0_°C_BBA76DB39A58E1CF4823220CF2D5F113.png",
   ...... and another 55 for all temperatures
   ]
   ]
   }
   or this call for plotting eq params
   {
   "tenantId": "1aa9d5e8-a182-414b-be97-1791fd89fc27",
   "batteryIds": ["0ENPE011011000B8B0000030","0ENPE011011000B8S0000019"],
   "plotType": "SOHC_EQ_PARAMS_TIME"
   }
   it should return this

{
"message": "Data received successfully",
"locations": [
"/1aa9d5e8-a182-414b-be97-1791fd89fc27/0ENPE011011000B8B0000030/sohc/sohc_eq_params_time_0ENPE011011000B8B0000030.png",
"/1aa9d5e8-a182-414b-be97-1791fd89fc27/0ENPE011011000B8S0000019/sohc/sohc_eq_params_time_0ENPE011011000B8S0000019.png"
]
}

    you can use this call to plot the top 3 worst cell indexed from sohcs
{
"tenantId": "1aa9d5e8-a182-414b-be97-1791fd89fc27",
"batteryIds": ["0ENPE011011000B8B0000030","0ENPE011011000B8S0000019"],
"plotType": "SOHC_3_WORST_CELL_INDEX_AGAINST_TIME"
}

and it will return

{
"message": "Data received successfully",
"locations": [
"/1aa9d5e8-a182-414b-be97-1791fd89fc27/0ENPE011011000B8B0000030/sohc/sohc_3_worst_cell_index_against_time_0ENPE011011000B8B0000030.png",
"/1aa9d5e8-a182-414b-be97-1791fd89fc27/0ENPE011011000B8S0000019/sohc/sohc_3_worst_cell_index_against_time_0ENPE011011000B8S0000019.png"
]
}
        

# Build and Test
to test it all you need is to open cmd or the intellij terminal in the PlotService folder !!!! and type : python manage.py runserver --noreload

    add DJANGO_ENV=local as environment variable when running
        

# Contribute
    Teamwork dreamwork

#Important note !!!!
    Python dose not have a clear naming convention so to keep track of names and make it easyer for everyone
to use your code you need to keep in mind the following : 
    All python packages are named starting with capital letter with no spaces and each word starts with capital
classes have the same convention as packages
functions need to be written only with lowercase characters and instead of spaces you need to put underscore
variables are the save as functions
have fun

If you want to learn more about creating good readme files then refer the following [guidelines](https://docs.microsoft.com/en-us/azure/devops/repos/git/create-a-readme?view=azure-devops). You can also seek inspiration from the below readme files:
- [ASP.NET Core](https://github.com/aspnet/Home)
- [Visual Studio Code](https://github.com/Microsoft/vscode)
- [Chakra Core](https://github.com/Microsoft/ChakraCore)