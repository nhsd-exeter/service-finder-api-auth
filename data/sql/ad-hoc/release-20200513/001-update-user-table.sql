ALTER TABLE service_finder."user" ADD COLUMN IF NOT EXISTS updated_by bigint REFERENCES service_finder."user"(id);

-- NHS users who have not yet verified
-- Due to a code bug the approval_status_updated is set to before the created date.
UPDATE service_finder."user"
SET updated_by = id
WHERE email_address_verified IS FALSE
AND approval_status_updated < created
AND updated_by IS NULL;

--  Non NHS users who have not yet verified
UPDATE service_finder."user"
SET updated_by = id
WHERE email_address_verified IS FALSE
AND created = updated
AND updated_by IS NULL;

-- Non NHS users not yet approved (some could already have been updated due to the condition above.)
UPDATE service_finder."user"
SET updated_by = id
WHERE approval_status = 'PENDING'
AND updated_by IS NULL;

-- Non NHS users who have not been changed since they were approved.
UPDATE service_finder."user"
SET updated_by = approval_status_updated_by
WHERE approval_status = 'APPROVED'
AND approval_status_updated_by IS NOT NULL
AND approval_status_updated = updated
AND updated_by IS NULL;

-- Non NHS users that have been changed since they were approved,
-- leave as null because we don't know who made the final change.

-- NHS users who have been verified, I do not know if the updated date
-- was when they verfied their e-mail, or when an admin user updated them.
