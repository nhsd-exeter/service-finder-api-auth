UPDATE service_finder."user"
    SET updated = (
        SELECT updated FROM service_finder."user_change"
            WHERE "user_change".user_id = "user".id
            ORDER BY "user_change".updated DESC FETCH FIRST 1 ROWS ONLY
    )
    WHERE EXISTS (
        SELECT updated FROM service_finder."user_change"
            WHERE "user_change".user_id = "user".id
    );
