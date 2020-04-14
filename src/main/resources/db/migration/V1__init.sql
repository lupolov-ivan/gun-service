create table subdivision
(
	id serial not null
		constraint subdivision_pkey
			primary key,
	name varchar(255)
);

create table unit
(
	id serial not null
		constraint unit_pkey
			primary key,
	is_alive boolean,
	posx integer,
	posy integer,
	protection_level integer,
	unit_type varchar(255),
	subdivision_id integer
		constraint fkad8vh7ng1tvmpv6dl7ep1r8a0
			references subdivision
);