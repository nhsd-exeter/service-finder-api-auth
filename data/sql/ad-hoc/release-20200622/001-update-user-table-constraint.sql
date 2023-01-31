ALTER TABLE
  service_finder."user" DROP CONSTRAINT user_approval_status_check;

ALTER TABLE
  service_finder."user"
ADD
  CONSTRAINT user_approval_status_check CHECK (
    approval_status IN ('PENDING', 'APPROVED', 'REJECTED', 'INACTIVE')
  );
