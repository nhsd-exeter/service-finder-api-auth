include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
AWS_CERTIFICATE := arn:aws:acm:eu-west-2:$(AWS_ACCOUNT_ID):certificate/c0718115-4e22-4f48-a4aa-8c16ea86c5e6

PROFILE := dev
ENVIRONMENT := dev
SPRING_PROFILES_ACTIVE := dev
API_IMAGE_TAG := v0.0.1
VERSION := v0.0.1

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

ADD_DEFAULT_COGNITO_USERS := false

#Cognito user pool details
COGNITO_USER_POOL := sf-authentication-dev-pool
COGNITO_USER_POOL_CLIENT_SECRET := $(or $(COGNITO_USER_POOL_CLIENT_SECRET), )
COGNITO_USER_POOL_CLIENT_ID := $(or $(COGNITO_USER_POOL_CLIENT_ID), )
COGNITO_USER_POOL_ID := $(or $(COGNITO_USER_POOL_ID), )
