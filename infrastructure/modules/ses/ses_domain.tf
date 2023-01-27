/* This block tells SES that we'd like to register a domain for email sending and receiving */



#***********************************************************************
#    Adding Route53 domain records
#***********************************************************************/



resource "aws_ses_domain_identity" "main" {
  provider = aws.ireland
  domain   = var.domain_name
}

/*This block create an appropriate record in the DNS zone */
resource "aws_route53_record" "ses_verification" {
  count   = var.enable_verification ? 1 : 0
  zone_id = var.route53_zone_id
  name    = "_amazonses.${aws_ses_domain_identity.main.id}"
  type    = "TXT"
  ttl     = "600"
  records = [aws_ses_domain_identity.main.verification_token]
}

/*This block just wait until SES acknowleges the verification of the domain idenitity i.e until sES is able to witness the new DSN record was created */
resource "aws_ses_domain_identity_verification" "main" {
  count      = var.enable_verification ? 1 : 0
  provider   = aws.ireland
  domain     = aws_ses_domain_identity.main.id
  depends_on = [aws_route53_record.ses_verification] //attention to this
}

#
# SES DKIM Verification
#

resource "aws_ses_domain_dkim" "main" {
  provider = aws.ireland
  domain   = aws_ses_domain_identity.main.domain
}
//this is mail from validation
resource "aws_route53_record" "dkim" {
  count   = 3
  zone_id = var.route53_zone_id
  name    = "${aws_ses_domain_dkim.main.dkim_tokens[count.index]}._domainkey.${var.domain_name}"
  type    = "CNAME"
  ttl     = "600"
  records = ["${aws_ses_domain_dkim.main.dkim_tokens[count.index]}.dkim.amazonses.com"]
}

#
# SES MAIL FROM Domain
#

resource "aws_ses_domain_mail_from" "main" {
  provider         = aws.ireland
  domain           = aws_ses_domain_identity.main.domain
  mail_from_domain = "mail.${var.domain_name}"
}
//this is for mail from domain to achieve DMARC validation
# SPF validaton record
resource "aws_route53_record" "spf_mail_from" {
  count   = var.enable_spf_record ? 1 : 0
  zone_id = var.route53_zone_id
  name    = "mail.${var.domain_name}"
  type    = "TXT"
  ttl     = "600"
  records = ["v=spf1 include:amazonses.com ~all"]
}

resource "aws_route53_record" "spf_domain" {
  zone_id = var.route53_zone_id
  name    = var.domain_name
  type    = "TXT"
  ttl     = "600"
  records = ["v=spf1 include:amazonses.com -all"]
}

# Sending MX Record
resource "aws_route53_record" "mx_send_mail_from" {

  zone_id = var.route53_zone_id
  name    = aws_ses_domain_mail_from.main.mail_from_domain
  type    = "MX"
  ttl     = "600"
  records = ["10 feedback-smtp.${var.ireland_region}.amazonses.com"]
}

# Receiving MX Record
resource "aws_route53_record" "mx_receive" {
  count   = var.enable_incoming_email ? 1 : 0
  zone_id = var.route53_zone_id
  name    = var.domain_name
  type    = "MX"
  ttl     = "600"
  records = ["10 inbound-smtp.${var.ireland_region}.amazonaws.com"]
}

#
# DMARC TXT Record
#

resource "aws_route53_record" "txt_dmarc" {
  count   = var.enable_dmarc ? 1 : 0
  zone_id = var.route53_zone_id
  name    = "_dmarc.${var.domain_name}"
  type    = "TXT"
  ttl     = "600"
  records = ["v=DMARC1; p=none; rua=mailto:${var.dmarc_rua};"]
}

#****************************************************************************************************
# SES Receipt Rules
#**************************************************************************************************

resource "aws_ses_receipt_rule_set" "domain_rule_set" {
  provider      = aws.ireland
  rule_set_name = var.ses_rule_set
}

resource "aws_ses_receipt_rule" "check_recipients" {
  provider      = aws.ireland
  count         = var.enable_incoming_email ? 1 : 0
  name          = "${var.domain_name}-s3-rule"
  rule_set_name = aws_ses_receipt_rule_set.domain_rule_set.rule_set_name
  recipients    = [var.s3_email_recipient]
  enabled       = true
  scan_enabled  = true

  s3_action {
    position          = 1
    bucket_name       = var.receive_s3_bucket
    object_key_prefix = var.receive_s3_prefix
  }

  depends_on = [aws_s3_bucket.ses_bucket, aws_s3_bucket_policy.ses_bucket]
}

resource "aws_ses_active_receipt_rule_set" "domain_rule_set" {
  provider      = aws.ireland
  rule_set_name = aws_ses_receipt_rule_set.domain_rule_set.rule_set_name
}

resource "aws_ses_email_identity" "s3_email" {
  provider = aws.ireland
  email    = var.s3_email_recipient

}
