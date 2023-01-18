CREATE TABLE IF NOT EXISTS service_finder."saved_location" (
  id                            bigserial PRIMARY KEY,
  user_account_id               bigserial  NOT NULL REFERENCES service_finder."user_account"(id),
  dated_added                   timestamp NOT NULL,
  description                   varchar(75) NULL,
  latitude                      float8 NULL,
  longitude                     float8 NULL,
  postcode                      varchar(255) NOT NULL
);
