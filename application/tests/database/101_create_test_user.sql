/* unconfirmed@nhs.net */
INSERT INTO
  "user" (
    created,
    updated,
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
  )
VALUES
  (
    current_timestamp,
    current_timestamp,
    'SUB_ID_UNCONFIRMED',
    false,
    'unconfirmed@nhs.net',
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (
      SELECT
        id
      FROM
        "job_type"
      WHERE
        code = 'OCCUPATIONAL_THERAPIST'
    ),
    (
      SELECT
        id
      FROM
        "organisation_type"
      WHERE
        code = 'GP_SURGERY'
    ),
    'APPROVED',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
  );

INSERT INTO
  "user_region" (user_id, region_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'unconfirmed@nhs.net'
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

/* search@nhs.net */
INSERT INTO
  "user" (
    created,
    updated,
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
  )
VALUES
  (
    current_timestamp,
    current_timestamp,
    'SUB_ID_SEARCH',
    true,
    'search@nhs.net',
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (
      SELECT
        id
      FROM
        "job_type"
      WHERE
        code = 'OCCUPATIONAL_THERAPIST'
    ),
    (
      SELECT
        id
      FROM
        "organisation_type"
      WHERE
        code = 'GP_SURGERY'
    ),
    'APPROVED',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
  );

INSERT INTO
  "user_region" (user_id, region_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'search@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'search@nhs.net'
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

/* reporter@nhs.net */
INSERT INTO
  "user" (
    created,
    updated,
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
  )
VALUES
  (
    current_timestamp,
    current_timestamp,
    'SUB_ID_REPORTER',
    true,
    'reporter@nhs.net',
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (
      SELECT
        id
      FROM
        "job_type"
      WHERE
        code = 'OCCUPATIONAL_THERAPIST'
    ),
    (
      SELECT
        id
      FROM
        "organisation_type"
      WHERE
        code = 'GP_SURGERY'
    ),
    'APPROVED',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
  );

INSERT INTO
  "user_region" (user_id, region_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'reporter@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'reporter@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'reporter@nhs.net'
    ),
    (
      SELECT
        id
      FROM
        "role"
      WHERE
        code = 'REPORTER'
    )
  );

/* approver@nhs.net */
INSERT INTO
  "user" (
    created,
    updated,
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
  )
VALUES
  (
    current_timestamp,
    current_timestamp,
    'SUB_ID_APPROVER',
    true,
    'approver@nhs.net',
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (
      SELECT
        id
      FROM
        "job_type"
      WHERE
        code = 'OCCUPATIONAL_THERAPIST'
    ),
    (
      SELECT
        id
      FROM
        "organisation_type"
      WHERE
        code = 'GP_SURGERY'
    ),
    'APPROVED',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
  );

INSERT INTO
  "user_region" (user_id, region_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'approver@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'approver@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'approver@nhs.net'
    ),
    (
      SELECT
        id
      FROM
        "role"
      WHERE
        code = 'APPROVER'
    )
  );

/* admin@nhs.net */
INSERT INTO
  "user" (
    created,
    updated,
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
  )
VALUES
  (
    current_timestamp,
    current_timestamp,
    'SUB_ID_ADMIN',
    true,
    'admin@nhs.net',
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (
      SELECT
        id
      FROM
        "job_type"
      WHERE
        code = 'OCCUPATIONAL_THERAPIST'
    ),
    (
      SELECT
        id
      FROM
        "organisation_type"
      WHERE
        code = 'GP_SURGERY'
    ),
    'APPROVED',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
  );

INSERT INTO
  "user_region" (user_id, region_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'admin@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'admin@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'admin@nhs.net'
    ),
    (
      SELECT
        id
      FROM
        "role"
      WHERE
        code = 'ADMIN'
    )
  );

/* super@nhs.net */
INSERT INTO
  "user" (
    created,
    updated,
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
  )
VALUES
  (
    current_timestamp,
    current_timestamp,
    'SUB_ID_SUPER',
    true,
    'super@nhs.net',
    '01234567890',
    'Bill Test',
    'Senior Occupational Therapist',
    'Barnfield Hill Surgery',
    (
      SELECT
        id
      FROM
        "job_type"
      WHERE
        code = 'OCCUPATIONAL_THERAPIST'
    ),
    (
      SELECT
        id
      FROM
        "organisation_type"
      WHERE
        code = 'GP_SURGERY'
    ),
    'APPROVED',
    current_timestamp,
    1,
    'EX1 1SR',
    current_timestamp
  );

INSERT INTO
  "user_region" (user_id, region_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'super@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'super@nhs.net'
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

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'super@nhs.net'
    ),
    (
      SELECT
        id
      FROM
        "role"
      WHERE
        code = 'APPROVER'
    )
  );

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'super@nhs.net'
    ),
    (
      SELECT
        id
      FROM
        "role"
      WHERE
        code = 'REPORTER'
    )
  );

INSERT INTO
  "user_role" (user_id, role_id)
VALUES
  (
    (
      SELECT
        id
      FROM
        "user"
      WHERE
        email_address = 'super@nhs.net'
    ),
    (
      SELECT
        id
      FROM
        "role"
      WHERE
        code = 'ADMIN'
    )
  );
