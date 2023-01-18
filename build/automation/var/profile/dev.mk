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
SERVICE_PREFIX := service-finder-$(PROFILE)
SERVICE_ACCOUNT := $(SERVICE_PREFIX)-role

SPLUNK_INDEX := eks_logs_service_finder_nonprod
SERVICE_AUTHENTICATION_REPLICAS := 1

CERTIFICATE_DOMAIN := certificate

AUTH_SERVER_PORT := 443

# ==============================================================================
# Infrastructure variables

DEPLOYMENT_STACKS := application
INFRASTRUCTURE_STACKS := authentication,account

TF_VAR_service_prefix := $(SERVICE_PREFIX)
TF_VAR_service_prefix_short := saa-$(PROFILE)

TF_VAR_service_finder_zone := servicefinder.nhs.uk
TF_VAR_sf_domain_name := nonprod-servicefinder.nhs.uk
TF_VAR_ui_main_url := https://$(TF_VAR_sf_domain_name)
TF_VAR_application_service_account_name := $(APPLICATION_SA_NAME)
TF_VAR_base_service_prefix = service-finder-nonprod
TF_VAR_ses_domain_identity := $(TF_VAR_base_service_prefix).$(TF_VAR_platform_zone)
TF_VAR_email_to_go_to_s3 := info@$(TF_VAR_sf_domain_name)
TF_VAR_send_email_logging_level := INFO
TF_VAR_service_finder_verification_redirect := $(TF_VAR_sf_domain_name) #To remove after new email address creation
TF_VAR_rds_hostname := service-finder.db.$(PROFILE).
TF_VAR_postgres_master_username := sfm
TF_VAR_database_name := service_finder




COOKIE_DOMAIN := $(TF_VAR_sf_domain_name)
SF_AUTH_URL   :=  $(TF_VAR_sf_domain_name)
ALLOWED_ORIGINS := *

#Cognito user pool details
COGNITO_USER_POOL := $(TF_VAR_service_prefix)-pool
TF_VAR_cognito_user_pool := $(COGNITO_USER_POOL)
COGNITO_USER_POOL_CLIENT_SECRET := $(or $(COGNITO_USER_POOL_CLIENT_SECRET), )
COGNITO_USER_POOL_CLIENT_ID := $(or $(COGNITO_USER_POOL_CLIENT_ID), )
COGNITO_USER_POOL_ID := $(or $(COGNITO_USER_POOL_ID), )
ADD_DEFAULT_COGNITO_USERS := true

# database settings
DB_INSTANCE := $(TF_VAR_service_prefix)-db
DB_HOST = $(TF_VAR_rds_hostname)$(TF_VAR_platform_zone)
DB_PORT := 5432
DB_NAME := $(TF_VAR_database_name)
DB_USERNAME := service_finder
DB_MASTER_USERNAME := $(TF_VAR_postgres_master_username)
DB_MASTER_PASSWORD_SECRET := $(TF_VAR_service_prefix)-postgres-master-password
DB_PASSWORD_SECRET := $(TF_VAR_service_prefix)-postgres-service-finder-password
DB_ENCRYPTION := true
# To find options for DB_ENCRYPTION_MODE go to JDBC driver docs
DB_ENCRYPTION_MODE := require

TF_VAR_db_host := $(DB_HOST)
TF_VAR_db_port := $(DB_PORT)
TF_VAR_db_name := $(DB_NAME)
TF_VAR_db_username := $(DB_USERNAME)
TF_VAR_db_password_secret := $(DB_PASSWORD_SECRET)




#Mail details
MAIL_SENDER_NAME := NHS Service Finder
MAIL_SENDER_EMAIL := info@sf.directoryofservices.nhs.uk # info@$(TF_VAR_sf_domain_name) .need to be replaced with info@servicefinder.nhs.uk after email verified with ses
TF_VAR_mail_sender_email := $(MAIL_SENDER_EMAIL)
TF_VAR_mail_sender_subject := $(MAIL_SENDER_NAME)
