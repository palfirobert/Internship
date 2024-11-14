__package__ = None
from PythonApp.Services.Services import Service
from PythonApp.Repository.Repositories import BatteryRepository, ChargeProfilesRepository, SOHCSRepository, \
    TenantRepository


class ServiceFactory:

    @staticmethod
    def initialise():
        # initialize repos
        battery_repo = BatteryRepository()
        charge_profiles_repo = ChargeProfilesRepository()
        sohcs_repo = SOHCSRepository()
        tenant_repository = TenantRepository()

        # create service instance
        service = Service(battery_repo, tenant_repository, charge_profiles_repo, sohcs_repo)
        return service

