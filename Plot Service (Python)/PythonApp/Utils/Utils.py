__package__ = None

import os

from django.conf import settings
'''
Utilis package, with usefull custom classes for your needs
contains:
    DataBaseUtils :
        * read_from_csv(filename)

Important ! put all utils here
'''

import csv
from configparser import ConfigParser
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker


'''
class with all database utils
'''


class ConfigUtils:
    connection = None

    # reads data from a csv and return the labels + data
    # input :
    #       filename : string referencing the csv fle from witch you need to read data
    # output:
    #       data : matrix with the data
    #       data_names : array of strings with the labels ascoiated with the data
    @staticmethod
    def read_from_csv(csv_file):
        data = []
        data_names = []

        with open(csv_file) as file:
            csv_reader = csv.reader(file, delimiter=',')
            line_count = 0
            for row in csv_reader:
                if line_count == 0:
                    data_names = row
                else:
                    data.append(row)

                line_count += 1

        return data, data_names

    # method that returns a session for the db, must be closed
    @staticmethod
    def get_session():

        # "postgresql+psycopg2://postgres:1337@localhost:5432/batterydatabase"
        database_settings = settings.DATABASE['default']
        connection_string = f"postgresql+psycopg2://{database_settings['USER']}:{database_settings['PASSWORD']}@{database_settings['HOST']}:{int(database_settings['PORT'])}/{database_settings['NAME']}"

        print(f"connecting to {database_settings['NAME']}.")
        if ConfigUtils.connection is None:
            ConfigUtils.connection = create_engine(connection_string, echo=True)
        Session = sessionmaker(bind=ConfigUtils.connection)
        return Session()

    @staticmethod
    def get_config(filename='../resources/config.ini', section='postgresql'):
        # create a parser
        parser = ConfigParser()
        # read config file
        parser.read(filename)

        # get section and update dictionary with connection string key:value pairs
        db = {}
        if section in parser:
            for key in parser[section]:
                db[key] = parser[section][key]
        else:
            raise Exception(
                'Section {0} not found in the {1} file'.format(section, filename))
        return db


