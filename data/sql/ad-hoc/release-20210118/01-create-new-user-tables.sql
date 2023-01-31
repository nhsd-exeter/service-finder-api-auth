CREATE TABLE IF NOT EXISTS service_finder."user_account" (
    id                                  bigserial       PRIMARY KEY,
    created                             timestamp       NOT NULL,
    updated                             timestamp       NOT NULL,
    updated_by                          bigint          REFERENCES service_finder."user_account"(id),
    identity_provider_id                varchar(255)    NOT NULL UNIQUE,
    email_address_verified              boolean         NOT NULL DEFAULT false,
    email_address                       varchar(255)    NOT NULL UNIQUE,
    last_logged_in                      timestamp,
    user_state                          varchar(8)      NOT NULL DEFAULT 'ACTIVE' CHECK(user_state IN ('ACTIVE','INACTIVE'))
);

CREATE TABLE IF NOT EXISTS service_finder."user_details" (
    id                                  bigserial       PRIMARY KEY,
    user_account_id                     bigserial       NOT NULL REFERENCES service_finder."user_account"(id),
    created                             timestamp       NOT NULL,
    telephone_number                    varchar(20),
    name                                varchar(255)    NOT NULL,
    job_name                            varchar(255)    NOT NULL,
    job_type_id                         bigint          NOT NULL REFERENCES service_finder."job_type"(id),
    job_type_other                      varchar(255),
    organisation_name                   varchar(255)    NOT NULL,
    organisation_type_id                bigint          NOT NULL REFERENCES service_finder."organisation_type"(id),
    organisation_type_other             varchar(255),
    approval_status                     varchar(8)      NOT NULL DEFAULT 'PENDING' CHECK(approval_status IN ('PENDING','APPROVED','REJECTED')),
    approval_status_updated             timestamp,
    approval_status_updated_by          bigint          REFERENCES service_finder."user_account"(id),
    rejection_reason                    varchar(500),
    postcode                            varchar(8),
    terms_and_conditions_accepted       timestamp
);

CREATE TABLE service_finder."user_compare_tmp" (
    identity_provider_id varchar(255),
    email_address varchar(255),
    email_address_verified boolean,
    created_date timestamp
);
