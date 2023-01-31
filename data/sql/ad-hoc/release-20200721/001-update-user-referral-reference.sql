CREATE
OR REPLACE FUNCTION pseudo_encrypt(VALUE bigint) returns int AS $ $ DECLARE l1 int;

l2 int;

r1 int;

r2 int;

i int := 0;

BEGIN l1 := (VALUE > > 16) & 65535;

r1 := VALUE & 65535;

WHILE i < 3 LOOP l2 := r1;

r2 := l1 # ((((1366 * r1 + 150889) % 714025) / 714025.0) * 32767)::int;
l1 := l2;

r1 := r2;

i := i + 1;

END LOOP;

RETURN ((r1 < < 16) + l1);

END;

$ $ LANGUAGE plpgsql strict immutable;

CREATE SEQUENCE IF NOT EXISTS service_finder."user_referral_reference_sequence";

ALTER DEFAULT PRIVILEGES GRANT USAGE,
SELECT
  ON SEQUENCES TO service_finder;

CREATE TABLE IF NOT EXISTS service_finder."user_referral_reference" (
  id bigint primary key default pseudo_encrypt(
    nextval(
      'service_finder.user_referral_reference_sequence'
    )
  ),
  user_id bigserial NOT NULL REFERENCES service_finder."user"(id),
  referral_reference_code varchar(12) NOT NULL DEFAULT 'AAA-AAAA-AAA'
);
