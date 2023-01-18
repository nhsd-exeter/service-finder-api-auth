CREATE TABLE IF NOT EXISTS service_finder."event" (
    id                      bigserial       PRIMARY KEY,
    created                 timestamp       NOT NULL,
    actor_email_address     varchar(255)    NOT NULL,
    type                    varchar(50)     NOT NULL,
    message                 varchar(1000)
);
