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
)
SELECT
    u.id,
    u.created,
    u.updated,
    null,    -- this is initially null, because we may not have inserted the updated_by user yet.
    u.identity_provider_id,
    u.email_address_verified,
    u.email_address,
    u.last_logged_in,
    u.user_state
FROM service_finder."user" u
WHERE NOT EXISTS (SELECT 1
    FROM service_finder."user" u2
    WHERE lower(u2.email_address) = lower(u.email_address)
    AND u2.id < u.id)
ORDER BY u.id ASC;

-- Now go back and update the updated_by field
UPDATE service_finder."user_account" ua
SET updated_by = (SELECT u.updated_by
    FROM service_finder."user" u
    WHERE u.id = ua.id);

INSERT INTO service_finder."user_details" (
    id,
    user_account_id,
    created,
    telephone_number,
    name,
    job_name,
    job_type_id,
    job_type_other,
    organisation_name,
    organisation_type_id,
    organisation_type_other,
    approval_status,
    approval_status_updated,
    approval_status_updated_by,
    rejection_reason,
    postcode,
    terms_and_conditions_accepted
)
SELECT
    u.id,
    ua.id,
    u.created,
    u.telephone_number,
    u.name,
    u.job_name,
    u.job_type_id,
    u.job_type_other,
    u.organisation_name,
    u.organisation_type_id,
    u.organisation_type_other,
    u.approval_status,
    u.approval_status_updated,
    u.approval_status_updated_by,
    u.rejection_reason,
    u.postcode,
    u.terms_and_conditions_accepted
FROM service_finder."user" u,
    service_finder."user_account" ua
WHERE ua.email_address = u.email_address
AND NOT EXISTS (SELECT 1
    FROM service_finder."user" u2
    WHERE lower(u2.email_address) = lower(u.email_address)
    AND u2.id < u.id)
ORDER BY u.id ASC;

-- Align sequences
SELECT setval('service_finder.user_account_id_seq', (SELECT MAX(id) FROM service_finder."user_account"), true);
SELECT setval('service_finder.user_details_id_seq', (SELECT MAX(id) FROM service_finder."user_account"), true);

-- Now we must clear out any duplicate entries from the user_region, user_roles and user_change tables. These happen for
-- folks who have registered with the same email address twice. Remember that the record we want will have
-- the lowest user_id.
DELETE FROM service_finder.user_region
WHERE user_id IN (
  SELECT u.id
  FROM service_finder.user u
  WHERE EXISTS (
    SELECT 1
    FROM service_finder.user u2
    WHERE lower(u2.email_address) = lower(u.email_address)
    AND u2.id < u.id)
);

DELETE FROM service_finder.user_role
WHERE user_id IN (
  SELECT u.id
  FROM service_finder.user u
  WHERE EXISTS (
    SELECT 1
    FROM service_finder.user u2
    WHERE lower(u2.email_address) = lower(u.email_address)
    AND u2.id < u.id)
);

DELETE FROM service_finder.user_change
WHERE user_id IN (
  SELECT u.id
  FROM service_finder.user u
  WHERE EXISTS (
    SELECT 1
    FROM service_finder.user u2
    WHERE lower(u2.email_address) = lower(u.email_address)
    AND u2.id < u.id)
);

COMMIT;
