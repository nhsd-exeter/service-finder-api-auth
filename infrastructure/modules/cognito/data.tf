# Lambda
data "archive_file" "custom_registration_verification_email_function" {
  type        = "zip"
  source_dir  = "${path.module}/functions"
  output_path = "${path.module}/functions_zip/${local.custom_registration_verification_email_function_name}.zip"
}

data "aws_iam_policy_document" "lambda_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type = "Service"
      identifiers = [
        "lambda.amazonaws.com",
      ]
    }
  }
}
