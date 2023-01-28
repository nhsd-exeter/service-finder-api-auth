PROJECT_DIR := $(dir $(abspath $(lastword $(MAKEFILE_LIST))))
include $(abspath $(PROJECT_DIR)/build/automation/init.mk)

# ==============================================================================
# TODO: Refactor old code

PROFILE = $(SF_JENKINS_ENV)

K8S_APP_NAMESPACE = $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-$(PROFILE)
K8S_JOB_NAMESPACE = $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-jobs-$(PROFILE)

PROJECT_APPLICATION_STACK_DIR=$(PROJECT_DIR)/deployment/stacks/application
PROJECT_JOBS_STACK_DIR=$(PROJECT_DIR)/deployment/stacks/jobs

JQ_PROGS_DIR_REL := $(shell echo $(abspath $(PROJECT_DIR)/build/jq) | sed "s;$(PROJECT_DIR);;g")

# -------------------------------------

# Variables to change to affect deployment
DEFAULT_LOCAL_PROFILE := local
DEFAULT_JENKINS_PROFILE := nonprod
DEFAULT_JENKINS_PROFILE_LIVE := demo #default : demo
GIT_TRUNK_BRANCH := master
ROLLBACK := false
DEPLOY_INFRASTRUCTURE_FROM_BRANCH := false
DEPLOY_TO_K8S_FROM_BRANCH := false

# IMAGE_BASE_VERSION := 202210211024-9f22e2b
IMAGE_BASE_VERSION := 1.0.20191119

JENKINS_ROLE := jenkins_assume_role
SF_AWS_SECRET_NAME := service-finder/deployment
SF_AWS_ADMIN_USER_PASSWORD_SECRET = service-finder-auth-api-$(PROFILE)-cognito-admin-password
SF_JENKINS_ENV = $(if $(HUDSON_URL),$(shell echo $(HUDSON_URL) | grep prod > /dev/null 2>&1 && echo $(DEFAULT_JENKINS_PROFILE_LIVE)|| echo $(DEFAULT_JENKINS_PROFILE)),$(DEFAULT_LOCAL_PROFILE))
K8S_JOB_DATA_NAMESPACE = $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-job-data-$(PROFILE)
K8S_JOB_ELASTIC_SEARCH_NAMESPACE = $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-job-es-$(PROFILE)
PROJECT_GROUP_NAME_SHORT := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)
APP_URL_PREFIX := $(K8S_APP_NAMESPACE)-$(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)

TERRAFORM_DIR := infrastructure/stacks
JENKINS_WORKSPACE_BUCKET := $(PROJECT_GROUP_SHORT)-$(PROJECT_NAME_SHORT)-jenkins-workspace

# ==============================================================================


# ==============================================================================
# Feature flags:
include $(VAR_DIR)/feature-flags/$(PROFILE).mk
# ==============================================================================

include $(VAR_DIR)/profile/$(PROFILE).mk

DOCKER_REGISTRY_LIVE = $(DOCKER_REGISTRY)/prod
DOCKER_NGINX_VERSION = 1.16.1-alpine


# ==============================================================================





prepare: ## Prepare environment
	make \
		git-config \
		docker-config

get-project-vars:
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	eval "$$(make secret-fetch-and-export-variables NAME=$(SF_AWS_SECRET_NAME))"

project-populate-application-variables:
	export TTL=$$(make -s k8s-get-namespace-ttl)

	export COGNITO_USER_POOL_CLIENT_SECRET=$$(make -s project-aws-get-cognito-client-secret NAME=$(COGNITO_USER_POOL))
	export COGNITO_USER_POOL_CLIENT_ID=$$(make -s project-aws-get-cognito-client-id NAME=$(COGNITO_USER_POOL))
	export COGNITO_USER_POOL_ID=$$(make -s aws-cognito-get-userpool-id NAME=$(COGNITO_USER_POOL))
	export COGNITO_JWT_VERIFICATION_URL=https://cognito-idp.eu-west-2.amazonaws.com/$${COGNITO_USER_POOL_ID}/.well-known/jwks.json

	export DB_MASTER_PASSWORD=$$(make -s aws-secret-get NAME=$(DB_MASTER_PASSWORD_SECRET))
	export DB_PASSWORD=$$(make -s aws-secret-get NAME=$(DB_PASSWORD_SECRET))
	export POSTCODE_MAPPING_PASSWORD=$$(make secret-fetch NAME=uec-dos-api-sfsa-$(FUZZY_PASSWORD_PROFILE)-cognito-passwords | jq .POSTCODE_PASSWORD | tr -d '"' )
	export POSTCODE_MAPPING_PASSWORD_DMO=$$(make secret-fetch NAME=uec-dos-api-sfsa-$(FUZZY_PASSWORD_PROFILE)-cognito-passwords | jq .POSTCODE_PASSWORD_DMO | tr -d '"' )

