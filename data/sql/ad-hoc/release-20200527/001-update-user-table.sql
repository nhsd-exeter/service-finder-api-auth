CREATE TABLE IF NOT EXISTS service_finder.user_change (
    id              bigserial PRIMARY KEY,
    user_id         bigserial  NOT NULL REFERENCES service_finder."user"(id),
    updated         timestamp NOT NULL,
    updated_by      bigint  NOT NULL REFERENCES service_finder."user"(id),
    original_value  varchar(255) NOT NULL,
    new_value       varchar(255) NOT NULL,
    field_type      varchar(30) NOT NULL
);
