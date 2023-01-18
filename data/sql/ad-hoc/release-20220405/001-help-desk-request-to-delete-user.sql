delete from
  user_region ur
where
  user_details_id in (
    select
      id
    from
      user_details ud
    where
      user_account_id in (
        select
          id
        from
          user_account ua
        where
          ua.email_address = 'd.elliott6@nhs.net'
      )
  );

delete from
  user_role ur
where
  user_details_id in (
    select
      id
    from
      user_details ud
    where
      user_account_id in (
        select
          id
        from
          user_account ua
        where
          ua.email_address = 'd.elliott6@nhs.net'
      )
  );

delete from
  user_details ud
where
  user_account_id in (
    select
      id
    from
      user_account ua
    where
      ua.email_address = 'd.elliott6@nhs.net'
  );

delete from
  user_account
where
  email_address = 'd.elliott6@nhs.net'
