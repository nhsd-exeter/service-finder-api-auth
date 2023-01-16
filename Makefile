PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)
DOCKER_REGISTRY_LIVE = $(DOCKER_REGISTRY)/prod

derive-build-tag:
	dir=$$(make _docker-get-dir NAME=api)
	echo $$(cat $$dir/VERSION) | \
				sed "s/YYYY/$$(date --date=$(BUILD_DATE) -u +"%Y")/g" | \
				sed "s/mm/$$(date --date=$(BUILD_DATE) -u +"%m")/g" | \
				sed "s/dd/$$(date --date=$(BUILD_DATE) -u +"%d")/g" | \
				sed "s/HH/$$(date --date=$(BUILD_DATE) -u +"%H")/g" | \
				sed "s/MM/$$(date --date=$(BUILD_DATE) -u +"%M")/g" | \
				sed "s/ss/$$(date --date=$(BUILD_DATE) -u +"%S")/g" | \
				sed "s/SS/$$(date --date=$(BUILD_DATE) -u +"%S")/g" | \
				sed "s/hash/$$(git rev-parse --short HEAD)/g"

compile: project-config # Compile the project to make the target class (binary) files
	make docker-run-mvn \
		DIR="application/authentication" \
		CMD="compile"

coverage-report: # Generate jacoco test coverage reports
	make load-cert-to-application # FIXME: Remove it !!!
	make unit-test
	make docker-run-mvn \
		DIR="application/authentication" \
		CMD="jacoco:report"

unit-test: # Run project unit tests
	make docker-run-mvn \
		DIR="application/authentication" \
		CMD="test"

# ==============================================================================
# Development workflow targets

build: project-config # Build project
	make load-cert-to-application
	make docker-run-mvn \
		DIR="application/authentication" \
		CMD="-Dmaven.test.skip=true clean install" \
		LIB_VOLUME_MOUNT="true" \
		PROFILE=local
	mv \
		$(PROJECT_DIR)/application/authentication/target/service-finder-api-auth-*.jar \
		$(PROJECT_DIR)/build/docker/api/assets/application/dos-service-finder-authentication-api.jar
	make docker-build NAME=api

start: project-start # Start project

stop: project-stop # Stop project

restart: stop start # Restart project

log: project-log # Show project logs

test: # Test project
	make docker-run-mvn \
		DIR="application/authentication" \
		CMD="clean test" \
		LIB_VOLUME_MOUNT="true" \
		PROFILE=local \
		VARS_FILE=$(VAR_DIR)/profile/local.mk


project-plan-deployment: ## Display what will occur during the deployment - optional: PROFILE
	eval "$$(make aws-assume-role-export-variables)"
	make terraform-plan STACK=$(INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)
	sleep $(SLEEP_AFTER_PLAN)

project-plan-deployment-base: ## Display what will occur during the deployment - optional: PROFILE
	make pipeline-print-variables PROFILE=base-$(PROFILE)
	make terraform-plan PROFILE=base-$(PROFILE)
	sleep $(SLEEP_AFTER_PLAN)

project-plan-deployment-destroy: ## Display what will occur during the deployment - optional: PROFILE
	make terraform-plan OPTS="-destroy"
	sleep $(SLEEP_AFTER_PLAN)

project-populate-cognito: ## Populate cognito - optional: PROFILE=nonprod|prod,AWS_ROLE=Developer
	eval "$$(make aws-assume-role-export-variables)"
	$(PROJECT_DIR)/infrastructure/scripts/cognito.sh

project-infrastructure-set-up-base: ## Set up infrastructure - optional: AWS_ROLE=Developer|jenkins_assume_role
	eval "$$(make aws-assume-role-export-variables)"
	make terraform-apply-auto-approve PROFILE=base-$(PROFILE)

project-infrastructure-set-up: ## Set up infrastructure - optional: AWS_ROLE=Developer|jenkins_assume_role
	make terraform-apply-auto-approve STACK=$(INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)

project-infrastructure-tear-down: ## Tear down infrastructure - optional: PROFILE=nonprod|prod,AWS_ROLE=Developer|jenkins_assume_role
	make terraform-destroy-auto-approve STACK=$(INFRASTRUCTURE_STACKS) PROFILE=$(PROFILE)

project-tear-down: ## Tear down environment - optional: PROFILE=nonprod|prod,AWS_ROLE=Developer|jenkins_assume_role
	make project-undeploy
	make project-infrastructure-tear-down
	make terraform-delete-states