project-aws-get-cognito-client-id: # Get AWS cognito client id - mandatory: NAME
	aws cognito-idp list-user-pool-clients \
		--user-pool-id $$(make -s aws-cognito-get-userpool-id NAME=$(NAME)) \
		--region $(AWS_REGION) \
		--query 'UserPoolClients[].ClientId' \
		--output text

project-aws-get-cognito-client-secret: # Get AWS secret - mandatory: NAME
	aws cognito-idp describe-user-pool-client \
		--user-pool-id $$(make -s aws-cognito-get-userpool-id NAME=$(NAME)) \
		--client-id $$(make -s project-aws-get-cognito-client-id NAME=$(NAME)) \
		--region $(AWS_REGION) \
		--query 'UserPoolClient.ClientSecret' \
		--output text

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


# ==============================================================================
# Development workflow targets

build: project-config # Build project
	make load-cert-to-application
	if [ $(PROFILE) == 'local' ]
	then
		make docker-run-mvn \
			DIR="application/authentication" \
			CMD="-Dmaven.test.skip=true -Ddependency-check.skip=true clean install" \
			LIB_VOLUME_MOUNT="true"
	else
		make docker-run-mvn \
			DIR="application/authentication" \
			CMD="-Ddependency-check.skip=true clean install \
			-Dsonar.verbose=true \
			-Dsonar.host.url='https://sonarcloud.io' \
			-Dsonar.organization='nhsd-exeter' \
			-Dsonar.projectKey='uec-dos-api-sfsa' \
			-Dsonar.java.binaries=target/classes \
			-Dsonar.projectName='service-finder-api-auth' \
			-Dsonar.login='$$(make secret-fetch NAME=service-finder-sonar-pass | jq .SONAR_HOST_TOKEN | tr -d '"' || exit 1)' \
			-Dsonar.sourceEncoding='UTF-8' \
			-Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco \
			-Dsonar.exclusions='src/main/java/**/config/*.*,src/main/java/**/model/*.*,src/main/java/**/exception/*.*,src/test/**/*.*,src/main/java/**/filter/*.*,src/main/java/**/ServiceFinderAuthenticationAPI.*' \
			sonar:sonar" \
			LIB_VOLUME_MOUNT="true"
	fi

	mv \
		$(PROJECT_DIR)/application/authentication/target/service-finder-api-auth-*.jar \
		$(PROJECT_DIR)/build/docker/api/assets/application/dos-service-finder-authentication-api.jar
	make docker-build NAME=api

scan:
	if [ ! -d $(PROJECT_DIR)/reports ]; then
		mkdir $(PROJECT_DIR)/reports
	fi

	make docker-run-mvn \
		DIR="application/authentication" \
		CMD="dependency-check:check"
	mv \
		$(PROJECT_DIR)/application/authentication/target/dependency-check-report.html \
		$(PROJECT_DIR)/reports/service-finder-authentication-dependency-report.html

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

project-push-image: ## Push the docker images (API) to the ECR
	make docker-push NAME=api VERSION=${VERSION}

deploy: # Deploy artefacts - mandatory: PROFILE=[name]
	eval "$$(make aws-assume-role-export-variables)"
	eval "$$(make project-populate-application-variables)"
	eval "$$(make secret-fetch-and-export-variables NAME=$(SF_AWS_SECRET_NAME))"
	make project-deploy PROFILE=$(PROFILE) STACK=application

clean: # Clean up project
	make stop
	docker network rm $(DOCKER_NETWORK) 2> /dev/null ||:



