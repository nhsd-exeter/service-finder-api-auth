include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk

# ==============================================================================
# Service variables
SPRING_PROFILES_ACTIVE := local,mail-smtp
CERTIFICATE_DOMAIN := certificate
AUTH_SERVER_PORT := 443
VERSION := v0.0.1
#UI_MAIN_UI := https://localhost:8080
#Cognito configuration
COGNITO_JWT_VERIFICATION_URL := http://testJwtVerificationUrl
COGNITO_USER_POOL_CLIENT_ID := testUserPoolClientId
COGNITO_USER_POOL_CLIENT_SECRET := testUserPoolClientSecret
COGNITO_USER_POOL_ID := testUserPoolId
COOKIE_DOMAIN := localhost
SF_AUTH_URL := localhost
ALLOWED_ORIGINS :=  https://localhost:8080

DB_HOST := postgres.sf.test
DB_PORT := 5432
DB_NAME := service_finder
DB_USERNAME := service_finder
DB_PASSWORD := service_finder
DB_ENCRYPTION := true
# To find options for DB_ENCRYPTION_MODE go to JDBC driver docs
DB_ENCRYPTION_MODE := require


MAIL_SENDER_NAME := Service Finder Authentication
MAIL_SENDER_EMAIL := info@servicefinder.nhs.uk

#AUTH_LOGIN_URL := http://mockservice.sf.test:8080/app/controllers
#AUTH_LOGIN_URI := /authentication/login

#POSTCODE_MAPPING_SERVICE_URL := http://mockservice.sf.test:8080/app/controllers
#POSTCODE_MAPPING_USER := service-finder-admin@nhs.net
#POSTCODE_MAPPING_PASSWORD := password
