locals {
  sf_url                                               = "https://${var.service_finder_verification_redirect}"
  custom_registration_verification_email_function_name = "${var.service_prefix}-customMessages"
  custom_registration_js_name                          = "service-finder-customMessages"
}
