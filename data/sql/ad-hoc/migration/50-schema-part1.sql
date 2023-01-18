--
-- PostgreSQL database dump
--

-- Dumped from database version 11.4
-- Dumped by pg_dump version 11.5

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: unaccent; Type: EXTENSION; Schema: -; Owner:
--

CREATE EXTENSION IF NOT EXISTS unaccent WITH SCHEMA service_finder;


--
-- Name: EXTENSION unaccent; Type: COMMENT; Schema: -; Owner:
--

COMMENT ON EXTENSION unaccent IS 'text search dictionary that removes accents';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: job_type; Type: TABLE; Schema: service_finder; Owner: sfm
--

CREATE TABLE service_finder.job_type (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(255) NOT NULL
);


ALTER TABLE service_finder.job_type OWNER TO sfm;

--
-- Name: job_type_id_seq; Type: SEQUENCE; Schema: service_finder; Owner: sfm
--

CREATE SEQUENCE service_finder.job_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE service_finder.job_type_id_seq OWNER TO sfm;

--
-- Name: job_type_id_seq; Type: SEQUENCE OWNED BY; Schema: service_finder; Owner: sfm
--

ALTER SEQUENCE service_finder.job_type_id_seq OWNED BY service_finder.job_type.id;


--
-- Name: login_attempt; Type: TABLE; Schema: service_finder; Owner: sfm
--

CREATE TABLE service_finder.login_attempt (
    id bigint NOT NULL,
    created timestamp without time zone NOT NULL,
    updated timestamp without time zone NOT NULL,
    email_address character varying(255) NOT NULL,
    attempts integer NOT NULL
);


ALTER TABLE service_finder.login_attempt OWNER TO sfm;

--
-- Name: login_attempt_id_seq; Type: SEQUENCE; Schema: service_finder; Owner: sfm
--

CREATE SEQUENCE service_finder.login_attempt_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE service_finder.login_attempt_id_seq OWNER TO sfm;

--
-- Name: login_attempt_id_seq; Type: SEQUENCE OWNED BY; Schema: service_finder; Owner: sfm
--

ALTER SEQUENCE service_finder.login_attempt_id_seq OWNED BY service_finder.login_attempt.id;


--
-- Name: organisation_type; Type: TABLE; Schema: service_finder; Owner: sfm
--

CREATE TABLE service_finder.organisation_type (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(255) NOT NULL
);


ALTER TABLE service_finder.organisation_type OWNER TO sfm;

--
-- Name: organisation_type_id_seq; Type: SEQUENCE; Schema: service_finder; Owner: sfm
--

CREATE SEQUENCE service_finder.organisation_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE service_finder.organisation_type_id_seq OWNER TO sfm;

--
-- Name: organisation_type_id_seq; Type: SEQUENCE OWNED BY; Schema: service_finder; Owner: sfm
--

ALTER SEQUENCE service_finder.organisation_type_id_seq OWNED BY service_finder.organisation_type.id;


--
-- Name: region; Type: TABLE; Schema: service_finder; Owner: sfm
--

CREATE TABLE service_finder.region (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(255) NOT NULL
);


ALTER TABLE service_finder.region OWNER TO sfm;

--
-- Name: region_id_seq; Type: SEQUENCE; Schema: service_finder; Owner: sfm
--

CREATE SEQUENCE service_finder.region_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE service_finder.region_id_seq OWNER TO sfm;

--
-- Name: region_id_seq; Type: SEQUENCE OWNED BY; Schema: service_finder; Owner: sfm
--

ALTER SEQUENCE service_finder.region_id_seq OWNED BY service_finder.region.id;


--
-- Name: role; Type: TABLE; Schema: service_finder; Owner: sfm
--

CREATE TABLE service_finder.role (
    id bigint NOT NULL,
    name character varying(255) NOT NULL,
    code character varying(255) NOT NULL
);


ALTER TABLE service_finder.role OWNER TO sfm;

--
-- Name: role_id_seq; Type: SEQUENCE; Schema: service_finder; Owner: sfm
--

CREATE SEQUENCE service_finder.role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE service_finder.role_id_seq OWNER TO sfm;

--
-- Name: role_id_seq; Type: SEQUENCE OWNED BY; Schema: service_finder; Owner: sfm
--

ALTER SEQUENCE service_finder.role_id_seq OWNED BY service_finder.role.id;


--
-- Name: user; Type: TABLE; Schema: service_finder; Owner: sfm
--

