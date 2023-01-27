# General variables
variable "aws_account" {
  description = "AWS account identifier."
}

variable "aws_region" {
  description = "AWS region."
}

variable "ireland_region" {
  description = "Ireland region."
  default     = "eu-west-1"
}


variable "tags" {
  type        = map(string)
  description = "A list of standard tags for any given resource."
}

# SES variables
variable "dmarc_rua" {
  description = "Email address for capturing DMARC aggregate reports."
}

variable "enable_dmarc" {
  description = "Control whether to create DMARC TXT record."
  type        = bool
  default     = true
}

variable "enable_incoming_email" {
  description = "Control whether or not to handle incoming emails."
  type        = bool
  default     = true
}

variable "enable_spf_record" {
  description = "Control whether or not to set SPF records."
  type        = bool
  default     = true
}
variable "domain_name" {
  description = "The domain name to configure SES."
}

variable "enable_verification" {
  description = "Control whether or not to verify SES DNS records."
  default     = true
}

/*variable "recipient_addresses" {
  type        = list(string)
  description = "List of email addresses to catch bounces and rejections."
}*/

variable "receive_s3_bucket" {
  description = "Name of the S3 bucket to store received emails."
}

variable "receive_s3_prefix" {
  description = "The key prefix of the S3 bucket to store received emails."
}

variable "route53_zone_id" {
  description = "Route53 host zone ID to enable SES."
}

variable "ses_rule_set" {
  description = "Name of the SES rule set to associate rules with."
}

variable "service_prefix" {
  description = "The prefix used to adhere to the naming conventions"
}

variable "s3_email_recipient" {
  description = "this is email attached to s3"
}
