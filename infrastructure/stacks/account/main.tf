module "ses" {
  source             = "../../modules/ses"
  aws_account        = var.aws_account_id
  aws_region         = var.ireland_region // ses receving emails works only in ireland
  service_prefix     = var.base_service_prefix
  route53_zone_id    = data.terraform_remote_state.route53.outputs.dns_zone_id
  dmarc_rua          = local.sf_ses["dmarc_rua"]
  domain_name        = local.sf_ses["domain_name"]
  s3_email_recipient = local.sf_ses["s3_email"]
  receive_s3_bucket  = local.sf_ses["receive_s3_bucket"]
  receive_s3_prefix  = local.sf_ses["receive_s3_prefix"]
  ses_rule_set       = local.sf_ses["ses_rule_set"]
  tags               = local.standard_tags
}

/* This block tells SES that we'd like to register a domain for email sending and receiving */
