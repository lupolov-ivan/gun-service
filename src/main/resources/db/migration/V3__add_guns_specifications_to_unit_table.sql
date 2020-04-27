ALTER TABLE unit
    ADD COLUMN load_cassette_time integer,
    ADD COLUMN disconnect_cassette_time integer,
    ADD COLUMN shot_period integer,
    ADD COLUMN quantity_bursting_cassette integer,
    ADD COLUMN capacity_bursting_cassette integer,
    ADD COLUMN quantity_armor_piercing_cassette integer,
    ADD COLUMN capacity_armor_piercing_cassette integer;