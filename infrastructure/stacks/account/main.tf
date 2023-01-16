resource "aws_ses_domain_identity" "primary" {
  domain = data.aws_route53_zone.zone.name
}

resource "aws_route53_record" "ses_verif" {
  zone_id = data.aws_route53_zone.zone.zone_id
  name    = "_amazonses.${aws_ses_domain_identity.primary.id}"
  type    = "TXT"
  ttl     = "600"
  records = [aws_ses_domain_identity.primary.verification_token]
}

resource "aws_ses_domain_identity_verification" "ses_verif" {
  domain = aws_ses_domain_identity.primary.id
  depends_on = [aws_route53_record.ses_verif]
}

resource "aws_route53_record" "email" {
  zone_id = data.aws_route53_zone.zone.zone_id
  name    = data.aws_route53_zone.zone.name
  type    = "MX"
  ttl     = "600"
  records = ["10 inbound-smtp.${var.ireland_region}.amazonaws.com"]
}

resource "aws_ses_email_identity" "email" {
  email = var.email_to_go_to_s3
}