docker-run-mvn-lib-mount: ### Build Docker image mounting library volume - mandatory: DIR, CMD
	make docker-run-mvn LIB_VOLUME_MOUNT=true \
		DIR="$(DIR)" \
		CMD="$(CMD)"


# ==============================================================================

monitor-r53-connection:
	attempt_counter=1
	max_attempts=20
	sleep 30
	http_status_code=0
	until [[ $$http_status_code -eq 200 ]]; do
		sleep 20
		if [[ $$attempt_counter -eq $$max_attempts ]]; then
			echo "Maximum attempts reached unable to connect to deployed instance"
			exit 0
		fi
		echo "Pinging deployed instance count - " $$attempt_counter
		http_status_code=$$(curl -s -k -o /dev/null -w "%{http_code}" --max-time 30 $(SERVICE_FINDER_AUTH_ENDPOINT)/home || true)
		attempt_counter=$$(($$attempt_counter+1))
		echo Status code is: $$http_status_code
	done


# ==============================================================================

k8s-wait-for-job-to-complete: ### Wait for the job to complete
	count=1
	until [ $$count -gt 20 ]; do
		if [ "$$(make -s k8s-job-failed | tr -d '\n')" == "True" ]; then
			echo "The job has failed"
			exit 1
		fi
		if [ "$$(make -s k8s-job-complete | tr -d '\n')" == "True" ]; then
			echo "The job has completed"
			exit 0
		fi
		echo "Still waiting for the job to complete"
		sleep 5
		((count++)) ||:
	done
	echo "The job has not completed, but have given up waiting."
	exit 1

k8s-get-replica-sets-not-yet-updated:
	echo -e
	kubectl get deployments -n $(K8S_APP_NAMESPACE) \
	-o=jsonpath='{range .items[?(@.spec.replicas!=@.status.updatedReplicas)]}{.metadata.name}{"("}{.status.updatedReplicas}{"/"}{.spec.replicas}{")"}{" "}{end}'

k8s-get-pod-status:
	echo -e
	kubectl get pods -n $(K8S_APP_NAMESPACE)

k8s-check-deployment-of-replica-sets:
	eval "$$(make aws-assume-role-export-variables)"
	make k8s-kubeconfig-get
	eval "$$(make k8s-kubeconfig-export-variables)"
	sleep 10
	elaspedtime=10
	until [ $$elaspedtime -gt $(CHECK_DEPLOYMENT_TIME_LIMIT) ]; do
		replicasNotYetUpdated=$$(make -s k8s-get-replica-sets-not-yet-updated)
		if [ -z "$$replicasNotYetUpdated" ]
		then
			echo "Success - all replica sets in the deployment have been updated."
			exit 0
		else
			echo "Waiting for all replicas to be updated: " $$replicasNotYetUpdated

			echo "----------------------"
			echo "Pod status: "
			make k8s-get-pod-status
			podStatus=$$(make -s k8s-get-pod-status)
			echo "-------"

			#Check failure conditions
			if [[ $$podStatus = *"ErrImagePull"*
					|| $$podStatus = *"ImagePullBackOff"* ]]; then
				echo "Failure: Error pulling Image"
				exit 1
			elif [[ $$podStatus = *"Error"*
								|| $$podStatus = *"error"*
								|| $$podStatus = *"ERROR"* ]]; then
				echo "Failure: Error with deployment"
				exit 1
			fi

		fi
		sleep 10
		((elaspedtime=elaspedtime+$(CHECK_DEPLOYMENT_POLL_INTERVAL)))
		echo "Elapsed time: " $$elaspedtime
	done

	echo "Conditional Success: The deployment has not completed within the timescales, but carrying on anyway"
	exit 0

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


run-smoke-test:
	echo TODO: $(@)

run-integration-test:
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

run-contract-test:
	make start PROFILE=local VERSION=$(VERSION)
	cd test/contract
	make run-contract
	cd ../../
	make stop

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
	#make docker-create-repository NAME=uec-dos-api/saa/api
	#make aws-codeartifact-setup REPOSITORY_NAME=$(PROJECT_GROUP_SHORT)

# ==============================================================================
# --------------------------------------

pipeline-on-success:
	echo TODO: $(@)

pipeline-on-failure:
	echo TODO: $(@)

.SILENT: \
	derive-build-tag
