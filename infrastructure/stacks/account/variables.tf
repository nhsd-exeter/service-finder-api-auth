# === Profile Specific =========================================================
variable "aws_profile" { description = "Texas AWS profile name" }

variable "aws_account_id" { description = "Texas AWS account id" }

variable "service_finder_zone" { description = "The hosted zone used for service finder" }

variable "platform_zone" { description = "The hosted zone used for the platform" }

variable "profile" { description = "K8s deployment profile name that can be either 'nonprod' or 'prod'" }

# === Common ===================================================================

variable "ireland_region" {
  description = "Ireland region."
  default     = "eu-west-1"
}

variable "aws_region" { description = "Texas AWS deployment region" }


variable "base_service_prefix" { description = "The prefix to be used for all infrastructure" }

variable "terraform_platform_state_store" { description = "Name of the S3 bucket used to store the platform infrastructure terraform state" }

variable "route53_terraform_state_key" { description = "The Route53 key in the terraform state bucket" }


variable "sf_domain_name" { description = "The service-finder domain" }

variable "email_to_go_to_s3" { description = "Emails sent to this address will be send to an S3 bucket" }

