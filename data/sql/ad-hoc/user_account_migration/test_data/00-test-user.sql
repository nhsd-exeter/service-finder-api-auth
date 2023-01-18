--User with region and role
INSERT INTO service_finder."user" (
    id,
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
    900001,
    current_timestamp,
    current_timestamp,
    null,
    '900001', -- must match the Cognito sub for this user
    true,
    '900001@nhs.net',
    '01234567890',
    '900001',
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
    900001,
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);

INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    900001,
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);

-- User with a user referral record
INSERT INTO service_finder."user" (
    id,
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
    900002,
    current_timestamp,
    current_timestamp,
    null,
    '900002', -- must match the Cognito sub for this user
    true,
    '900002@nhs.net',
    '01234567890',
    '900002',
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
    900002,
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);

INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    900002,
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);

INSERT INTO service_finder."user_referral_reference" (
    id,
    user_id,
    referral_reference_code
)
VALUES(
    900002,
    900002,
    'Test'
);

INSERT INTO service_finder."user_referral_reference" (
    id,
    user_id,
    referral_reference_code
)
VALUES(
    900003,
    900002,
    'Test2'
);

-- User with user change record
INSERT INTO service_finder."user" (
    id,
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
    900003,
    current_timestamp,
    current_timestamp,
    null,
    '900003', -- must match the Cognito sub for this user
    true,
    '900003@nhs.net',
    '01234567890',
    '900003',
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
    900003,
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);

INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    900003,
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);

INSERT INTO service_finder."user_change"(
    id,
    user_id,
    updated,
    updated_by,
    original_value,
    new_value,
    field_type
)
VALUES(
    900003,
    900003,
    current_timestamp,
    900002,
    'ORIG',
    'NEW',
    'STATUS'
);

INSERT INTO service_finder."user_change"(
    id,
    user_id,
    updated,
    updated_by,
    original_value,
    new_value,
    field_type
)
VALUES(
    900004,
    900003,
    current_timestamp,
    900001,
    'ORIG2',
    'NEW2',
    'STATUS'
);

-- User with Other data
INSERT INTO service_finder."user" (
    id,
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
    job_type_other,
    organisation_type_id,
    organisation_type_other,
    approval_status,
    approval_status_updated,
    approval_status_updated_by,
    postcode,
    terms_and_conditions_accepted
) VALUES (
    900004,
    current_timestamp,
    current_timestamp,
    null,
    '900004', -- must match the Cognito sub for this user
    true,
    '900004@nhs.net',
    '01234567890',
    '900004',
    'Admin',
    'NHS',
    (SELECT id FROM service_finder."job_type" WHERE code = 'ADMINISTRATOR'),
    'JOB TYPE OTHER',
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'NHS_ENGLAND'),
    'ORG TYPE OTHER',
    'APPROVED',
    current_timestamp,
    1,
    'EX2 5SE',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_id, region_id) VALUES (
    900004,
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);

INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    900004,
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);

-- USer with  rejected reason
INSERT INTO service_finder."user" (
    id,
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
    job_type_other,
    organisation_type_id,
    organisation_type_other,
    approval_status,
    approval_status_updated,
    approval_status_updated_by,
    postcode,
    terms_and_conditions_accepted,
    rejection_reason,
    last_logged_in,
    user_state
) VALUES (
    900005,
    current_timestamp,
    current_timestamp,
    900002,
    '900005', -- must match the Cognito sub for this user
    true,
    '900005@nhs.net',
    '01234567890',
    '900005',
    'Admin',
    'NHS',
    (SELECT id FROM service_finder."job_type" WHERE code = 'ADMINISTRATOR'),
    'JOB TYPE OTHER',
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'NHS_ENGLAND'),
    'ORG TYPE OTHER',
    'REJECTED',
    current_timestamp,
    1,
    'EX2 5SE',
    current_timestamp,
    'Reject',
    current_timestamp,
    'INACTIVE'
);

INSERT INTO service_finder."user_region" (user_id, region_id) VALUES (
    900005,
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);

INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    900005,
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);

--User with content download
INSERT INTO service_finder."user" (
    id,
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
    job_type_other,
    organisation_type_id,
    organisation_type_other,
    approval_status,
    approval_status_updated,
    approval_status_updated_by,
    postcode,
    terms_and_conditions_accepted
) VALUES (
    900006,
    current_timestamp,
    current_timestamp,
    null,
    '900006', -- must match the Cognito sub for this user
    true,
    '900006@nhs.net',
    '01234567890',
    '900006',
    'Admin',
    'NHS',
    (SELECT id FROM service_finder."job_type" WHERE code = 'ADMINISTRATOR'),
    'JOB TYPE OTHER',
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'NHS_ENGLAND'),
    'ORG TYPE OTHER',
    'APPROVED',
    current_timestamp,
    1,
    'EX2 5SE',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_id, region_id) VALUES (
    900006,
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);

INSERT INTO service_finder."user_role" (user_id, role_id) VALUES (
    900006,
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);

INSERT INTO service_finder."content_download"(
    id,
    downloaded_by,
    download_type,
    download_time
)
VALUES(
    900006,
    900006,
    'SEARCH',
    current_timestamp
);

-- Update user 900002 to say it has been updated by user 900005
UPDATE service_finder."user"
SET updated_by = 900005
WHERE id = 900002;

-- Update user 900003 to say it has had its approval state updated by user 900004
UPDATE service_finder."user"
SET approval_status_updated_by = 900004
WHERE id = 900003;
