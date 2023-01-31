package uk.nhs.digital.uec.api.domain;



import java.time.LocalDateTime;
import java.util.SortedSet;

import uk.nhs.digital.uec.api.model.Location;

public class UserBuilder {

    private long id;

    private String identityProviderId;

    private boolean emailAddressVerified;

    private String emailAddress;

    private String name;

    private String telephoneNumber;

    private String jobName;

    private JobType jobType;

    private String jobTypeOther;

    private String organisationName;

    private OrganisationType organisationType;

    private String organisationTypeOther;

    private String postcode;

    private Region region;

    private String approvalStatus;

    private LocalDateTime approvalStatusUpdated;

    private User approvalStatusUpdatedBy;

    private String rejectionReason;

    private LocalDateTime termsAndConditionsAccepted;

    private SortedSet<Role> roles;

    private LocalDateTime lastLoggedIn;

    private String userState;

    private LocalDateTime inactiveDate;

  private SortedSet<Location> locations;

  public UserBuilder withId(long id) {
        this.id = id;
        return this;
    }

    public UserBuilder withIdentityProviderId(String identityProviderId) {
        this.identityProviderId = identityProviderId;
        return this;
    }

    public UserBuilder withEmailAddressVerified(boolean emailAddressVerified) {
        this.emailAddressVerified = emailAddressVerified;
        return this;
    }

    public UserBuilder withEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
        return this;
    }

    public UserBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder withTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
        return this;
    }

    public UserBuilder withJobName(String jobName) {
        this.jobName = jobName;
        return this;
    }

    public UserBuilder withJobType(JobType jobType) {
        this.jobType = jobType;
        return this;
    }

    public UserBuilder withJobTypeOther(String jobTypeOther) {
        this.jobTypeOther = jobTypeOther;
        return this;
    }

    public UserBuilder withOrganisationName(String organisationName) {
        this.organisationName = organisationName;
        return this;
    }

    public UserBuilder withOrganisationType(OrganisationType organisationType) {
        this.organisationType = organisationType;
        return this;
    }

    public UserBuilder withOrganisationTypeOther(String organisationTypeOther) {
        this.organisationTypeOther = organisationTypeOther;
        return this;
    }

    public UserBuilder withPostcode(String postcode) {
        this.postcode = postcode;
        return this;
    }

    public UserBuilder withRegion(Region region) {
        this.region = region;
        return this;
    }

    public UserBuilder withApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
        return this;
    }

    public UserBuilder withApprovalStatusUpdated(LocalDateTime approvalStatusUpdated) {
        this.approvalStatusUpdated = approvalStatusUpdated;
        return this;
    }

    public UserBuilder withApprovalStatusUpdatedBy(User approvalStatusUpdatedBy) {
        this.approvalStatusUpdatedBy = approvalStatusUpdatedBy;
        return this;
    }

    public UserBuilder withRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
        return this;
    }

    public UserBuilder withTermsAndConditionsAccepted(LocalDateTime termsAndConditionsAccepted) {
        this.termsAndConditionsAccepted = termsAndConditionsAccepted;
        return this;
    }

    public UserBuilder withRoles(SortedSet<Role> roles) {
        this.roles = roles;
        return this;
    }

    public UserBuilder withLastLoggedIn(LocalDateTime lastLoggedIn){
        this.lastLoggedIn = lastLoggedIn;
        return this;
    }

    public UserBuilder withUserState(String userState){
        this.userState = userState;
        return this;
    }

    public UserBuilder withInactiveDate(LocalDateTime inactiveDate){
        this.inactiveDate = inactiveDate;
        return this;
    }

    public UserBuilder withLocations(SortedSet<Location> locations){
      this.locations = locations;
      return this;
    }

    public User createUser() {
        UserAccount userAccount = new UserAccount(id, identityProviderId, emailAddressVerified, emailAddress, lastLoggedIn, userState, locations);
        UserDetails userDetails = new UserDetails(id, userAccount, name, telephoneNumber, jobName, jobType, jobTypeOther, organisationName, organisationType, organisationTypeOther, postcode, region, approvalStatus, approvalStatusUpdated, null, rejectionReason, termsAndConditionsAccepted, roles);
        userAccount.setUserDetails(userDetails);
        userAccount.setInactiveDate(inactiveDate);
        User user = new User(userAccount);
        UserAccount approvalStatusUpdatedByUserAccount = null;
        if (this.approvalStatusUpdatedBy!=null)
        {
            approvalStatusUpdatedByUserAccount = approvalStatusUpdatedBy.getUserAccount();
            user.setUpdatedBy(approvalStatusUpdatedBy);
            user.setApprovalStatusUpdatedBy(approvalStatusUpdatedBy);
        }
        return user;
    }

}
