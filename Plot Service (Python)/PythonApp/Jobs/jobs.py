import urllib3
import json
from django.conf import settings
from PythonApp.Utils.ServiceFactory import ServiceFactory


class TeamsWebhookException(Exception):
    """custom exception for failed webhook call"""
    pass


class ConnectorCard:
    def __init__(self, hook_url, http_timeout=60):
        self.__http = urllib3.PoolManager()
        self.__payload = {}
        self.__hook_url = hook_url
        self.__http_timeout = http_timeout

    def text(self, message_text):
        """
        sets text to teams message
        :param message_text: text of the message
        :return:
        """
        self.__payload["text"] = message_text
        return self

    def title(self, message_title):
        """
        sets title to teams message
        :param message_title: title of the message
        :return:
        """
        self.__payload["title"] = message_title
        return self

    def send(self):
        """
        sends message to teams channel
        :return:
        """
        headers = {"Content-Type":"application/json"}
        r = self.__http.request(
            'POST',
            f'{self.__hook_url}',
            body=json.dumps(self.__payload).encode('utf-8'),
            headers=headers, timeout=self.__http_timeout)
        if r.status == 200:
            return True
        else:
            print("exception")
            raise TeamsWebhookException(r.reason)


def send_message_to_teams(title, text):
    """
    compose and send message to teams channel
    :param title: message title
    :param text: message text
    :return:
    """
    teams_channel_connection_url = settings.TEAMSCHANNEL_CONNECTION_URL
    teams_message = ConnectorCard(teams_channel_connection_url)
    teams_message.title(title)
    teams_message.text(text)
    teams_message.send()


def get_new_data():
    """
    get the report about retrieved new data from databricks and send it to teams channel
    :return:
    """
    service = ServiceFactory.initialise()
    message = service.get_full_report()
    send_message_to_teams("Data retrieved from Databricks!", message)

