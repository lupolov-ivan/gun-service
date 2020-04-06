CREATE TABLE unit
(
	id SERIAL NOT NULL
		CONSTRAINT unit_pkey
			PRIMARY KEY,
	battlefield_id INTEGER,
	is_alive BOOLEAN,
	posx INTEGER,
	posy INTEGER,
	protection_level INTEGER,
	unit_type VARCHAR (255)
);
