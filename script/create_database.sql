SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

-- Clear database and user if they exist --
drop database if exists football_prediction;
drop user if exists tclarke;

-- Create new user and database, make tclarke the owner --
create user tclarke with password 'tclarkepass';
create database football_prediction with owner tclarke;
grant all privileges on database football_prediction to tclarke;

SET search_path = public, pg_catalog;
SET default_with_oids = false;
SET default_tablespace = '';

\c football_prediction

--
-- Name: regular_season; Type: SCHEMA; Schema: -; Owner: tclarke
--
CREATE SCHEMA regular_season;
ALTER SCHEMA regular_season OWNER TO tclarke;

REVOKE ALL ON SCHEMA regular_season FROM PUBLIC;
REVOKE ALL ON SCHEMA regular_season FROM tclarke;
GRANT ALL ON SCHEMA regular_season TO tclarke;

--
-- Name: roster; Type: SCHEMA; Schema: -; Owner: tclarke
--
CREATE SCHEMA roster;
ALTER SCHEMA roster OWNER TO tclarke;

REVOKE ALL ON SCHEMA roster FROM PUBLIC;
REVOKE ALL ON SCHEMA roster FROM tclarke;
GRANT ALL ON SCHEMA roster TO tclarke;

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;