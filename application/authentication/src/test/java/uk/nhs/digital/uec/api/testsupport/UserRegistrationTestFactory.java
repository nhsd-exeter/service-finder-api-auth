package uk.nhs.digital.uec.api.testsupport;

import uk.nhs.digital.uec.api.model.RegistrationCompleteAccount;
import uk.nhs.digital.uec.api.model.RegistrationRequestAccount;
import uk.nhs.digital.uec.api.model.UserRegistration;

/**
 * Test factory for the creation of {@link UserRegistration}s in known states for testing purposes
 */
public class UserRegistrationTestFactory {

    public static final String EMAIL_ADDRESS = "bill2@example.com";

    public static final String PASSWORD = "12345678";

    public static final String NAME = "Bill";

    public static final String JOB_TITLE = "job_title";

    public static final String JOB_ROLE_TYPE = "job_role_type";

    public static final String JOB_TYPE_TYPE_OTHER = "OTHER";

    public static final String JOB_ROLE_TYPE_OTHER_DETAILS = "job_role_type_other";

    public static final String ORGANISATION_NAME = "org_name";

    public static final String ORGANISATION_TYPE = "org_type";

    public static final String ORGANISATION_TYPE_OTHER_DETAILS = "org_type_other";

    public static final String CONTACT_TELEPHONE_NUMBER = "tel_no";

    public static final String WORKPLACE_POSTCODE = "BS1 1AR";

    public static final String REGION = "SOUTH_WEST";

    public static final String ORGANISATION_TYPE_OTHER = "OTHER";

    public static RegistrationRequestAccount atestUserRegistrationRequestAccount() {
        return new RegistrationRequestAccount(EMAIL_ADDRESS, PASSWORD);
    }

    public static RegistrationCompleteAccount atestUserRegistrationCompleteAccount(){
        return new RegistrationCompleteAccount(EMAIL_ADDRESS,
        NAME,
        JOB_TITLE,
        JOB_TYPE_TYPE_OTHER,
        JOB_ROLE_TYPE_OTHER_DETAILS,
        ORGANISATION_NAME,
        ORGANISATION_TYPE_OTHER,
        ORGANISATION_TYPE_OTHER_DETAILS,
        CONTACT_TELEPHONE_NUMBER,
        WORKPLACE_POSTCODE,
        REGION,
        true,
        true);
    }

    public static UserRegistrationBuilder atestUserRegistration() {
        return new UserRegistrationBuilder()
            .withEmailAddress(EMAIL_ADDRESS)
            .withPassword(PASSWORD)
            .withName(NAME)
            .withJobTitle(JOB_TITLE)
            .withJobRoleType(JOB_ROLE_TYPE)
            .withOrganisationName(ORGANISATION_NAME)
            .withOrganisationType(ORGANISATION_TYPE)
            .withContactTelephoneNumber(CONTACT_TELEPHONE_NUMBER)
            .withWorkplacePostcode(WORKPLACE_POSTCODE)
            .withRegion(REGION)
            .withAcceptedTermsAndConditions(true);
    }

    public static UserRegistrationBuilder atestUserRegistrationWithOthers() {
        return new UserRegistrationBuilder()
            .withEmailAddress(EMAIL_ADDRESS)
            .withPassword(PASSWORD)
            .withName(NAME)
            .withJobTitle(JOB_TITLE)
            .withJobRoleType(JOB_TYPE_TYPE_OTHER)
            .withJobRoleTypeOther(JOB_ROLE_TYPE_OTHER_DETAILS)
            .withOrganisationName(ORGANISATION_NAME)
            .withOrganisationType(ORGANISATION_TYPE_OTHER)
            .withOrganisationTypeOther(ORGANISATION_TYPE_OTHER_DETAILS)
            .withContactTelephoneNumber(CONTACT_TELEPHONE_NUMBER)
            .withWorkplacePostcode(WORKPLACE_POSTCODE)
            .withRegion(REGION)
            .withAcceptedTermsAndConditions(true);
    }

    public static class UserRegistrationBuilder {

        private String emailAddress;

        private String password;

        private String name;

        private String jobTitle;

        private String jobRoleType;

        private String jobRoleTypeOther;

        private String organisationName;

        private String organisationType;

        private String organisationTypeOther;

        private String contactTelephoneNumber;

        private String workplacePostcode;

        private String region;

        private boolean acceptedTermsAndConditions;

        public UserRegistrationBuilder withEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
            return this;
        }

        public UserRegistrationBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public UserRegistrationBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public UserRegistrationBuilder withJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
            return this;
        }

        public UserRegistrationBuilder withJobRoleType(String jobRoleType) {
            this.jobRoleType = jobRoleType;
            return this;
        }

        public UserRegistrationBuilder withJobRoleTypeOther(String jobRoleTypeOther) {
            this.jobRoleTypeOther = jobRoleTypeOther;
            return this;
        }

        public UserRegistrationBuilder withOrganisationName(String organisationName) {
            this.organisationName = organisationName;
            return this;
        }

        public UserRegistrationBuilder withOrganisationType(String organisationType) {
            this.organisationType = organisationType;
            return this;
        }

        public UserRegistrationBuilder withOrganisationTypeOther(String organisationTypeOther) {
            this.organisationTypeOther = organisationTypeOther;
            return this;
        }

        public UserRegistrationBuilder withContactTelephoneNumber(String contactTelephoneNumber) {
            this.contactTelephoneNumber = contactTelephoneNumber;
            return this;
        }

        public UserRegistrationBuilder withWorkplacePostcode(String workplacePostcode) {
            this.workplacePostcode = workplacePostcode;
            return this;
        }

        public UserRegistrationBuilder withRegion(String region) {
            this.region = region;
            return this;
        }

        public UserRegistrationBuilder withAcceptedTermsAndConditions(boolean acceptedTermsAndConditions) {
            this.acceptedTermsAndConditions = acceptedTermsAndConditions;
            return this;
        }

        public UserRegistration build() {
            return new UserRegistration(emailAddress,
                                        password,
                                        name,
                                        jobTitle,
                                        jobRoleType,
                                        jobRoleTypeOther,
                                        organisationName,
                                        organisationType,
                                        organisationTypeOther,
                                        contactTelephoneNumber,
                                        workplacePostcode,
                                        region,
                                        acceptedTermsAndConditions);
        }

    }

}
