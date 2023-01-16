# There are issues at the moment with this resource in the AWS Terraform provider
# that mean that *updating* any parameters in the User Pool might cause Terraform
# to fail. The current workaround is to delete your user pool and re-run Terraform
# again. When this gets to production it will be created from scratch so I don't
# foresee any issues.
resource "aws_cognito_user_pool" "pool" {
  name                     = var.cognito_pool_name
  username_attributes      = ["email"]
  auto_verified_attributes = ["email"]
  user_pool_add_ons {
    advanced_security_mode = "OFF"
  }
  email_configuration {
    source_arn = "arn:aws:ses:${var.ireland_region}:${var.aws_account}:identity/${var.email_address}"
    #source_arn = "arn:aws:ses:${var.london_region}:${var.aws_account}:identity/${var.email_address}"

    email_sending_account = "DEVELOPER"
  }


  password_policy {
    minimum_length                   = 8
    require_lowercase                = false
    require_numbers                  = false
    require_symbols                  = false
    require_uppercase                = false
    temporary_password_validity_days = 7
  }

  lambda_config {
    custom_message = aws_lambda_function.custom_registration_verification_email.arn
  }

  tags = var.tags
}

# Note: when no read_attributes are specified, all are readable by default
resource "aws_cognito_user_pool_client" "client" {
  name                   = "auth-pool-client"
  user_pool_id           = aws_cognito_user_pool.pool.id
  explicit_auth_flows    = ["USER_PASSWORD_AUTH"]
  generate_secret        = true
  refresh_token_validity = 1
}
# explicit_auth_flows    = ["USER_PASSWORD_AUTH","ALLOW_REFRESH_TOKEN_AUTH"] Enable instead once all secrets removed from code and cognito and secrets have been reset
