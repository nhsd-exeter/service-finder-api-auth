/* User data */
INSERT INTO service_finder."user_account" (
    id,
    created,
    updated,
    updated_by,
    identity_provider_id,
    email_address_verified,
    email_address,
    last_logged_in,
    user_state
) VALUES (
    nextval('service_finder.user_account_id_seq'),
    current_timestamp,
    current_timestamp,
    (select last_value from service_finder.user_account_id_seq),
    'acd0d135-4587-4329-8bb2-de67c7865a75',
    true,
    'service-finder-approver@nhs.net',
    current_timestamp,
    'ACTIVE'
);

INSERT INTO service_finder."user_details" (
    id,
    user_account_id,
    created,
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
    nextval('service_finder.user_details_id_seq'),
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'service-finder-approver@nhs.net'),
    current_timestamp,
    '01234567890',
    'Approver name',
    'Test job name',
    'Test organisation name',
    (SELECT id FROM "job_type" WHERE code = 'ADMINISTRATOR'),
    (SELECT id FROM "organisation_type" WHERE code = 'NHS_ENGLAND'),
    'APPROVED',
    current_timestamp,
    (SELECT id FROM "user_account" WHERE email_address = 'service-finder-admin@nhs.net'),
    'EX1 1SR',
    current_timestamp
);


INSERT INTO service_finder."user_account" (
    id,
    created,
    updated,
    updated_by,
    identity_provider_id,
    email_address_verified,
    email_address,
    last_logged_in,
    user_state
) VALUES (
    nextval('service_finder.user_account_id_seq'),
    current_timestamp,
    current_timestamp,
    (select last_value from service_finder.user_account_id_seq),
    '8e291a45-6c23-48d6-8a66-3a364365f314',
    true,
    'service-finder-reporter@nhs.net',
    current_timestamp,
    'ACTIVE'
);

INSERT INTO service_finder."user_details" (
    id,
    user_account_id,
    created,
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
    nextval('service_finder.user_details_id_seq'),
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'service-finder-reporter@nhs.net'),
    current_timestamp,
    '01234567890',
    'Reporter name',
    'Test job name',
    'Test organisation name',
    (SELECT id FROM "job_type" WHERE code = 'ADMINISTRATOR'),
    (SELECT id FROM "organisation_type" WHERE code = 'NHS_ENGLAND'),
    'APPROVED',
    current_timestamp,
    (SELECT id FROM "user_account" WHERE email_address = 'service-finder-admin@nhs.net'),
    'EX1 1SR',
    current_timestamp
);

INSERT INTO service_finder."user_account" (
    id,
    created,
    updated,
    updated_by,
    identity_provider_id,
    email_address_verified,
    email_address,
    last_logged_in,
    user_state
) VALUES (
    nextval('service_finder.user_account_id_seq'),
    current_timestamp,
    current_timestamp,
    (select last_value from service_finder.user_account_id_seq),
    '9ce6e50c-808f-466d-bec2-929e8349aa2e',
    true,
    'service-finder-search@nhs.net',
    current_timestamp,
    'ACTIVE'
);

INSERT INTO service_finder."user_details" (
    id,
    user_account_id,
    created,
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
    nextval('service_finder.user_details_id_seq'),
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'service-finder-search@nhs.net'),
    current_timestamp,
    '01234567890',
    'Search name',
    'Test job name',
    'Test organisation name',
    (SELECT id FROM "job_type" WHERE code = 'ADMINISTRATOR'),
    (SELECT id FROM "organisation_type" WHERE code = 'NHS_ENGLAND'),
    'APPROVED',
    current_timestamp,
    (SELECT id FROM "user_account" WHERE email_address = 'service-finder-admin@nhs.net'),
    'EX1 1SR',
    current_timestamp
);

/* User-Region join table data */
INSERT INTO "user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'service-finder-approver@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM "region" WHERE code = 'SOUTH_WEST'));
INSERT INTO "user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'service-finder-reporter@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM "region" WHERE code = 'SOUTH_WEST'));
INSERT INTO "user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'service-finder-search@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM "region" WHERE code = 'SOUTH_WEST'));

/* User-Role join table data */
INSERT INTO "user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'service-finder-approver@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM "role" WHERE code = 'APPROVER'));
INSERT INTO "user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'service-finder-approver@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM "role" WHERE code = 'SEARCH'));
INSERT INTO "user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'service-finder-reporter@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM "role" WHERE code = 'REPORTER'));
INSERT INTO "user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'service-finder-reporter@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM "role" WHERE code = 'SEARCH'));
INSERT INTO "user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'service-finder-search@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM "role" WHERE code = 'SEARCH'));
