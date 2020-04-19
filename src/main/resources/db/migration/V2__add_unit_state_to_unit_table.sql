ALTER TABLE unit
    DROP COLUMN is_alive,
    ADD COLUMN unit_state varchar (255);