--DROP SCHEMA openshift cascade;
CREATE SCHEMA IF NOT EXISTS openshift;

--DROP TABLE openshift.leaks_data;
CREATE TABLE IF NOT EXISTS openshift.leaks_data(
    id SERIAL NOT NULL PRIMARY KEY, thing_name varchar(100) NOT NULL, pressure NUMERIC(3,5) NOT NULL, tstamp NUMBER(10) NOT NULL);

select * from pg_tables where schemaname='openshift';