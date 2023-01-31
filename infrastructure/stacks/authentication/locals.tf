locals {

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder-authentication"
    "Product"     = "service-finder-authentication"
    "Environment" = var.profile
    "tag"         = "uec-sf"
    "uc_name"     = "UECSF"
  }

  sf_cognito = {
    cognito_pool_name                    = "${var.service_prefix}-pool"
    email_address                        = var.mail_sender_email #"info@${var.service_finder_zone}"
    s3_email_address                     = var.email_to_go_to_s3
    service_finder_zone                  = var.service_finder_zone
    ui_main_url                          = var.ui_main_url
    service_finder_verification_redirect = var.service_finder_verification_redirect
  }

}
