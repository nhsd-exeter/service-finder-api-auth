CREATE TABLE IF NOT EXISTS service_finder."content_download" (
    id                          bigserial PRIMARY KEY,
    downloaded_by               bigserial NOT NULL REFERENCES service_finder."user"(id),
    download_type               varchar(255) NOT NULL,
    download_time               timestamp NOT NULL
);

CREATE TABLE IF NOT EXISTS service_finder."content_download_filter" (
    id                          bigserial PRIMARY KEY,
    content_download_id         bigserial NOT NULL REFERENCES service_finder."content_download"(id),
    name                        varchar(255) NOT NULL,
    value                       varchar(255) NOT NULL DEFAULT ''
);
