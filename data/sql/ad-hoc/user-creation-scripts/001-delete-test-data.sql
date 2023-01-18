DELETE FROM service_finder."user_role"
WHERE user_details_id BETWEEN 70001 AND 705000;

DELETE FROM service_finder."user_region"
WHERE user_details_id BETWEEN 700001 AND 705000;

DELETE FROM service_finder."user_details"
WHERE id BETWEEN 700001 AND 705000;

DELETE FROM service_finder."user_account"
WHERE id BETWEEN 700001 AND 705000;
