ALTER TABLE ONLY service_finder."user"
    ADD CONSTRAINT user_approval_status_updated_by_fkey FOREIGN KEY (approval_status_updated_by) REFERENCES service_finder."user"(id);

SELECT pg_catalog.setval(
    pg_get_serial_sequence('service_finder.job_type', 'id'),
    (SELECT MAX(id) FROM service_finder.job_type)+1);
SELECT pg_catalog.setval(
    pg_get_serial_sequence('service_finder.login_attempt', 'id'),
    (SELECT MAX(id) FROM service_finder.login_attempt)+1);
SELECT pg_catalog.setval(
    pg_get_serial_sequence('service_finder.organisation_type', 'id'),
    (SELECT MAX(id) FROM service_finder.organisation_type)+1);
SELECT pg_catalog.setval(
    pg_get_serial_sequence('service_finder.region', 'id'),
    (SELECT MAX(id) FROM service_finder.region)+1);
SELECT pg_catalog.setval(
    pg_get_serial_sequence('service_finder.role', 'id'),
    (SELECT MAX(id) FROM service_finder.role)+1);
SELECT pg_catalog.setval(
    pg_get_serial_sequence('service_finder.user', 'id'),
    (SELECT MAX(id) FROM service_finder.user)+1);
