data "terraform_remote_state" "route53" {
  backend = "s3"
  config = {
    bucket = var.terraform_platform_state_store
    key    = var.route53_terraform_state_key
    region = var.aws_region
  }
}
