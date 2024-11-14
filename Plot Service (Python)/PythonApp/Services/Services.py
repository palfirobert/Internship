__package__ = None

from datetime import datetime
from io import BytesIO
import base64
import requests
from PythonApp.Domain.domain import Battery, Tenant, ChargeProfiles, SOHCS, PlotType, DataType
from PythonApp.Repository.Repositories import BatteryRepository, TenantRepository, ChargeProfilesRepository, \
    SOHCSRepository
from PythonApp.Utils.DataProcessing import DataProcessing as dataProcess
import matplotlib.pyplot as plt
import json
from azure.storage.blob import BlobServiceClient
from django.conf import settings

from PythonApp.Utils.Utils import ConfigUtils

'''
The service that manages all the repos
'''


def get_sohc(index, list_sohcs,
             plot_type):  # method that returns a sohc value from the list customizable after plot type
    # takes the sohc cell and returns it as a list
    sohcs = dataProcess.string_to_list(list_sohcs)

    if plot_type == PlotType.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME:
        # return the value of the minId index from the sohc list
        return sohcs[index]
    elif plot_type == PlotType.SOHC_3_WORST_SOHCS_AGAINST_TIME:
        sohcs.sort()
        return sohcs[index]


def get_axis_sohc(list_of_sohcs, plot_type):
    # declare empty lists
    sohc_time_stamps = []
    if plot_type == PlotType.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME:
        sohc_values = []
        # populate the lists
        for sohc in list_of_sohcs:
            sohc_time_stamps.append(sohc['timestamp'])
            sohc_values.append(get_sohc(sohc['min_sohc_cell_id'] - 1, sohc['sohc'], plot_type))

        # return the timestamp and value of sohcs of the battery
        return sohc_time_stamps, sohc_values
    elif plot_type == PlotType.SOHC_3_WORST_SOHCS_AGAINST_TIME:
        sohc_worst_1 = []
        sohc_worst_2 = []
        sohc_worst_3 = []
        for sohc in list_of_sohcs:
            sohc_time_stamps.append(sohc['timestamp'])
            sohc_worst_1.append(get_sohc(0, sohc['sohc'], plot_type))
            sohc_worst_2.append(get_sohc(1, sohc['sohc'], plot_type))
            sohc_worst_3.append(get_sohc(2, sohc['sohc'], plot_type))
        return sohc_time_stamps, sohc_worst_1, sohc_worst_2, sohc_worst_3

    elif plot_type == PlotType.SOHC_3_WORST_CELL_INDEX_AGAINST_TIME:
        first_worst_cell_index = []
        second_worst_cell_index = []
        third_worst_cell_index = []
        for sohc in list_of_sohcs:
            sohc_time_stamps.append(sohc['timestamp'])
            sohc_data = json.loads(sohc['sohc'])
            first_worst = sohc_data[0]
            second_worst = sohc_data[0]
            third_worst = sohc_data[0]
            for value in sohc_data:
                if value < first_worst:
                    third_worst = second_worst
                    second_worst = first_worst
                    first_worst = value
            first_worst_cell_index.append(sohc_data.index(first_worst))
            second_worst_cell_index.append(sohc_data.index(second_worst))
            third_worst_cell_index.append(sohc_data.index(third_worst))

        return sohc_time_stamps, first_worst_cell_index, second_worst_cell_index, third_worst_cell_index
    else:
        return "Invalid data :("


