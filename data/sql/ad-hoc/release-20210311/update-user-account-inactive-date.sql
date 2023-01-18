ALTER TABLE service_finder."user_account" ADD COLUMN IF NOT EXISTS inactive_date timestamp;
ALTER TABLE service_finder."user_account" ALTER user_state DROP DEFAULT;
ALTER TABLE service_finder."user_account" ALTER user_state SET DEFAULT 'INACTIVE';

UPDATE service_finder."user_account"
    SET inactive_date = updated
    WHERE user_state = 'INACTIVE';