project-push-image-api: ## Push the docker images (API) to the ECR
	make docker-login
	make docker-push NAME=api VERSION=${VERSION}

project-deploy: ### Deploy application stack to the Kubernetes cluster - mandatory: STACK|STACKS|DEPLOYMENT_STACKS=[comma-separated names],PROFILE=[profile name]
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	make project-deploy STACK=application PROFILE=$(PROFILE)

project-clean: # Clean up project
	make docker-image-clean-project-repo-version PROJECT_REPO=uec-dos-api/saa/api VERSION=$(VERSION)
	docker network rm $(DOCKER_NETWORK) 2> /dev/null ||:


# ==============================================================================
# Supporting targets

trust-certificate: ssl-trust-certificate-project ## Trust the SSL development certificate

load-cert-to-application: ## Copies certificates to project directory
	cp $(PROJECT_DIR)/build/automation/etc/certificate/* $(PROJECT_DIR)/application/authentication/src/main/resources/certificate


# ==============================================================================
# Pipeline targets

build-artefact:
	echo TODO: $(@)

publish-artefact:
	echo TODO: $(@)

backup-data:
	echo TODO: $(@)

provision-infractructure:
	echo TODO: $(@)

deploy-artefact:
	echo TODO: $(@)

apply-data-changes:
	echo TODO: $(@)

# --------------------------------------

run-static-analisys:
	echo TODO: $(@)

run-unit-test:
	echo TODO: $(@)

run-smoke-test:
	echo TODO: $(@)

run-integration-test:
	echo TODO: $(@)

run-contract-test:
	echo TODO: $(@)

run-functional-test:
	[ $$(make project-branch-func-test) != true ] && exit 0
	echo TODO: $(@)

run-performance-test:
	[ $$(make project-branch-perf-test) != true ] && exit 0
	echo TODO: $(@)

run-security-test:
	[ $$(make project-branch-sec-test) != true ] && exit 0
	echo TODO: $(@)

# --------------------------------------

remove-unused-environments:
	echo TODO: $(@)

remove-old-artefacts:
	echo TODO: $(@)

remove-old-backups:
	echo TODO: $(@)

# --------------------------------------

pipeline-finalise: ## Finalise pipeline execution - mandatory: PIPELINE_NAME,BUILD_STATUS
	# Check if BUILD_STATUS is SUCCESS or FAILURE
	make pipeline-send-notification

pipeline-send-notification: ## Send Slack notification with the pipeline status - mandatory: PIPELINE_NAME,BUILD_STATUS
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make secret-fetch-and-export-variables NAME=$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)/deployment)"
	make slack-it

# --------------------------------------

pipeline-check-resources: ## Check all the pipeline deployment supporting resources - optional: PROFILE=[name]
	profiles="$$(make project-list-profiles)"
	# for each profile
	#export PROFILE=$$profile
	# TODO:
	# table: $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-deployment
	# secret: $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)/deployment
	# bucket: $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)-deployment
	# certificate: SSL_DOMAINS_PROD
	# repos: DOCKER_REPOSITORIES

pipeline-create-resources: ## Create all the pipeline deployment supporting resources - optional: PROFILE=[name]
	profiles="$$(make project-list-profiles)"
	# for each profile
	#export PROFILE=$$profile
	# TODO:
	# Per AWS accoount, i.e. `nonprod` and `prod`
	eval "$$(make aws-assume-role-export-variables)"
	#make aws-dynamodb-create NAME=$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-deployment ATTRIBUTE_DEFINITIONS= KEY_SCHEMA=
	#make secret-create NAME=$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)/deployment VARS=DB_PASSWORD,SMTP_PASSWORD,SLACK_WEBHOOK_URL
	#make aws-s3-create NAME=$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)-deployment
	#make ssl-request-certificate-prod SSL_DOMAINS_PROD
	# Centralised, i.e. `mgmt`
	eval "$$(make aws-assume-role-export-variables AWS_ACCOUNT_ID=$(AWS_ACCOUNT_ID_MGMT))"
	#make docker-create-repository NAME=uec-dos-api/sfa/api
	#make aws-codeartifact-setup REPOSITORY_NAME=$(PROJECT_GROUP_SHORT)

# ==============================================================================

.SILENT: \
	derive-build-tag