class Service:

    # constructor method
    # param:
    #   batteryrepo - BatteryRepository
    #   tenantrepo - TenantRepository
    #   chargerofilesrepo - ChargeProfilesRepository
    #   sohcsrepo - SOHCSRepository
    def __init__(self, batteryrepo, tenantrepo, chargeprofilesrepo, sohcsrepo):
        self.battery_repo: BatteryRepository = batteryrepo
        self.tenant_repo: TenantRepository = tenantrepo
        self.charge_profiles_repo: ChargeProfilesRepository = chargeprofilesrepo
        self.sohcs_repo: SOHCSRepository = sohcsrepo

    # method that saves the data from e recived csv  into the db
    # params:
    #   filename - String reprezenting the path to the file that you want to save from
    def insert_data_from_csv_into_db(self, filename):
        print("1")
        data, data_names = ConfigUtils.read_from_csv(filename)
        print(data, data_names)
        if filename == "../resources/charge profiles databricks.csv":
            self.__insert_from_charge_profiles(data, data_names)

        if filename == "../resources/charge profiles.csv":
            self.__insert_from_charge_profiles(data, data_names)

        if filename == "../resources/sohcs.csv":
            self.__insert_for_sohcs(data, data_names)

        if filename == "../resources/sohcs databricks.csv":
            self.__insert_for_sohcs(data, data_names)

    # insert from sohcs cvs method
    # private function that inserts only from a sohcs cvs
    # param:
    #      data - the data that will be saved into the db
    #      data_names - the labelds for the data
    def __insert_for_sohcs(self, data, data_names):
        print(2)
        for row in data:
            battery_id = row[0]
            battery_type = row[1]
            tenant_id = row[2]
            algo_version = row[3]
            delta_sohcs_flags = row[4]
            sohc = row[5]
            eq_param = row[6]
            min_sohc_cell_id = row[7]
            status = row[8]
            # print(float(row[9]))
            time_from_csv = float(row[9]) / 1000.0
            timestamp = datetime.fromtimestamp(time_from_csv)
            tenant = Tenant(tenant_id)
            battery = Battery(battery_id, battery_type, tenant_id)

            sohc = SOHCS(algo_version, delta_sohcs_flags, sohc, eq_param, min_sohc_cell_id, status, timestamp,
                         battery_id)
            try:
                self.tenant_repo.save(tenant)
            except Exception:
                print("erroare la tenant")
            try:
                self.battery_repo.save(battery)
            except Exception:
                print("erroare la baterie")

            self.sohcs_repo.save(sohc)

    # insert from charge profiles cvs method
    # private function that inserts only from a charge profiles cvs
    # param:
    #      data - the data that will be saved into the db
    #      data_names - the labelds for the data
    def __insert_from_charge_profiles(self, data, data_names):
        print(3)
        for row in data:
            battery_id = row[0]
            timestamp = row[1]
            tenant_id = row[8]
            fast_temperature = row[2]
            fast_soc = row[3]
            fast_current = row[4]
            protected_temperature = row[5]
            protected_soc = row[6]
            protected_current = row[7]
            time_from_csv = float(timestamp) / 1000.0
            timestamp = datetime.fromtimestamp(time_from_csv)

            battery = Battery(battery_id, "", tenant_id)
            tenant = Tenant(tenant_id)
            charge_profile = ChargeProfiles(timestamp, fast_temperature, fast_soc, fast_current, protected_temperature,
                                            protected_soc, protected_current, battery_id)

            try:
                self.tenant_repo.save(tenant)
            except Exception:
                print("erroare la tenant")
            try:
                self.battery_repo.save(battery)
            except Exception:
                print("erroare la baterie")
            # try:
            #    self.sohcs_repo.save(sohc)
            # except Exception:
            #    print("---------------------------------------------------- cred ca aici e baiu")
            self.charge_profiles_repo.save(charge_profile)

    def plot(self, tenant, battery_ids, plot_type):

        plot_locations = []

        if plot_type.name.__contains__("SOHC"):
            plot_locations = self.plot_sohcs(tenant, battery_ids, plot_type)
        elif plot_type.name.__contains__("CHARGE_PROFILE"):
            plot_locations = self.__plot_charge_profiles(tenant, battery_ids, plot_type)


        else:
            print("Plotting not found :(")

        return plot_locations

    def plot_sohcs(self, tenant, battery_ids, plot_type):

        plot_locations = []

        for battery in battery_ids:
            data = self.sohcs_repo.find_all_sohcs_by_battery_id(battery)
            # verify if the id exists (just in case)
            if data:
                # tenantId/batteryId/sohc/hfahjhadjvhasj.PNG
                if plot_type == PlotType.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME:
                    plot_locations.append(self.plot_sohc_battery(tenant, battery, data, plot_type))
                elif plot_type == PlotType.SOHC_3_WORST_SOHCS_AGAINST_TIME:
                    plot_locations.append(self.plot_worst_first_second_third_sohc(tenant, battery, data, plot_type))
                elif plot_type == PlotType.SOHC_EQ_PARAMS_AGAINST_TIME:
                    plot_locations.append(self.plot_eq_params_time(tenant, battery, data, plot_type))
                elif plot_type == PlotType.SOHC_3_WORST_CELL_INDEX_AGAINST_TIME:
                    plot_locations.append(self.plot_3_worst_cell_index_against_time(tenant, battery, data, plot_type))
            else:
                plot_locations.append(battery + ' invalid')

        return plot_locations

    def plot_3_worst_cell_index_against_time(self, tenant_id, battery_id, data, plot_type):
        x_axis, y1, y2, y3 = get_axis_sohc(data, plot_type)
        plt.figure(figsize=(10, 6))
        plt.style.use("Solarize_Light2")
        plt.xlabel('Timestamp')
        plt.ylabel('Sohc_index')
        plt.title(f'SOHC PLOT FOR BATTERY WITH ID {battery_id}')
        plt.plot(x_axis, y1, label='1st worst cell index')
        plt.plot(x_axis, y2, label='2nd worst cell index')
        plt.plot(x_axis, y3, label='3rd worst cell index')
        plt.legend()
        # Capture the plot as bytes using BytesIO
        buffer = BytesIO()
        plt.savefig(buffer, format="png")
        buffer.seek(0)

        plt.show()
        return self.save_to_blobstorage(tenant_id, battery_id, buffer, plot_type)

    def plot_worst_first_second_third_sohc(self, tenant, battery, data, plot_type):
        x_axis, y1, y2, y3 = get_axis_sohc(data, plot_type)
        plt.figure(figsize=(10, 6))
        plt.style.use("Solarize_Light2")
        plt.xlabel('Timestamp')
        plt.ylabel('SOHC')
        plt.title(f'SOHC PLOT FOR BATTERY WITH ID {battery}')
        plt.plot(x_axis, y1, label='1st worst sohc')
        plt.plot(x_axis, y2, label='2nd worst sohc')
        plt.plot(x_axis, y3, label='3rd worst sohc')
        plt.legend()
        # Capture the plot as bytes using BytesIO
        buffer = BytesIO()
        plt.savefig(buffer, format="png")
        buffer.seek(0)

        plt.show()
        return self.save_to_blobstorage(tenant, battery, buffer, plot_type)

    def plot_sohc_battery(self, tenant, battery, data, plot_type):

        x_axis, y_axis = get_axis_sohc(data, plot_type)
        plt.figure(figsize=(10, 6))
        plt.style.use("Solarize_Light2")
        plt.xlabel('Timestamp')
        plt.ylabel('SOHC')
        plt.title(f'SOHC PLOT FOR BATTERY WITH ID {battery}')
        plt.plot(x_axis, y_axis)

        # Capture the plot as bytes using BytesIO
        buffer = BytesIO()
        plt.savefig(buffer, format="png")
        buffer.seek(0)

        plt.show()
        return self.save_to_blobstorage(tenant, battery, buffer, plot_type)

    def save_to_blobstorage(self, tenant, battery, buffer, plot_type, temperature=""):
        # Connect to storage
        blobstorage_settings = settings.BLOBSTORAGE['default']
        connection_string = blobstorage_settings['CONNECTION_STRING']
        container_name = blobstorage_settings['CONTAINER']

        blob_service_client = BlobServiceClient.from_connection_string(connection_string)

        container = None
        # Connect to container
        if plot_type.__str__().__contains__("SOHC"):
            container = f"{container_name}/{tenant}/{battery}/sohc"
        if plot_type.__str__().__contains__("CHARGE_PROFILE"):
            if plot_type.__str__().__contains__("CHARGE_PROFILE_PROTECTED"):
                container = f"{container_name}/{tenant}/{battery}/charge_profile/charge_profile_protected"
            elif plot_type.__str__().__contains__("CHARGE_PROFILE_FAST"):
                container = f"{container_name}/{tenant}/{battery}/charge_profile/charge_profile_fast"

        if container == None:
            raise Exception("Can t find suitable container fro this plot")
        image_container = blob_service_client.get_container_client(container)

        subcontiner_sufix = None
        # Upload image
        if plot_type.__str__().__contains__("SOHC"):
            subcontiner_sufix = "sohc"
            blob_name = f"{plot_type.name.lower()}_{battery}.png"
        elif plot_type.__str__().__contains__("CHARGE_PROFILE"):
            subcontiner_sufix = "charge_profile"
            if plot_type.__str__().__contains__("FAST"):
                subcontiner_sufix += "/charge_profile_fast"
            else :
                subcontiner_sufix += "/charge_profile_protected"
            blob_name = f"{plot_type.name.lower()}_{temperature}_°C_{battery}.png"

        image_container.upload_blob(name=blob_name, data=buffer.getvalue(), overwrite=True)

        location = f"/{tenant}/{battery}/{subcontiner_sufix}/{blob_name}"
        print("Saved to blobsotrage " + location)
        return location

    # de aicia e cu charge profiles
    def __plot_soc_and_i_fast(self, data, tenant, battery_id, temperature, plot_type):
        plt.figure(figsize=(10, 6))
        plt.style.use("Solarize_Light2")
        plt.xlabel('SOC(%)')
        plt.ylabel('I(Amp)')
        plt.title(f'FCP PLOT FOR BATTERY WITH ID {battery_id} at {temperature}°C')

        for charge_profile in data:
             plt.plot(json.loads(charge_profile['fast_soc']), json.loads(charge_profile['fast_current'])[temperature],
                      label=charge_profile['timestamp'])
        plt.legend(loc="upper right")

         # Capture the plot as bytes using BytesIO
        buffer = BytesIO()
        plt.savefig(buffer, format="png")
        buffer.seek(0)

        plt.show()
        return self.save_to_blobstorage(tenant, battery_id, buffer, plot_type, temperature)

    def __plot_soc_and_i_protected(self, data, tenant, battery_id, temperature, plot_type):
        plt.figure(figsize=(10, 6))
        plt.style.use("Solarize_Light2")
        plt.xlabel('SOC(%)')
        plt.ylabel('I(Amp)')
        plt.title(f'PCP PLOT FOR BATTERY WITH ID {battery_id} at {temperature}°C')

        for charge_profile in data:
            plt.plot(json.loads(charge_profile['protected_soc']),
                     json.loads(charge_profile['protected_current'])[temperature],
                     label=charge_profile['timestamp'])
        plt.legend(loc="upper right")

        # Capture the plot as bytes using BytesIO
        buffer = BytesIO()
        plt.savefig(buffer, format="png")
        buffer.seek(0)

        plt.show()
        return self.save_to_blobstorage(tenant, battery_id, buffer, plot_type, temperature)

    def __plot_charge_profiles(self, tenant, battery_ids, plot_type):
        plot_locations = []

        for battery in battery_ids:
            # tenantId/batteryId/sohc/hfahjhadjvhasj.PNG
            plot_locations+=self.plot_soc_and_i(tenant, battery, plot_type)

        return plot_locations

    def plot_soc_and_i(self, tenant, battery_id, plot_type):
        plot_locations = []
        print("ploting soc and i for battery " + battery_id)
        data = self.charge_profiles_repo.find_all_by_battery_id(battery_id)

        for i in range(len(json.loads(data[0]['fast_temperature']))):
            print("ploting the plot " + i.__str__())
            if plot_type.name.__contains__("CHARGE_PROFILE_FAST"):
                print("ploting for fast current")
                plot_locations.append(
                    self.__plot_soc_and_i_fast(data, tenant, battery_id, json.loads(data[0]['fast_temperature'])[i],
                                               plot_type))
            else:
                print("ploting for portected current")
                plot_locations.append(
                    self.__plot_soc_and_i_protected(data, tenant, battery_id,
                                                    json.loads(data[0]['fast_temperature'])[i], plot_type))

        return plot_locations

    def __get_eq_params_axis(self, data):
        first_eq_element = {}
        second_eq_element = {}
        third_eq_element = {}

        first_eq_element['min'] = []
        first_eq_element['max'] = []
        first_eq_element['avg'] = []

        second_eq_element['min'] = []
        second_eq_element['max'] = []
        second_eq_element['avg'] = []

        third_eq_element['min'] = []
        third_eq_element['max'] = []
        third_eq_element['avg'] = []

        time = []

        for sohc in data:
            time.append(sohc['timestamp'])
            eq_param_array = json.loads(sohc['eq_param'])
            first_min_eq = 1000.0
            first_max_eq = 0.0
            first_avg_eq = 0.0

            second_min_eq = 1000.0
            second_max_eq = 0.0
            second_avg_eq = 0.0

            third_min_eq = 1000.0
            third_max_eq = 0.0
            third_avg_eq = 0.0

            for eq_data in eq_param_array:
                first = eq_data[0]
                second = eq_data[1]
                third = eq_data[2]

                if first < first_min_eq:
                    first_min_eq = first
                if first > first_max_eq:
                    first_max_eq = first
                first_avg_eq += first

                if second < second_min_eq:
                    second_min_eq = second
                if second > second_max_eq:
                    second_max_eq = second
                second_avg_eq += second

                if third < third_min_eq:
                    third_min_eq = third
                if third > third_max_eq:
                    third_max_eq = third
                third_avg_eq += third

            first_eq_element['min'].append(first_min_eq)
            first_eq_element['max'].append(first_max_eq)
            first_eq_element['avg'].append(first_avg_eq / 112)

            second_eq_element['min'].append(second_min_eq)
            second_eq_element['max'].append(second_max_eq)
            second_eq_element['avg'].append(second_avg_eq / 112)

            third_eq_element['min'].append(third_min_eq)
            third_eq_element['max'].append(third_max_eq)
            third_eq_element['avg'].append(third_avg_eq / 112)

        return first_eq_element, second_eq_element, third_eq_element, time

    def plot_eq_params_time(self, tenant, battery_id, data, plot_type):
        print("ploting eq params against time for battery " + battery_id)
        first_eq_data, second_eq_data, third_eq_data, time = self.__get_eq_params_axis(data)

        plt.style.use("Solarize_Light2")
        figure, axis = plt.subplots(1, 3, figsize=(40, 10))
        axis[0].set_xlabel('Date')
        axis[0].set_ylabel('EQ Values')
        axis[0].set_title(f'FIRST PARAM EQ PLOT FOR BATTERY WITH ID {battery_id}')

        axis[0].plot(time, first_eq_data['min'], label='min_first_eq')
        axis[0].plot(time, first_eq_data['max'], label='max_first_eq')
        axis[0].plot(time, first_eq_data['avg'], label='avg_first_eq')
        axis[0].legend(loc="upper right")

        axis[1].set_xlabel('Date')
        axis[1].set_ylabel('EQ Values')
        axis[1].set_title(f'SECOND PARAM EQ PLOT FOR BATTERY WITH ID {battery_id}')

        axis[1].plot(time, second_eq_data['min'], label='min_second_eq')
        axis[1].plot(time, second_eq_data['max'], label='max_second_eq')
        axis[1].plot(time, second_eq_data['avg'], label='avg_second_eq')
        axis[1].legend(loc="upper right")

        axis[2].set_xlabel('Date')
        axis[2].set_ylabel('EQ Values')
        axis[2].set_title(f'THIRD PARAM EQ PLOT FOR BATTERY WITH ID {battery_id}')

        axis[2].plot(time, third_eq_data['min'], label='min_third_eq')
        axis[2].plot(time, third_eq_data['max'], label='max_third_eq')
        axis[2].plot(time, third_eq_data['avg'], label='avg_third_eq')
        axis[2].legend(loc="upper right")
        # Capture the plot as bytes using BytesIO
        buffer = BytesIO()
        plt.savefig(buffer, format="png")
        buffer.seek(0)

        plt.show()
        return self.save_to_blobstorage(tenant, battery_id, buffer, plot_type)

    def get_existing_data(self, data_type):
        """
        FUNCTION THAT MAKES A DICTIONARY WITH THE KEY= BATTERY ID, AND THE VALUE IS A LIST WITH ALL THE SOHCS/CP OF
        :param data_type: the type of data to be retrieved; could be sohc or cp (charge profile)
        :return: returns dictionary with the existing data of specified type
        """
        db_existing_data = {}
        battery_ids = self.battery_repo.find_all_ids()
        if data_type == DataType.SOHC.name:
            for battery in battery_ids:
                data = self.sohcs_repo.find_all_sohcs_by_battery_id(battery)
                if data:
                    db_existing_data[battery] = data
        elif data_type == DataType.CP.name:
            for battery in battery_ids:
                data = self.charge_profiles_repo.find_all_by_battery_id(battery)
                if data:
                    db_existing_data[battery] = data
        return db_existing_data

    def get_tenant_ids_corresponding_to_timestamp(self, data, no_timestamp_column, no_tenant_id_column):
        """
        :param data: data from databricks
        :param no_timestamp_column: column number of timestamp in csv
        :param no_tenant_id_column: column number of tenant id in csv
        :return: dictionary with key = timestamp, value = tenant id
        """
        tenant_ids_corresponding_to_timestamp = {}
        for row in data:
            try:
                tenant_ids_corresponding_to_timestamp[
                    datetime.fromtimestamp(float(row[no_timestamp_column]) / 1000.0)].append(row[no_tenant_id_column])
            except Exception:
                tenant_ids_corresponding_to_timestamp[
                    datetime.fromtimestamp(float(row[no_timestamp_column]) / 1000.0)] = []
                tenant_ids_corresponding_to_timestamp[
                    datetime.fromtimestamp(float(row[no_timestamp_column]) / 1000.0)].append(row[no_tenant_id_column])
        return tenant_ids_corresponding_to_timestamp

    def create_databricks_data_dictionary(self, data_type, data):
        """

        :param data_type: the type of data to be retrieved; could be sohc or cp (charge profile)
        :param data: data retrieved from databricks
        :return: if sohc:   DICTIONARY WITH THE DATA RECEIVED FROM DATABRICKS KEY=BATTERY ID, VALUE = LIST OF DATA
                            dictionary of tenant ids corresponding to timestamp
                            dictionary of battery type corresponding to battery id
                 if cp:  DICTIONARY WITH THE DATA RECEIVED FROM DATABRICKS KEY=BATTERY ID, VALUE = LIST OF DATA
                         dictionary of tenant ids corresponding to timestamp
        """
        if data_type == DataType.SOHC.name:

            databricks_received_sohcs = {}
            battery_type = {}
            for row in data:
                try:
                    databricks_received_sohcs[row[0]].append(SOHCS(row[3], row[4], row[5], row[6], row[7], row[8],
                                                                   datetime.fromtimestamp(float(row[9]) / 1000.0),
                                                                   row[0]))
                except Exception:
                    databricks_received_sohcs[row[0]] = []
                    databricks_received_sohcs[row[0]].append(SOHCS(row[3], row[4], row[5], row[6], row[7], row[8],
                                                                   datetime.fromtimestamp(float(row[9]) / 1000.0),
                                                                   row[0]))
            tenant_ids_corresponding_to_timestamp = self.get_tenant_ids_corresponding_to_timestamp(data, 9, 2)

            for row in data:
                battery_type[row[0]] = row[1]

            return databricks_received_sohcs, tenant_ids_corresponding_to_timestamp, battery_type

        elif data_type == DataType.CP.name:
            databricks_received_cp = {}
            for row in data:
                try:
                    databricks_received_cp[row[0]].append(
                        ChargeProfiles(datetime.fromtimestamp(float(row[1]) / 1000.0), row[2], row[3], row[4], row[5],
                                       row[6], row[7], row[0]))
                except Exception:
                    databricks_received_cp[row[0]] = []
                    databricks_received_cp[row[0]].append(
                        ChargeProfiles(datetime.fromtimestamp(float(row[1]) / 1000.0), row[2], row[3], row[4], row[5],
                                       row[6], row[7], row[0]))

            tenant_ids_corresponding_to_timestamp = self.get_tenant_ids_corresponding_to_timestamp(data, 1, 8)

            return databricks_received_cp, tenant_ids_corresponding_to_timestamp

    def get_data_from_databricks(self, file_path, data_type):
        """
        MAKES THE CONNECTION WITH THE DATABRICKS AND CREATES A CSV WITH THE DATA AND RETURNS IT
        :param file_path: path of the csv in databricks
        :param data_type: the type of data to be retrieved; could be sohc or cp (charge profile)
        :return: csv with retrieved data from databricks
        """

        # Declare variables
        databricks_settings = settings.DATABRICKS['default']
        url = databricks_settings['BASE_URL']
        access_token = databricks_settings['ACCESS_TOKEN']
        headers = {"Authorization": f"Bearer {access_token}", "Accept": "application/json"}
        params = {"path": file_path}

        # Read file (daca vrei sa verifici fisierele pune /list)
        response = requests.get(url + '/read', headers=headers, params=params)

        bytes_content = base64.b64decode(response.json()['data'])
        csv_content = bytes_content.decode("utf-8")

        # Specify CSV file path to save to
        if data_type == DataType.SOHC.name:
            csv_file_path = "../resources/sohcs databricks.csv"
        elif data_type == DataType.CP.name:
            csv_file_path = "../resources/charge profiles databricks.csv"

        # Write CSV content to the specified CSV file
        with open(csv_file_path, "w", newline="") as csv_file:
            csv_file.write(csv_content)
        data, data_names = ConfigUtils.read_from_csv(csv_file_path)
        # self.insert_data_from_csv_into_db(csv_file_path)
        return data

    def get_dictionaries_differences(self, dictionary_1, dictionary_2):
        """

        :param dictionary_1: dictionary with old data
        :param dictionary_2: dictionary with both old and new data
        :return: dictionary with the differences between the two input dictionaries
        """
        differences = {}
        for key in dictionary_1.keys():
            for item in dictionary_1[key]:
                try:
                    if not dictionary_2.keys().__contains__(key) or not dictionary_2[key].__contains__(
                            item):
                        try:
                            differences[key].append(item)
                        except Exception:
                            differences[key] = []
                            differences[key].append(item)
                except Exception:
                    differences[key] = []
                    differences[key].append(item)
        return differences

    def save_tenant(self, tenant_id):
        """
        saves new tenant to database
        :param tenant_id: tenant id to be saved
        :return:
        """
        tenant = Tenant(tenant_id)
        try:
            self.tenant_repo.save(tenant)
        except Exception:
            print("Save tenant error")

    def save_battery(self, battery):
        """
        saves new battery to database
        :param battery: battery to be saved
        :return:
        """
        try:
            self.battery_repo.save(battery)
        except Exception:
            print("Save battery error")

    def update_database_with_databricks_data(self, differences, tenant_ids_corresponding_to_timestamp, data_type,
                                             battery_type=None):
        """
        UPDATES database AND ADDS ONLY THE NEW DATA retrieved FROM DATABRICKS
        :param differences: new data retrieved from databricks
        :param tenant_ids_corresponding_to_timestamp: dictionary of timestamps with corresponding tenant ids
        :param data_type: the type of data to be retrieved; could be sohc or cp (charge profile)
        :param battery_type: dictionary of battery type corresponding to battery id
        :return:
        """
        if data_type == DataType.SOHC.name:
            for key in differences.keys():
                for sohc in differences[key]:
                    tenant_id = tenant_ids_corresponding_to_timestamp[sohc['timestamp']]
                    self.save_tenant(tenant_id)

                    battery = Battery(sohc['battery_id'], battery_type[sohc['battery_id']], tenant_id)
                    self.save_battery(battery)

                    self.sohcs_repo.save(sohc)
        elif data_type == DataType.CP.name:
            for key in differences.keys():
                for cp in differences[key]:
                    tenant_id = tenant_ids_corresponding_to_timestamp[cp['timestamp']]
                    self.save_tenant(tenant_id)

                    battery = Battery(cp['battery_id'], "", tenant_id)
                    self.save_battery(battery)

                    self.charge_profiles_repo.save(cp)

    def manage_databricks_data(self, data_type):
        """
        updates the database with the new data retrieved from databricks
        :param data_type: the type of data to be retrieved; could be sohc or cp (charge profile)
        :return: new data retrieved from databricks
        """
        # Declare variables
        databricks_settings = settings.DATABRICKS['default']
        differences = {}
        if data_type == DataType.CP.name:
            db_existing_cp = self.get_existing_data(data_type)
            data = self.get_data_from_databricks(databricks_settings['CHARGE_PROFILES_PATH'],
                                                 data_type)
            databricks_received_cp, tenant_ids_corresponding_to_timestamp = self.create_databricks_data_dictionary(
                data_type,
                data)
            differences = self.get_dictionaries_differences(databricks_received_cp, db_existing_cp)
            self.update_database_with_databricks_data(differences, tenant_ids_corresponding_to_timestamp, data_type)

        elif data_type == DataType.SOHC.name:
            db_existing_sohcs = self.get_existing_data(data_type)

            data = self.get_data_from_databricks(databricks_settings['SOHCS_PATH'],
                                                 data_type)

            databricks_received_sohcs, tenant_ids_corresponding_to_timestamp, battery_type = self.create_databricks_data_dictionary(
                data_type, data)

            differences = self.get_dictionaries_differences(databricks_received_sohcs, db_existing_sohcs)
            self.update_database_with_databricks_data(differences, tenant_ids_corresponding_to_timestamp, data_type,
                                                      battery_type)

        return differences

    def get_full_report(self):
        """

        :return: report about new data retrieved from databricks
        """
        return self.get_report_data_type(DataType.SOHC.name, 'sohc') + "\n" \
               + self.get_report_data_type(DataType.CP.name, 'charge profile')

    def get_report_data_type(self, data_type, data_type_message):
        """

        :param data_type: the type of data to be retrieved; could be sohc or cp (charge profile)
        :param data_type_message: data type name to be written in message
        :return: report for one type of data
        """
        message = ""
        new_data = self.manage_databricks_data(data_type)
        if not new_data:
            message += "\nNo new data for " + data_type_message + "s.\n"
        else:
            tenant_ids = set()
            for key in new_data:
                tenant_ids.add(self.get_tenant_id(key))
            message += "\nNew " + data_type_message + " data for tenants: " + \
                       self.format_report_tenants_section(tenant_ids) + "\n"

        return message

    def format_report_tenants_section(self, tenant_ids):
        """

        :param tenant_ids: the ids of the tenant whose batteries have new data available
        :return: list of tenant ids formated for the report
        """
        message = ""
        for tenant in tenant_ids:
            message += "\n\t\t" + tenant + "\n"
        return message

    def get_tenant_id(self, battery_id):
        """

        :param battery_id: battery id
        :return: tenant id associated with given battery
        """
        return self.battery_repo.get_battery_tenant(battery_id)

