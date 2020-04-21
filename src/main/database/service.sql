-- SQL Manager Lite for PostgreSQL 5.8.1.48500
-- ---------------------------------------
-- Хост         : localhost
-- База данных  : service
-- Версия       : PostgreSQL 9.5.2, compiled by Visual C++ build 1800, 64-bit



SET check_function_bodies = false;
--
-- Definition for sequence hibernate_sequence (OID = 16742) : 
--
SET search_path = public, pg_catalog;
CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MAXVALUE
    NO MINVALUE
    CACHE 1;
--
-- Structure for table country (OID = 16744) : 
--
CREATE TABLE public.country (
    id integer NOT NULL,
    code varchar(255),
    short_title varchar(255),
    title varchar(255)
)
WITH (oids = false);
--
-- Definition for index country_tite_idx (OID = 16752) : 
--
CREATE INDEX country_tite_idx ON country USING btree (title);
--
-- Definition for index country_pkey (OID = 16750) : 
--
ALTER TABLE ONLY country
    ADD CONSTRAINT country_pkey
    PRIMARY KEY (id);
--
-- Comments
--
COMMENT ON SCHEMA public IS 'standard public schema';
