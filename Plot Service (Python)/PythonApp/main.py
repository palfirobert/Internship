__package__ = None
from PythonApp.Domain.domain import PlotType
from Repository.Repositories import *
from Services.Services import Service
import matplotlib.pyplot as plt

#function that initializes the database
#it populates it with the data about sohcs and charge profiles
def database_init():
    battery_repo = BatteryRepository()
    charge_profiles_repo = ChargeProfilesRepository()
    sohcs_repo = SOHCSRepository()
    tenant_repository = TenantRepository()

    service = Service(battery_repo, tenant_repository, charge_profiles_repo, sohcs_repo)

    sohcs_file = "../resources/sohcs.csv"
    charge_profiles_file = "../resources/charge profiles.csv"

    service.insert_data_from_csv_into_db(sohcs_file)
    service.insert_data_from_csv_into_db(charge_profiles_file)



battery_repo = BatteryRepository()
charge_profiles_repo = ChargeProfilesRepository()
sohcs_repo = SOHCSRepository()
tenant_repository = TenantRepository()

service = Service(battery_repo, tenant_repository, charge_profiles_repo, sohcs_repo)
ids= ["0ENPE011011000B8B0000030","0ENPE011011000B8S0000019"]  #["0ENPE011011000B8B0000030","0ENPE011011000B8S0000019"], "BBA76DB39A58E1CF4823220CF2D5F113"
tenant_id = "1aa9d5e8-a182-414b-be97-1791fd89fc27"
service.plot(tenant_id,ids,PlotType.SOHC_3_WORST_SOHCS_AGAINST_TIME)
service.plot(tenant_id,ids,PlotType.SOHC_3_WORST_CELL_INDEX_AGAINST_TIME)
service.plot(tenant_id,ids,PlotType.SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME)

service.manage_databricks_data('sohc')

