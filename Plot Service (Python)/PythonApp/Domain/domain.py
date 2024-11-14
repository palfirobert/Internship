from enum import Enum

'''
The domain of our problem the listed classes are also in the database

battery_user
tenant
battery
charge_profiles
sohcs
aslo we have user_tenant
'''

from sqlalchemy import ForeignKey, Column, Integer, TIMESTAMP
from sqlalchemy import String
from sqlalchemy.orm import DeclarativeBase
from sqlalchemy.orm import Mapped


class Base(DeclarativeBase):
    pass


class Battery(Base):
    __tablename__ = "battery"

    def __init__(self, batter_id, battery_type, tenand_id):
        self.battery_id = batter_id
        self.battery_type = battery_type
        self.tenant_id = tenand_id

    battery_id = Column("battery_id", String, primary_key=True)
    battery_type = Column("battery_type", String)
    tenant_id: Mapped[str] = Column("tenant_id", String, ForeignKey("tenant.tenant_id"))

    def __str__(self) -> str:
        return self.battery_id + " " + self.battery_type + " " + self.tenant_id


class User(Base):
    __tablename__ = "battery_users"

    battery_user_id = Column("battery_user_id", Integer, primary_key=True)
    username = Column("username", String)
    password = Column("password", String)


class Tenant(Base):
    __tablename__ = "tenant"

    def __init__(self, tenant_id):
        self.tenant_id = tenant_id

    tenant_id = Column("tenant_id", String, primary_key=True)

    def __repr__(self) -> str:
        return self.tenant_id


class ChargeProfiles(Base):
    __tablename__ = "charge_profiles"

    def __init__(self, timestamp, fast_temperature, fast_soc, fast_current, protected_temperature, protected_soc,
                 protected_current, battery_id):
        self.battery_id = battery_id
        self.timestamp = timestamp
        self.fast_temperature = fast_temperature
        self.fast_soc = fast_soc
        self.fast_current = fast_current
        self.protected_temperature = protected_temperature
        self.protected_soc = protected_soc
        self.protected_current = protected_current

    profile_id = Column("profile_id", Integer, primary_key=True)
    timestamp = Column("timestamp", TIMESTAMP)
    fast_temperature = Column("fast_temperature", String)
    fast_soc = Column("fast_soc", String)
    fast_current = Column("fast_current", String)
    protected_temperature = Column("protected_temperature", String)
    protected_soc = Column("protected_soc", String)
    protected_current = Column("protected_current", String)
    battery_id = Column("battery_id", String, ForeignKey("battery.battery_id"))

    def __repr__(self) -> str:
        return str(self.battery_id) + " " + str(self.timestamp)
    def __eq__(self, other):
        if isinstance(other, ChargeProfiles):
            return self.battery_id==other.battery_id and self.timestamp==other.timestamp
        return False

    def __getitem__(self, key):

        if key == 'profile_id':
            return self.profile_id
        elif key == 'timestamp':
            return self.timestamp
        elif key == 'fast_temperature':
            return self.fast_temperature
        elif key == 'fast_soc':
            return self.fast_soc
        elif key == 'fast_current':
            return self.fast_current
        elif key == 'protected_temperature':
            return self.protected_temperature
        elif key == 'protected_current':
            return self.protected_current
        elif key == 'protected_soc':
            return self.protected_soc
        elif key == 'battery_id':
            return self.battery_id
        else:
            raise KeyError(f"Invalid key: {key}")


class SOHCS(Base):
    __tablename__ = "sohcs"

    def __init__(self, algo_version, delta_sohc_flags, sohc, eq_param, min_sohc_cell_id, status, timestamp, battery_id):
        self.algo_version = algo_version
        self.delta_sohc_flags: int = delta_sohc_flags
        self.sohc = sohc
        self.eq_param = eq_param
        self.min_sohc_cell_id = min_sohc_cell_id
        self.status = status
        self.timestamp = timestamp
        self.battery_id = battery_id

    sohcs_id = Column("sohcs_id", Integer, primary_key=True)
    algo_version = Column("algo_version", String)
    delta_sohc_flags = Column("delta_sohc_flags", String)
    sohc = Column("sohc", Integer)
    eq_param = Column("eq_param", String)
    min_sohc_cell_id = Column("min_sohc_cell_id", Integer)
    status = Column("status", Integer)
    timestamp = Column("timestamp", TIMESTAMP)
    battery_id = Column("battery_id", String, ForeignKey("battery.battery_id"))

    def __repr__(self) -> str:
        return "Battery id: "+str(self.battery_id) + " Timestamp: "+str(self.timestamp)

    def __getitem__(self, key):
        if key == 'algo_version':
            return self.algo_version
        elif key == 'delta_sohc_flags':
            return self.delta_sohc_flags
        elif key == 'sohc':
            return self.sohc
        elif key == 'eq_param':
            return self.eq_param
        elif key == 'min_sohc_cell_id':
            return self.min_sohc_cell_id
        elif key == 'status':
            return self.status
        elif key == 'timestamp':
            return self.timestamp
        elif key == 'battery_id':
            return self.battery_id
        else:
            raise KeyError(f"Invalid key: {key}")

    def __eq__(self, other):
        if isinstance(other, SOHCS):
            return self.battery_id==other.battery_id and self.timestamp==other.timestamp
        return False


'''
class UsersTenants(Base):
    __tablename__ = "users_tenants"

    user_id : Mapped[int] = mapped_column(ForeignKey("battery_users.battery_user_id"),primary_key=True)
    tenant_id : Mapped[str] = mapped_column(ForeignKey("tenant.tenant_id"),primary_key=True)
'''

class PlotType(Enum):
    SOHC_MIN_CELL_EVOLUTION_AGAINST_TIME=1
    SOHC_EQ_PARAMS_AGAINST_TIME=2
    SOHC_3_WORST_CELL_INDEX_AGAINST_TIME=3
    SOHC_3_WORST_SOHCS_AGAINST_TIME=4
    CHARGE_PROFILE_PROTECTED_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE=5
    CHARGE_PROFILE_FAST_CURRENT_AGAINST_SOC_FOR_EACH_TEMPERATURE=6

class DataType(Enum):
    SOHC=1
    CP=2
