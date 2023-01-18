CREATE OR REPLACE VIEW service_finder.user_download AS
    SELECT  ud.name,
    ua.email_address,
    ua.email_address_verified,
    ud.approval_status,
    ua.last_logged_in,
    r.name AS region,
    ot.name AS org_type,
    jt.name AS job_type,
    ua.created,
    ua.id AS numeric_user_id,
    ua.identity_provider_id,
    ua.updated,
    ud.terms_and_conditions_accepted,
    ud.rejection_reason,
    string_agg(ro.name::text, ',') AS roles,
    ud.job_name,
    ud.organisation_name,
    ud.postcode,
    ud.telephone_number,
    ua.user_state
    FROM service_finder.user_account ua,
    service_finder.user_details ud,
    service_finder.user_region ur,
    service_finder.region r,
    service_finder.organisation_type ot,
    service_finder.job_type jt,
    service_finder.user_role uro,
    service_finder.role ro
    WHERE ud.user_account_id = ua.id
    AND   ur.user_details_id = ud.id
    AND   r.id = ur.region_id
    AND   ot.id = ud.organisation_type_id
    AND   jt.id = ud.job_type_id
    AND   uro.user_details_id = ud.id
    AND   ro.id = uro.role_id
    GROUP BY ua.id, ud.id, r.id, ot.id, jt.id
