--dati create database si folositi o dupa, ca postgres nu are asa comenzi :()--


create table battery_users
(
	battery_user_id serial not null
		constraint battery_users_pk
			primary key,
	username varchar not null,
	password varchar not null
);

create table tenant
(
	tenant_id varchar not null
		constraint tenant_pk
			primary key
);

create table battery
(
    battery_id varchar not null
        constraint battery_pk
            primary key,
    battery_type varchar,
    tenant_id varchar,
    constraint battery_tenant_fk
        foreign key(tenant_id) references tenant(tenant_id)
);

create table sohcs
(
	sohcs_id serial not null
		constraint sohcs_pk
			primary key,
	algo_version varchar,
	delta_sohc_flags varchar,
	sohc varchar,
	eq_param varchar,
	min_sohc_cell_id int,
	status int,
	timestamp timestamp,
	battery_id varchar,
	constraint battery_shocs_fk 
foreign key(battery_id) references battery(battery_id)
);

create table charge_profiles
(
	profile_id serial not null
		constraint charge_profiles_pk
			primary key,
	timestamp timestamp,
	fast_temperature varchar,
	fast_soc varchar,
	fast_current varchar,
	protected_temperature varchar,
	protected_soc varchar,
	protected_current varchar,
	battery_id varchar,
    constraint battery_charge_fk
        foreign key(battery_id) references battery(battery_id)

);
create table users_tenants
(
	user_id int not null,
	tenant_id varchar not null,
    constraint tenant_user_fk
        foreign key(user_id) references battery_users(battery_user_id),
            constraint user_tenant_fk
            foreign key(tenant_id) references tenant(tenant_id),
            
    constraint prim_us_ten_pk primary key (user_id,tenant_id)
	
);


---run this script and everything will be fine :)---
