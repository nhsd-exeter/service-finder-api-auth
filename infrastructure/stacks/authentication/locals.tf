locals {

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder-authentication"
    "Product"     = "service-finder-authentication"
    "Environment" = var.profile
  }

  sf_cognito = {
    cognito_pool_name = "${var.service_prefix}-pool"
  }

}
