provider "aws" {
  region  = var.ireland_region
  alias   = "ireland"
  version = ">= 3.74.1, < 4.0.0"
}

provider "aws" {
  region  = var.aws_region
  version = ">= 3.74.1, < 4.0.0"
}
