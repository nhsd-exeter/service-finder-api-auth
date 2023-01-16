resource "aws_secretsmanager_secret" "smtp_host" {
  name                    = "${var.service_prefix}-smtp-hostname"
  description             = "SMTP hostname"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret_version" "smtp_host" {
  secret_id     = aws_secretsmanager_secret.smtp_host.id
  secret_string = "smtp.host"
  lifecycle {
    ignore_changes = [secret_string]
  }
}

resource "aws_secretsmanager_secret" "smtp_user" {
  name                    = "${var.service_prefix}-smtp-username"
  description             = "SMTP username"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret_version" "smtp_user" {
  secret_id     = aws_secretsmanager_secret.smtp_user.id
  secret_string = "smtp_username"
  lifecycle {
    ignore_changes = [secret_string]
  }
}

resource "aws_secretsmanager_secret" "smtp_password" {
  name                    = "${var.service_prefix}-smtp-password"
  description             = "SMTP password"
  recovery_window_in_days = 0
}

resource "aws_secretsmanager_secret_version" "smtp_password" {
  secret_id     = aws_secretsmanager_secret.smtp_password.id
  secret_string = local.dummy_password

  lifecycle {
    ignore_changes = [secret_string]
  }
}
