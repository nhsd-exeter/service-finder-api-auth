ALTER TABLE
  service_finder."user"
ADD
  COLUMN IF NOT EXISTS user_state varchar(8) NOT NULL DEFAULT 'ACTIVE' CHECK(user_state IN ('ACTIVE', 'INACTIVE'));

UPDATE
  service_finder."user"
SET
  user_state = 'INACTIVE',
  approval_status = 'APPROVED',
  approval_status_updated = CURRENT_TIMESTAMP,
  approval_status_updated_by = id
WHERE
  approval_status = 'INACTIVE';

ALTER TABLE
  service_finder."user" DROP CONSTRAINT user_approval_status_check;

ALTER TABLE
  service_finder."user"
ADD
  CONSTRAINT user_approval_status_check CHECK (
    approval_status IN ('PENDING', 'APPROVED', 'REJECTED')
  );
