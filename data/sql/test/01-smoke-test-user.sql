/* User data */
INSERT INTO
  service_finder."user_account" (
    id,
    created,
    updated,
    updated_by,
    identity_provider_id,
    email_address_verified,
    email_address,
    last_logged_in,
    user_state
  )
VALUES
  (
    nextval('service_finder.user_account_id_seq'),
    current_timestamp,
    current_timestamp,
    (
      select
        last_value
      from
        service_finder.user_account_id_seq
    ),
    'a1e24eb3-f6da-49d0-b1a6-db760c40c627',
    -- the identity_provider_id gets replaced in the pipeline to match the Cognito sub for this user
    true,
    'service-finder-smoke-test@nhs.net',
    current_timestamp,
    'ACTIVE'
  );

INSERT INTO
  service_finder."user_details" (
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
  )
VALUES
  (
    nextval('service_finder.user_details_id_seq'),
    (
      SELECT
        id
      FROM
        service_finder."user_account"
      WHERE
        email_address = 'service-finder-smoke-test@nhs.net'
    ),
    current_timestamp,
    '01234567890',
    'Smoke test user',
    'Test job name',
    'Test organisation name',
    (
      SELECT
        id
      FROM
        "job_type"
      WHERE
        code = 'ADMINISTRATOR'
    ),
    (
      SELECT
        id
      FROM
        "organisation_type"
      WHERE
        code = 'NHS_ENGLAND'
    ),
    'APPROVED',
    current_timestamp,
    (
      SELECT
        id
      FROM
        "user_account"
      WHERE
        email_address = 'service-finder-admin@nhs.net'
    ),
    'EX1 1SR',
    current_timestamp
  );

/* User-Region join table data */
INSERT INTO
  "user_region" (user_details_id, region_id)
VALUES
  (
    (
      SELECT
        ud.id
      FROM
        service_finder."user_details" ud,
        service_finder."user_account" ua
      WHERE
        ua.email_address = 'service-finder-admin@nhs.net'
        and ud.user_account_id = ua.id
    ),
    (
      SELECT
        id
      FROM
        "region"
      WHERE
        code = 'SOUTH_WEST'
    )
  );

/* User-Role join table data */
INSERT INTO
  "user_role" (user_details_id, role_id)
VALUES
  (
    (
      SELECT
        ud.id
      FROM
        service_finder."user_details" ud,
        service_finder."user_account" ua
      WHERE
        ua.email_address = 'service-finder-admin@nhs.net'
        and ud.user_account_id = ua.id
    ),
    (
      SELECT
        id
      FROM
        "role"
      WHERE
        code = 'SEARCH'
    )
  );
