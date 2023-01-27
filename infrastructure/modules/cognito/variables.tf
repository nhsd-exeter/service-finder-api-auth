# General variables
variable "aws_account" {
  description = "AWS account identifier."
}

# === Profile Specific =========================================================
variable "ireland_region" {
  description = "Ireland region."
  default     = "eu-west-1"
}


variable "tags" {
  type        = map(string)
  description = "A list of standard tags for any given resource."
}

variable "service_prefix" {
  description = "The prefix used to adhere to the naming conventions"
}

# Cognito variables
variable "cognito_pool_name" {
  description = "Name of the cognito user pool."
}

variable "email_address" {
  description = "SES email address to use to send emails from Cognito."
}

variable "service_finder_zone" {
  description = "Current service finder domain in Route53."
}

variable "profile" {
  description = "K8s deployment profile name that can be either 'nonprod' or 'prod'"
}

variable "s3_email_address" {
  description = "this is email attached to s3"
}

variable "ui_main_url" {
  description = "this is mail url"
}

variable "service_finder_verification_redirect" {
  description = "Mail redirect url to be removed once email is resolved"
}
