resource "aws_lambda_function" "custom_registration_verification_email" {
  filename         = data.archive_file.custom_registration_verification_email_function.output_path
  function_name    = local.custom_registration_verification_email_function_name
  description      = "Service Finder function that provides a custom registration verification email for Cognito"
  role             = aws_iam_role.custom_registration_verification_email_role.arn
  handler          = "${local.custom_registration_js_name}.handler"
  source_code_hash = data.archive_file.custom_registration_verification_email_function.output_base64sha256
  runtime          = "nodejs14.x"
  timeout          = 3
  memory_size      = 128
  publish          = true
  environment {
    variables = {
      SF_URL = local.sf_url
    }
  }
  tags = var.tags
}

resource "aws_lambda_permission" "custom_registration_verification_email" {
  statement_id  = "AllowExecutionFromCognitoUserPool"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.custom_registration_verification_email.function_name
  principal     = "cognito-idp.amazonaws.com"
  source_arn    = aws_cognito_user_pool.pool.arn
}

resource "aws_iam_role" "custom_registration_verification_email_role" {
  name               = "${local.custom_registration_verification_email_function_name}-role"
  assume_role_policy = data.aws_iam_policy_document.lambda_assume_role.json
  tags               = var.tags
}

resource "aws_iam_role_policy_attachment" "basic" {
  role       = aws_iam_role.custom_registration_verification_email_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}
