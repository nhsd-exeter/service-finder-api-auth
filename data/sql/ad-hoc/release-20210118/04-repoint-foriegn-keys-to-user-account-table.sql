ALTER TABLE service_finder.content_download
    DROP CONSTRAINT content_download_downloaded_by_fkey;

ALTER TABLE service_finder.content_download
    ADD CONSTRAINT content_download_downloaded_by_fkey
    FOREIGN KEY (downloaded_by)
    REFERENCES service_finder.user_account(id);


ALTER TABLE service_finder.user_referral_reference
    RENAME COLUMN user_id TO user_account_id;

ALTER TABLE service_finder.user_referral_reference
    DROP CONSTRAINT user_referral_reference_user_id_fkey;

ALTER TABLE service_finder.user_referral_reference
    ADD CONSTRAINT user_referral_reference_user_account_id_fkey
    FOREIGN KEY (user_account_id)
    REFERENCES service_finder.user_account(id);


ALTER TABLE service_finder.user_region
    RENAME COLUMN user_id TO user_details_id;

ALTER TABLE service_finder.user_region
    DROP CONSTRAINT user_region_user_id_fkey;

ALTER TABLE service_finder.user_region
    ADD CONSTRAINT user_region_user_details_id_fkey
    FOREIGN KEY (user_details_id)
    REFERENCES service_finder.user_details(id);


ALTER TABLE service_finder.user_role
    RENAME COLUMN user_id TO user_details_id;

ALTER TABLE service_finder.user_role
    DROP CONSTRAINT user_role_user_id_fkey;

ALTER TABLE service_finder.user_role
    ADD CONSTRAINT user_role_user_details_id_fkey
    FOREIGN KEY (user_details_id)
    REFERENCES service_finder.user_details(id);


ALTER TABLE service_finder.user_change
    RENAME COLUMN user_id TO user_account_id;

ALTER TABLE service_finder.user_change
    DROP CONSTRAINT user_change_user_id_fkey;

ALTER TABLE service_finder.user_change
    ADD CONSTRAINT user_change_user_account_id_fkey
    FOREIGN KEY (user_account_id)
    REFERENCES service_finder.user_account(id);

ALTER TABLE service_finder.user_change
    DROP CONSTRAINT user_change_updated_by_fkey;

ALTER TABLE service_finder.user_change
    ADD CONSTRAINT user_change_updated_by_fkey
    FOREIGN KEY (user_account_id)
    REFERENCES service_finder.user_account(id);
