__package__ = None
'''
Repository classed for Battery, Tenant, ChargeProfiles and SOHCS 
'''
from PythonApp.Domain.domain import Battery, SOHCS, ChargeProfiles
from PythonApp.Utils.Utils import ConfigUtils as dbu
from sqlalchemy import select

'''
Repository interface containing crud methods, all repositories need to impliment this interface
'''


class Repository():

    # save methodm, saves an entity into the database
    # param:
    #       entity - the entity that you want to save
    # returns : the enitity that was saved
    def save(self, entity):
        pass

    # delete method, deletes an entity from the database
    # param:
    #       entity - the entity that you want to delete
    def delete(self, entity):
        pass

    # update method updates an enitity from the database,
    # it recieves an enitity and the new data from it is written in place of the old data from this entity
    # param:
    #       entity - the entity that you want to update
    def update(self, entity):
        pass

    # find_one - method that searches for an entity
    # param:
    #   the entity that you want to search or the entity that you want to search for
    def find_one(self, entity):
        pass

    # find_all - method that finds all entities that match the param entity
    def find_all_sohcs_by_id(self, entity):
        pass
    def find_all_by_battery_id(self,battery_id):
        pass
    def find_all_ids(self):
        pass


class BatteryRepository(Repository):

    def save(self, entity: Battery):
        print("savin battery" + entity.__repr__())
        session = dbu.get_session()
        session.add(entity)
        session.commit()
        session.close()
        print("battery saved")

    def find_all_ids(self):
        print("getting the batteries...")
        session = dbu.get_session()
        statement = select(Battery.battery_id)
        battery_ids = session.scalars(statement).all()

        return battery_ids

    def get_battery_tenant(self, battery_id):
        """
        get the tenant id associated to a battery id
        :param battery_id: battery id
        :return:
        """
        print("getting tenant...")
        session = dbu.get_session()
        statement = select(Battery.tenant_id).where(Battery.battery_id == battery_id)
        tenant_id = session.scalars(statement).first()

        return tenant_id

class TenantRepository(Repository):

    def save(self, entity):
        print("savin tenant data" + entity.__repr__())
        session = dbu.get_session()  # dbu the databaseutils class that provides sessions with the db
        session.add(entity)
        session.commit()
        session.close()
        print("tenant saved")


class ChargeProfilesRepository(Repository):

    def save(self, entity):
        print("savin charge profile" + entity.__repr__())
        session = dbu.get_session()
        session.add(entity)
        session.commit()
        session.close()
        print("charge profile saved")

    def find_all_by_battery_id(self,battery_id):
        print("finding all charge profiles for battery " + battery_id)
        session = dbu.get_session()
        statement =  select(ChargeProfiles).filter_by(battery_id=battery_id)
        charge_profiles = session.scalars(statement).all()
        return charge_profiles


class SOHCSRepository(Repository):

    def save(self, entity):
        print("savin sohc" + entity.__repr__())

        session = dbu.get_session()
        session.add(entity)
        session.commit()
        session.close()
        print("sohc saved")




    def find_all_sohcs_by_battery_id(self, battery_id):
        # create session
        session = dbu.get_session()

        # query through the data
        statement = select(SOHCS).filter_by(battery_id=battery_id).order_by(SOHCS.timestamp)

        # retrieve data
        battery_sohcs = session.scalars(statement).all()

        return battery_sohcs

# TODO CRY