CREATE TABLE service_finder."user" (
    id bigint NOT NULL,
    created timestamp without time zone NOT NULL,
    updated timestamp without time zone NOT NULL,
    identity_provider_id character varying(255) NOT NULL,
    email_address_verified boolean DEFAULT false NOT NULL,
    email_address character varying(255) NOT NULL,
    telephone_number character varying(20),
    name character varying(255) NOT NULL,
    job_name character varying(255) NOT NULL,
    job_type_id bigint NOT NULL,
    job_type_other character varying(255),
    organisation_name character varying(255) NOT NULL,
    organisation_type_id bigint NOT NULL,
    organisation_type_other character varying(255),
    approval_status character varying(8) DEFAULT 'PENDING'::character varying NOT NULL,
    approval_status_updated timestamp without time zone,
    approval_status_updated_by bigint,
    rejection_reason character varying(500),
    postcode character varying(8),
    terms_and_conditions_accepted timestamp without time zone,
    CONSTRAINT user_approval_status_check CHECK (((approval_status)::text = ANY ((ARRAY['PENDING'::character varying, 'APPROVED'::character varying, 'REJECTED'::character varying])::text[])))
);


ALTER TABLE service_finder."user" OWNER TO sfm;

--
-- Name: user_id_seq; Type: SEQUENCE; Schema: service_finder; Owner: sfm
--

CREATE SEQUENCE service_finder.user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE service_finder.user_id_seq OWNER TO sfm;

--
-- Name: user_id_seq; Type: SEQUENCE OWNED BY; Schema: service_finder; Owner: sfm
--

ALTER SEQUENCE service_finder.user_id_seq OWNED BY service_finder."user".id;


--
-- Name: user_region; Type: TABLE; Schema: service_finder; Owner: sfm
--

CREATE TABLE service_finder.user_region (
    user_id bigint NOT NULL,
    region_id bigint NOT NULL
);


ALTER TABLE service_finder.user_region OWNER TO sfm;

--
-- Name: user_role; Type: TABLE; Schema: service_finder; Owner: sfm
--

CREATE TABLE service_finder.user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


ALTER TABLE service_finder.user_role OWNER TO sfm;

--
-- Name: job_type id; Type: DEFAULT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.job_type ALTER COLUMN id SET DEFAULT nextval('service_finder.job_type_id_seq'::regclass);


--
-- Name: login_attempt id; Type: DEFAULT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.login_attempt ALTER COLUMN id SET DEFAULT nextval('service_finder.login_attempt_id_seq'::regclass);


--
-- Name: organisation_type id; Type: DEFAULT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.organisation_type ALTER COLUMN id SET DEFAULT nextval('service_finder.organisation_type_id_seq'::regclass);


--
-- Name: region id; Type: DEFAULT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.region ALTER COLUMN id SET DEFAULT nextval('service_finder.region_id_seq'::regclass);


--
-- Name: role id; Type: DEFAULT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.role ALTER COLUMN id SET DEFAULT nextval('service_finder.role_id_seq'::regclass);


--
-- Name: user id; Type: DEFAULT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder."user" ALTER COLUMN id SET DEFAULT nextval('service_finder.user_id_seq'::regclass);


--
-- Name: job_type job_type_code_key; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.job_type
    ADD CONSTRAINT job_type_code_key UNIQUE (code);


--
-- Name: job_type job_type_pkey; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.job_type
    ADD CONSTRAINT job_type_pkey PRIMARY KEY (id);


--
-- Name: login_attempt login_attempt_email_address_key; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.login_attempt
    ADD CONSTRAINT login_attempt_email_address_key UNIQUE (email_address);


--
-- Name: login_attempt login_attempt_pkey; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.login_attempt
    ADD CONSTRAINT login_attempt_pkey PRIMARY KEY (id);


--
-- Name: organisation_type organisation_type_code_key; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.organisation_type
    ADD CONSTRAINT organisation_type_code_key UNIQUE (code);


--
-- Name: organisation_type organisation_type_pkey; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.organisation_type
    ADD CONSTRAINT organisation_type_pkey PRIMARY KEY (id);


--
-- Name: region region_code_key; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.region
    ADD CONSTRAINT region_code_key UNIQUE (code);


--
-- Name: region region_pkey; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.region
    ADD CONSTRAINT region_pkey PRIMARY KEY (id);


--
-- Name: role role_code_key; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.role
    ADD CONSTRAINT role_code_key UNIQUE (code);


--
-- Name: role role_pkey; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.role
    ADD CONSTRAINT role_pkey PRIMARY KEY (id);


--
-- Name: user user_email_address_key; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder."user"
    ADD CONSTRAINT user_email_address_key UNIQUE (email_address);


--
-- Name: user user_identity_provider_id_key; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder."user"
    ADD CONSTRAINT user_identity_provider_id_key UNIQUE (identity_provider_id);


--
-- Name: user user_pkey; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder."user"
    ADD CONSTRAINT user_pkey PRIMARY KEY (id);


