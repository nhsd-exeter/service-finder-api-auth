module "cognito" {
  source                               = "../../modules/cognito"
  aws_account                          = var.aws_account_id
  profile                              = var.profile
  service_prefix                       = var.service_prefix
  cognito_pool_name                    = local.sf_cognito["cognito_pool_name"]
  tags                                 = local.standard_tags
  email_address                        = local.sf_cognito["email_address"]
  service_finder_zone                  = local.sf_cognito["service_finder_zone"]
  s3_email_address                     = local.sf_cognito["s3_email_address"]
  ui_main_url                          = local.sf_cognito["ui_main_url"]
  service_finder_verification_redirect = local.sf_cognito["service_finder_verification_redirect"]
  // depends_on                           = [aws_ses_email_identity.s3_email]
}
