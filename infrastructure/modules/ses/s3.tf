resource "aws_s3_bucket" "ses_bucket" {
  bucket        = var.receive_s3_bucket
  force_destroy = false
  acl           = "private"
  versioning {
    enabled = true
  }
  server_side_encryption_configuration {
    rule {
      apply_server_side_encryption_by_default {
        sse_algorithm = "AES256"
      }
    }
  }

  tags = merge(
    var.tags,
    {
      "Name" = "Service Finder SES emails bucket"
    },
  )
}

resource "aws_s3_bucket_object" "received_emails" {
  bucket = aws_s3_bucket.ses_bucket.id
  acl    = "private"
  key    = "${var.receive_s3_prefix}/"
  source = "/dev/null"
}

resource "aws_s3_bucket_policy" "ses_bucket" {
  bucket = aws_s3_bucket.ses_bucket.id
  policy = data.aws_iam_policy_document.ses_bucket.json
}