--
-- Name: user_region user_region_pkey; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.user_region
    ADD CONSTRAINT user_region_pkey PRIMARY KEY (user_id, region_id);


--
-- Name: user_role user_role_pkey; Type: CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.user_role
    ADD CONSTRAINT user_role_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: user user_approval_status_updated_by_fkey; Type: FK CONSTRAINT; Schema: service_finder; Owner: sfm
--

-- ALTER TABLE ONLY service_finder."user"
--     ADD CONSTRAINT user_approval_status_updated_by_fkey FOREIGN KEY (approval_status_updated_by) REFERENCES service_finder."user"(id);


--
-- Name: user user_job_type_id_fkey; Type: FK CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder."user"
    ADD CONSTRAINT user_job_type_id_fkey FOREIGN KEY (job_type_id) REFERENCES service_finder.job_type(id);


--
-- Name: user user_organisation_type_id_fkey; Type: FK CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder."user"
    ADD CONSTRAINT user_organisation_type_id_fkey FOREIGN KEY (organisation_type_id) REFERENCES service_finder.organisation_type(id);


--
-- Name: user_region user_region_region_id_fkey; Type: FK CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.user_region
    ADD CONSTRAINT user_region_region_id_fkey FOREIGN KEY (region_id) REFERENCES service_finder.region(id);


--
-- Name: user_region user_region_user_id_fkey; Type: FK CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.user_region
    ADD CONSTRAINT user_region_user_id_fkey FOREIGN KEY (user_id) REFERENCES service_finder."user"(id);


--
-- Name: user_role user_role_role_id_fkey; Type: FK CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.user_role
    ADD CONSTRAINT user_role_role_id_fkey FOREIGN KEY (role_id) REFERENCES service_finder.role(id);


--
-- Name: user_role user_role_user_id_fkey; Type: FK CONSTRAINT; Schema: service_finder; Owner: sfm
--

ALTER TABLE ONLY service_finder.user_role
    ADD CONSTRAINT user_role_user_id_fkey FOREIGN KEY (user_id) REFERENCES service_finder."user"(id);


--
-- Name: SCHEMA service_finder; Type: ACL; Schema: -; Owner: sfm
--

--REVOKE ALL ON SCHEMA service_finder FROM rdsadmin;
--REVOKE ALL ON SCHEMA service_finder FROM PUBLIC;
--GRANT ALL ON SCHEMA service_finder TO sfm;
--GRANT ALL ON SCHEMA service_finder TO PUBLIC;


--
-- Name: TABLE job_type; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE service_finder.job_type TO service_finder;


--
-- Name: SEQUENCE job_type_id_seq; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,USAGE ON SEQUENCE service_finder.job_type_id_seq TO service_finder;


--
-- Name: TABLE login_attempt; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE service_finder.login_attempt TO service_finder;


--
-- Name: SEQUENCE login_attempt_id_seq; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,USAGE ON SEQUENCE service_finder.login_attempt_id_seq TO service_finder;


--
-- Name: TABLE organisation_type; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE service_finder.organisation_type TO service_finder;


--
-- Name: SEQUENCE organisation_type_id_seq; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,USAGE ON SEQUENCE service_finder.organisation_type_id_seq TO service_finder;


--
-- Name: TABLE region; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE service_finder.region TO service_finder;


--
-- Name: SEQUENCE region_id_seq; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,USAGE ON SEQUENCE service_finder.region_id_seq TO service_finder;


--
-- Name: TABLE role; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE service_finder.role TO service_finder;


--
-- Name: SEQUENCE role_id_seq; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,USAGE ON SEQUENCE service_finder.role_id_seq TO service_finder;


--
-- Name: TABLE "user"; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE service_finder."user" TO service_finder;


--
-- Name: SEQUENCE user_id_seq; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,USAGE ON SEQUENCE service_finder.user_id_seq TO service_finder;


--
-- Name: TABLE user_region; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE service_finder.user_region TO service_finder;


--
-- Name: TABLE user_role; Type: ACL; Schema: service_finder; Owner: sfm
--

GRANT SELECT,INSERT,DELETE,UPDATE ON TABLE service_finder.user_role TO service_finder;


--
-- Name: DEFAULT PRIVILEGES FOR SEQUENCES; Type: DEFAULT ACL; Schema: -; Owner: sfm
--

ALTER DEFAULT PRIVILEGES FOR ROLE sfm GRANT SELECT,USAGE ON SEQUENCES  TO service_finder;


--
-- Name: DEFAULT PRIVILEGES FOR TABLES; Type: DEFAULT ACL; Schema: -; Owner: sfm
--

ALTER DEFAULT PRIVILEGES FOR ROLE sfm GRANT SELECT,INSERT,DELETE,UPDATE ON TABLES  TO service_finder;


--
-- PostgreSQL database dump complete
--

