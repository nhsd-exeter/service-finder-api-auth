locals {

  standard_tags = {
    "Programme"   = "uec"
    "Service"     = "service-finder"
    "Product"     = "service-finder"
    "Environment" = var.profile
  }

  # Simple Email Service
  sf_ses = {
    dmarc_rua   = var.email_to_go_to_s3
    domain_name = var.sf_domain_name
    #recipient_addresses = [var.email_accounts]
    s3_email          = var.email_to_go_to_s3
    receive_s3_bucket = "uec-${var.base_service_prefix}-ses"
    receive_s3_prefix = "received_emails"
    ses_rule_set      = "${var.sf_domain_name}_rule_set"
  }

}
