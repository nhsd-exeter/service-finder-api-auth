# === Profile Specific =========================================================


variable "aws_account_id" { description = "Texas AWS account id" }

variable "aws_profile" { description = "Texas AWS profile name" }

variable "service_finder_zone" { description = "The hosted zone used for service finder" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }


# === Common ===================================================================

variable "aws_region" { description = "Texas AWS deployment region" }

variable "service_prefix" { description = "The prefix to be used for all infrastructure" }


variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "route53_terraform_state_key" { description = "The Route53 key in the terraform state bucket" }

variable "mail_sender_email" {
  description = "SES email address to use to send emails from Cognito."
}

variable "email_to_go_to_s3" {
  description = "Emails sent to this address will be send to an S3 bucket"
}

variable "ui_main_url" {
  description = "this is mail url"
}

variable "service_finder_verification_redirect" {
  description = "Temp link to redirect users"
}
