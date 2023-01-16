provider "aws" {
  region  = var.aws_region
  alias   = "provider"
  version = ">= 3.74.1, < 4.0.0"
}
