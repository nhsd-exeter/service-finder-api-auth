include $(VAR_DIR)/platform-texas/v1/account-live-k8s-nonprod.mk
# ==============================================================================


SPRING_PROFILES_ACTIVE := dev-compose,mail-smtp,local-elasticsearch,test
DOCKER_COMPOSE_API_IMAGE_VERSION := latest
DOCKER_COMPOSE_UI_IMAGE_VERSION := latest

AUTH_API_ENDPOINT := https://autentication.saa.test:8443/api

USER_MANAGEMENT_INTERNAL_URL := https://user-management.sf.test:8443
USER_MANAGEMENT_URL := https://user-management.sf.test:8443
COOKIE_DOMAIN := sf.test
ALLOWED_ORIGINS := https://ui-user.sf.test:8443,https://ui-admin.sf.test:8443,https://service-finder.sf.test:8443

COGNITO_JWT_VERIFICATION_URL := http://testJwtVerificationUrl
COGNITO_USER_POOL_CLIENT_ID := testUserPoolClientId
COGNITO_USER_POOL_CLIENT_SECRET := testUserPoolClientSecret
COGNITO_USER_POOL_ID := testUserPoolId

DB_HOST := postgres.sf.test
DB_PORT := 5432
DB_NAME := service_finder
DB_USERNAME := service_finder
DB_PASSWORD := service_finder
DB_ENCRYPTION := true
# To find options for DB_ENCRYPTION_MODE go to JDBC driver docs
DB_ENCRYPTION_MODE := require
MAIL_SENDER_NAME := NHS Service Finder
MAIL_SENDER_EMAIL := info@finder.directoryofservices.nhs.uk
MAIL_CUSTOM_FEEDBACK_RECIPIENT := info@finder.directoryofservices.nhs.uk

MAIL_SERVER_HOST := mailcatcher.sf.test
MAIL_SERVER_PORT := 25
MAIL_SERVER_AUTH := false


AWS_ACCESS_KEY_ID := test_access_key_id
AWS_SECRET_ACCESS_KEY := test_secret_access_key

EXETER_HELPDESK_EMAIL := exeter.test@nhs.net
SERVICE_FINDER_EMAIL := service.test@nhs.net

# UI config for timeout message
# show warning message after X minutes
USER_AUTO_LOGOUT_TRESHOLD := 1
# logout user after further X minutes
USER_AUTO_LOGOUT_TIMEOUT := 2
UI_MAIN_UI := https://ui-user.sf.test:8443
POSTCODE_MAPPING_SERVICE_URL := http://mockservice.sf.test:8080/app/controllers
POSTCODE_MAPPING_USER := service-finder-admin@nhs.net
POSTCODE_MAPPING_PASSWORD := password

AUTH_LOGIN_URL := http://mockservice.sf.test:8080/app/controllers
AUTH_LOGIN_URI := /authentication/login

# Please note that there are environment variables in the local project
# please see: 'legacy/tests/integration-test/src/test/resources/properties/environment.properties'
