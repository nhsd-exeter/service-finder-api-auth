SELECT id, email_address FROM service_finder.user WHERE id IN (SELECT user_id FROM service_finder.user_region GROUP BY user_id HAVING COUNT(user_id) > 1);
