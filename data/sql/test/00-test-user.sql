/* unconfirmed@nhs.net */

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
    'SUB_ID_UNCONFIRMED',
    false,
    'unconfirmed@nhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'unconfirmed@nhs.net'),
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
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'unconfirmed@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);


/* search@nhs.net */

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
    'SUB_ID_SEARCH',
    true,
    'search@nhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'search@nhs.net'),
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
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'search@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'search@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);


/* reporter@nhs.net */

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
    'SUB_ID_REPORTER',
    true,
    'reporter@nhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'reporter@nhs.net'),
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
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'reporter@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'reporter@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'reporter@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'REPORTER')
);


/* approver@nhs.net */

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
    current_timestamp  - INTERVAL '2 DAY',
    current_timestamp,
    (select last_value from service_finder.user_account_id_seq),
    'SUB_ID_APPROVER',
    true,
    'approver@nhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'approver@nhs.net'),
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
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'approver@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'approver@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'approver@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'APPROVER')
);


/* admin@nhs.net */

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
    current_timestamp  - INTERVAL '1 DAY',
    current_timestamp,
    (select last_value from service_finder.user_account_id_seq),
    'SUB_ID_ADMIN',
    true,
    'admin@nhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'admin@nhs.net'),
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
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'admin@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'admin@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'admin@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);


/* super@nhs.net */

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
    'SUB_ID_SUPER',
    true,
    'super@nhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'super@nhs.net'),
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
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'super@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'super@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'super@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'APPROVER')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'super@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'REPORTER')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'super@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'ADMIN')
);


/* Approved and confirmed */

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
    'APPROVED_CONFIRMED',
    true,
    'approved_confirmed@nhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'approved_confirmed@nhs.net'),
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
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'approved_confirmed@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'approved_confirmed@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);


/* Unapproved and confirmed */

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
    'UNAPPROVED_CONFIRMED',
    true,
    'unapproved_confirmed@nhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'unapproved_confirmed@nhs.net'),
    current_timestamp,
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (SELECT id FROM service_finder."job_type" WHERE code = 'OCCUPATIONAL_THERAPIST'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'GP_SURGERY'),
    'PENDING',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'unapproved_confirmed@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'unapproved_confirmed@nhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);



/* Unapproved and confirmed non-nhs */

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
    'UNAPPROVED_CONFIRMED_NON_NHS',
    true,
    'unapproved_confirmed@nonnhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'unapproved_confirmed@nonnhs.net'),
    current_timestamp,
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (SELECT id FROM service_finder."job_type" WHERE code = 'OCCUPATIONAL_THERAPIST'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'GP_SURGERY'),
    'PENDING',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'unapproved_confirmed@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'unapproved_confirmed@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);


/* Unapproved and confirmed non-nhs Test1*/

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
    'TEST1_CONFIRMED_NON_NHS',
    true,
    'test_user1@nonnhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'test_user1@nonnhs.net'),
    current_timestamp,
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (SELECT id FROM service_finder."job_type" WHERE code = 'OCCUPATIONAL_THERAPIST'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'GP_SURGERY'),
    'PENDING',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user1@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user1@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);


/* Unapproved and confirmed non-nhs Test2*/

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
    'TEST2_CONFIRMED_NON_NHS',
    true,
    'test_user2@nonnhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'test_user2@nonnhs.net'),
    current_timestamp,
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (SELECT id FROM service_finder."job_type" WHERE code = 'OCCUPATIONAL_THERAPIST'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'GP_SURGERY'),
    'PENDING',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user2@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user2@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);


/* Unapproved and confirmed non-nhs Test3*/

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
    'TEST3_CONFIRMED_NON_NHS',
    true,
    'test_user3@nonnhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'test_user3@nonnhs.net'),
    current_timestamp,
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (SELECT id FROM service_finder."job_type" WHERE code = 'OCCUPATIONAL_THERAPIST'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'GP_SURGERY'),
    'PENDING',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user3@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user3@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);

/* Unapproved and confirmed non-nhs Test4*/

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
    'TEST4_CONFIRMED_NON_NHS',
    true,
    'test_user4@nonnhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'test_user4@nonnhs.net'),
    current_timestamp,
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (SELECT id FROM service_finder."job_type" WHERE code = 'OCCUPATIONAL_THERAPIST'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'GP_SURGERY'),
    'PENDING',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user4@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user4@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);

/* Unapproved and confirmed non-nhs Test5*/

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
    'TEST5_CONFIRMED_NON_NHS',
    true,
    'test_user5@nonnhs.net',
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
    (SELECT id FROM service_finder."user_account" WHERE email_address = 'test_user5@nonnhs.net'),
    current_timestamp,
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (SELECT id FROM service_finder."job_type" WHERE code = 'OCCUPATIONAL_THERAPIST'),
    (SELECT id FROM service_finder."organisation_type" WHERE code = 'GP_SURGERY'),
    'PENDING',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
);

INSERT INTO service_finder."user_region" (user_details_id, region_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user5@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."region" WHERE code = 'SOUTH_WEST')
);
INSERT INTO service_finder."user_role" (user_details_id, role_id) VALUES (
    (SELECT ud.id FROM service_finder."user_details" ud, service_finder."user_account" ua
    WHERE ua.email_address = 'test_user5@nonnhs.net' and ud.user_account_id = ua.id),
    (SELECT id FROM service_finder."role" WHERE code = 'SEARCH')
);
