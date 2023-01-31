CREATE TABLE service_finder."role" (
    id          bigserial       PRIMARY KEY,
    name        varchar(255)    NOT NULL,
    code        varchar(255)    NOT NULL    UNIQUE
);

CREATE TABLE service_finder."region" (
    id          bigserial       PRIMARY KEY,
    name        varchar(255)    NOT NULL,
    code        varchar(255)    NOT NULL    UNIQUE
);

CREATE TABLE service_finder."organisation_type" (
    id          bigserial       PRIMARY KEY,
    name        varchar(255)    NOT NULL,
    code        varchar(255)    NOT NULL    UNIQUE
);

CREATE TABLE service_finder."job_type" (
    id          bigserial       PRIMARY KEY,
    name        varchar(255)    NOT NULL,
    code        varchar(255)    NOT NULL    UNIQUE
);

CREATE TABLE service_finder."user" (
    id                                  bigserial       PRIMARY KEY,
    created                             timestamp       NOT NULL,
    updated                             timestamp       NOT NULL,
    updated_by                          bigint          REFERENCES service_finder."user"(id),
    identity_provider_id                varchar(255)    NOT NULL UNIQUE,
    email_address_verified              boolean         NOT NULL DEFAULT false,
    email_address                       varchar(255)    NOT NULL UNIQUE,
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
    approval_status_updated_by          bigint          REFERENCES service_finder."user"(id),
    rejection_reason                    varchar(500),
    postcode                            varchar(8),
    terms_and_conditions_accepted       timestamp,
    last_logged_in                      timestamp,
    user_state                          varchar(8)      NOT NULL DEFAULT 'ACTIVE' CHECK(user_state IN ('ACTIVE','INACTIVE'))
);

CREATE TABLE service_finder."user_account" (
    id                                  bigserial       PRIMARY KEY,
    created                             timestamp       NOT NULL,
    updated                             timestamp       NOT NULL,
    updated_by                          bigint          REFERENCES service_finder."user_account"(id),
    identity_provider_id                varchar(255)    NOT NULL UNIQUE,
    email_address_verified              boolean         NOT NULL DEFAULT false,
    email_address                       varchar(255)    NOT NULL UNIQUE,
    last_logged_in                      timestamp,
    user_state                          varchar(8)      NOT NULL DEFAULT 'INACTIVE' CHECK(user_state IN ('ACTIVE','INACTIVE')),
    inactive_date                       timestamp
);

CREATE TABLE service_finder."user_details" (
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

CREATE TABLE service_finder.user_change (
    id              bigserial PRIMARY KEY,
    user_id         bigserial  NOT NULL REFERENCES service_finder."user"(id),
    updated         timestamp NOT NULL,
    updated_by      bigint  NOT NULL REFERENCES service_finder."user"(id),
    original_value  varchar(255) NOT NULL,
    new_value       varchar(255) NOT NULL,
    field_type      varchar(30) NOT NULL
);

CREATE TABLE service_finder."user_role" (
    user_id     bigint     REFERENCES service_finder."user"(id),
    role_id     bigint     REFERENCES service_finder."role"(id),
    PRIMARY KEY(user_id, role_id)
);

CREATE TABLE service_finder."user_region" (
    user_id     bigint     REFERENCES service_finder."user"(id),
    region_id   bigint     REFERENCES service_finder."region"(id),
    PRIMARY KEY(user_id, region_id)
);

CREATE TABLE service_finder."login_attempt" (
    id               bigserial       PRIMARY KEY,
    created          timestamp       NOT NULL,
    updated          timestamp       NOT NULL,
    email_address    varchar(255)    NOT NULL UNIQUE,
    attempts         int             NOT NULL
);

CREATE TABLE service_finder."event" (
    id                      bigserial       PRIMARY KEY,
    created                 timestamp       NOT NULL,
    actor_email_address     varchar(255)    NOT NULL,
    type                    varchar(50)     NOT NULL,
    message                 varchar(1000)
);


CREATE TABLE service_finder."user_referral_reference" (
    id                          bigint primary key default pseudo_encrypt(nextval('service_finder.user_referral_reference_sequence')),
    user_id                     bigserial  NOT NULL REFERENCES service_finder."user"(id),
    referral_reference_code     varchar(12) NOT NULL DEFAULT 'AAA-AAAA-AAA'
);

CREATE TABLE service_finder."content_download" (
    id                          bigserial PRIMARY KEY,
    downloaded_by               bigserial NOT NULL REFERENCES service_finder."user"(id),
    download_type               varchar(255) NOT NULL,
    download_time               timestamp NOT NULL
);

CREATE TABLE service_finder."content_download_filter" (
    id                          bigserial PRIMARY KEY,
    content_download_id         bigserial NOT NULL REFERENCES service_finder."content_download"(id),
    name                        varchar(255) NOT NULL,
    value                       varchar(255) NOT NULL DEFAULT ''
);

CREATE TABLE service_finder."saved_location" (
  id                            bigserial PRIMARY KEY,
  user_account_id               bigserial  NOT NULL REFERENCES service_finder."user_account"(id),
  dated_added                   timestamp NOT NULL,
  description                   varchar(75) NULL,
  latitude                      float8 NULL,
  longitude                     float8 NULL,
  postcode                      varchar(255) NOT NULL
);
