INSERT INTO service_finder."user" (
    created,
    updated,
    updated_by,
    identity_provider_id,
    email_address_verified,
    email_address,
    telephone_number,
    name,
    job_name,
    organisation_name,
    job_type_id,
    organisation_type_id,
    approval_status,
    approval_status_updated,
    approval_status_updated_by,
    postcode,
    terms_and_conditions_accepted
) VALUES (
    current_timestamp,
    current_timestamp,
    (select last_value from service_finder.user_id_seq),
    '41308a85-d3a9-4c73-9079-ca90ebd0984c', -- must match the Cognito sub for this user
    true,
    'service-finder-admin@nhs.net',
    '01234567890',
    'Service Finder Admin',
    'Admin',
    'NHS',
    (SELECT id FROM service_finder."job_type" WHERE code = 'ADMINISTRATOR'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'NHS_ENGLAND'),
    'APPROVED',
    current_timestamp,
    1,
    'EX2 5SE',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_id, region_id) VALUES (
    (SELECT id FROM service_finder."user" WHERE email_address = 'service-finder-admin@nhs.net'),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);

INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    (SELECT id FROM service_finder."user" WHERE email_address = 'service-finder-admin@nhs.net'),
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);
INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    (SELECT id FROM service_finder."user" WHERE email_address = 'service-finder-admin@nhs.net'),
    (SELECT id FROM service_finder."role" WHERE code = 'APPROVER')
);
INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    (SELECT id FROM service_finder."user" WHERE email_address = 'service-finder-admin@nhs.net'),
    (SELECT id FROM service_finder."role" WHERE code = 'REPORTER')
);
INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    (SELECT id FROM service_finder."user" WHERE email_address = 'service-finder-admin@nhs.net'),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);
