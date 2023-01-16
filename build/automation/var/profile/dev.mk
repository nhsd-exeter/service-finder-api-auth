include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/c0718115-4e22-4f48-a4aa-8c16ea86c5e6

PROFILE := dev
ENVIRONMENT := $(PROFILE)
SPRING_PROFILES_ACTIVE := $(PROFILE)
API_IMAGE_TAG := v0.0.1
VERSION := v0.0.1
SLEEP_AFTER_PLAN := 1s

SPLUNK_INDEX := eks_logs_service_finder_nonprod
SERVICE_AUTHENTICATION_REPLICAS := 1

CERTIFICATE_DOMAIN := certificate

AUTH_SERVER_PORT := 443

# ==============================================================================
# Infrastructure variables

DEPLOYMENT_STACKS := application
INFRASTRUCTURE_STACKS := authentication

TF_VAR_service_prefix := sf-authentication-$(PROFILE)
TF_VAR_service_prefix_short := sfa-$(PROFILE)

TF_VAR_service_finder_zone := servicefinder.nhs.uk
TF_VAR_sf_domain_name := nonprod-servicefinder.nhs.uk
TF_VAR_ui_main_url := https://$(TF_VAR_sf_domain_name)

TF_VAR_base_service_prefix = service-finder-nonprod
TF_VAR_ses_domain_identity := $(TF_VAR_base_service_prefix).$(TF_VAR_platform_zone)
TF_VAR_email_to_go_to_s3 := info@$(TF_VAR_ses_domain_identity)
TF_VAR_send_email_logging_level := INFO
TF_VAR_service_finder_verification_redirect := $(TF_VAR_sf_domain_name) #To remove after new email address creation

#Cognito user pool details
COGNITO_USER_POOL := $(TF_VAR_service_prefix)-pool
TF_VAR_cognito_user_pool := $(COGNITO_USER_POOL)
COGNITO_USER_POOL_CLIENT_SECRET := $(or $(COGNITO_USER_POOL_CLIENT_SECRET), )
COGNITO_USER_POOL_CLIENT_ID := $(or $(COGNITO_USER_POOL_CLIENT_ID), )
COGNITO_USER_POOL_ID := $(or $(COGNITO_USER_POOL_ID), )
ADD_DEFAULT_COGNITO_USERS := true

#Mail details
MAIL_SENDER_NAME := NHS Service Finder
MAIL_SENDER_EMAIL := info@$(TF_VAR_service_finder_zone)
TF_VAR_mail_sender_email := $(MAIL_SENDER_EMAIL)
TF_VAR_mail_sender_subject := $(MAIL_SENDER_NAME)
