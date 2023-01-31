package uk.nhs.digital.uec.api.model;

import java.time.LocalDateTime;
import java.util.SortedSet;

import uk.nhs.digital.uec.api.domain.User;

public class UserDetailBuilder {

    private String emailAddress;

    private boolean emailAddressVerified;

    private LocalDateTime registrationDate;

    private String name;

    private String telephoneNumber;

    private String jobName;

    private String jobType;

    private String jobTypeOther;

    private String organisationName;

    private String organisationType;

    private String organisationTypeOther;

    private String postcode;

    private String region;

    private String approvalStatus;

    private String rejectionReason;

    private SortedSet<String> roles;

    private LocalDateTime lastLoggedIn;

    private String userState;

    public UserDetailBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public UserDetailBuilder withEmailAddressVerified(boolean emailAddressVerified) {
        this.emailAddressVerified = emailAddressVerified;
        return this;
    }

    public UserDetailBuilder withRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
        return this;
    }

    public UserDetailBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserDetailBuilder withTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
        return this;
    }

    public UserDetailBuilder withJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public UserDetailBuilder withJobType(String jobType) {
        this.jobType = jobType;
        return this;
    }

    public UserDetailBuilder withJobTypeOther(String jobTypeOther) {
        this.jobTypeOther = jobTypeOther;
        return this;
    }

    public UserDetailBuilder withOrganisationName(String organisationName) {
        this.organisationName = organisationName;
        return this;
    }

    public UserDetailBuilder withOrganisationType(String organisationType) {
        this.organisationType = organisationType;
        return this;
    }

    public UserDetailBuilder withOrganisationTypeOther(String organisationTypeOther) {
        this.organisationTypeOther = organisationTypeOther;
        return this;
    }

    public UserDetailBuilder withPostcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public UserDetailBuilder withRegion(String region) {
        this.region = region;
        return this;
    }

    public UserDetailBuilder withApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
        return this;
    }

    public UserDetailBuilder withRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public UserDetailBuilder withRoles(SortedSet<String> roles) {
        this.roles = roles;
        return this;
    }

    public UserDetailBuilder withLastLoggedIn(LocalDateTime lastLoggedIn) {
        this.lastLoggedIn = lastLoggedIn;
        return this;
    }

    public UserDetailBuilder withUserState(String userState) {
        this.userState = userState;
        return this;
    }

    private boolean userStateToInactive() {
        return this.userState != null && userState.equals(User.APPROVAL_STATUS_APPROVED);
    }

    public UserDetail create() {
        return new UserDetail(emailAddress,
                emailAddressVerified,
                registrationDate,
                name,
                telephoneNumber,
                jobName,
                jobType,
                jobTypeOther,
                organisationName,
                organisationType,
                organisationTypeOther,
                postcode,
                region,
                approvalStatus,
                rejectionReason,
                roles,
                lastLoggedIn,
                this.userStateToInactive());
    }

}
