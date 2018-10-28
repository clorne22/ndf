
drop table t_ndf_item;
drop table t_ndf;
drop table t_user;
drop table t_km;
drop table t_business;


-- Table : t_km
create table t_km (
	id integer not null,
	start_date date not null,
	end_date date,
	price numeric(10,2) not null,
	constraint t_km_pkey primary key (id)
)
;


-- Table : t_user
create table t_user (
	id integer not null,
	first_name varchar(255) not null,
	last_name varchar(255) not null,
	login varchar(255) not null,
	password varchar(255) not null,
	administrator boolean,
	constraint t_user_pkey primary key (id)
)
;


-- Table : t_business
create table t_business (
	id integer not null,
	number varchar(255) not null,
	label varchar(255) not null,
	place varchar(255),
	trip varchar(255),
	trip_object varchar(255),
	km integer,
	trip_price numeric(10,2) null,
	constraint t_business_pkey primary key (id)
)
;


-- Table : t_ndf
create table t_ndf (
	id integer not null,
	ndf_date date not null,
	visa date null,
	payment date null,
	km_id integer not null,
	user_id integer not null,
	constraint t_ndf_pkey primary key (id),
  	constraint t_ndf_km_fkey foreign key (km_id) references t_km (id) MATCH SIMPLE,
  	constraint t_ndf_user_fkey foreign key (user_id) references t_user (id) MATCH SIMPLE
)
;

-- Table : t_ndf_item
create table t_ndf_item (
	id integer not null,
	ndf_id integer not null,
	business_id integer null,
	ndf_item_date date not null,
	place varchar(255),
	ndf_item_object varchar(255),
	trip varchar(255),
	ndf_item_trip_price numeric(10,2) null,
	ndf_item_km_price numeric(10,2) null,
	km integer,
	constraint t_ndf_item_pkey primary key (id),
  	constraint t_ndf_item_business_fkey foreign key (business_id) references t_business (id) MATCH SIMPLE,
  	constraint t_ndf_item_ndf_fkey foreign key (ndf_id) references t_ndf (id) MATCH SIMPLE
)
;


