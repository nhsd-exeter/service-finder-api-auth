data "aws_iam_policy_document" "ses_bucket" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["ses.amazonaws.com"]
    }

    actions = [
      "s3:PutObject",
    ]

    condition {
      test     = "StringEquals"
      variable = "aws:Referer"
      values = [
        var.aws_account,
      ]
    }

    resources = ["${aws_s3_bucket.ses_bucket.arn}/*"]
  }
}
