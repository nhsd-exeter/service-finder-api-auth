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
) SELECT
    seq,
    current_timestamp,
    current_timestamp,
    seq,
    seq,
    true,
    seq || 'search@nhs.net',
    current_timestamp,
    'ACTIVE'
FROM GENERATE_SERIES(700001, 705000) seq;

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
) SELECT
    seq,
    seq,
    current_timestamp,
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (SELECT id FROM service_finder."job_type" WHERE code = 'OCCUPATIONAL_THERAPIST'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'GP_SURGERY'),
    'APPROVED',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
FROM GENERATE_SERIES(700001, 705000) seq;

INSERT INTO service_finder."user_region" (user_details_id, region_id) SELECT
    seq,
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
FROM GENERATE_SERIES(700001, 705000) seq;

INSERT INTO service_finder."user_role" (user_details_id, role_id) SELECT
    seq,
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
FROM GENERATE_SERIES(700001, 705000) seq;
