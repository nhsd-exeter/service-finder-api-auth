package uk.nhs.digital.uec.api.domain;

import java.time.LocalDateTime;
import java.util.SortedSet;

public class UserDetailsBuilder {

    private long id;
    private UserAccount userAccount;
    private LocalDateTime created;
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
    private UserAccount approvalStatusUpdatedBy;
    private String rejectionReason;
    private LocalDateTime termsAndConditionsAccepted;
    private SortedSet<Role> roles;


    public UserDetailsBuilder withId(long id){
        this.id = id;
        return this;
    }

    public UserDetailsBuilder withUserAccount(UserAccount userAccount){
        this.userAccount = userAccount;
        return this;
    }

    public UserDetailsBuilder withCreated(LocalDateTime created){
        this.created = created;
        return this;
    }

    public UserDetailsBuilder withName(String name){
        this.name = name;
        return this;
    }

    public UserDetailsBuilder withTelephoneNumber(String telephoneNumber){
        this.telephoneNumber = telephoneNumber;
        return this;
    }

    public UserDetailsBuilder withJobName(String jobName){
        this.jobName = jobName;
        return this;
    }

    public UserDetailsBuilder withJobType(JobType jobType){
        this.jobType = jobType;
        return this;
    }

    public UserDetailsBuilder withJobTypeOther(String jobTypeOther){
        this.jobTypeOther = jobTypeOther;
        return this;
    }

    public UserDetailsBuilder withOrganisationName(String organisationName){
        this.organisationName = organisationName;
        return this;
    }

    public UserDetailsBuilder withOrganisationType(OrganisationType organisationType){
        this.organisationType = organisationType;
        return this;
    }

    public UserDetailsBuilder withOrganisationTypeOther(String organisationTypeOther){
        this.organisationTypeOther = organisationTypeOther;
        return this;
    }

    public UserDetailsBuilder withPostCode(String postcode){
        this.postcode = postcode;
        return this;
    }

    public UserDetailsBuilder withRegion(Region region){
        this.region = region;
        return this;
    }

    public UserDetailsBuilder withApprovalStatus(String approvalStatus){
        this.approvalStatus = approvalStatus;
        return this;
    }

    public UserDetailsBuilder withApprovalStatusUpdated(LocalDateTime approvalStatusUpdated){
        this.approvalStatusUpdated = approvalStatusUpdated;
        return this;
    }

    public UserDetailsBuilder withApprovalStatusUpdatedBy(UserAccount approvalStatusUpdatedBy){
        this.approvalStatusUpdatedBy = approvalStatusUpdatedBy;
        return this;
    }

    public UserDetailsBuilder withRejectionReason(String rejectionReason){
        this.rejectionReason = rejectionReason;
        return this;
    }

    public UserDetailsBuilder withTermsAndConditionsAccepted(LocalDateTime termsAndConditionsAccepted){
        this.termsAndConditionsAccepted = termsAndConditionsAccepted;
        return this;
    }

    public UserDetailsBuilder withRoles(SortedSet<Role> roles){
        this.roles = roles;
        return this;
    }

    public UserDetails build(){
        return new UserDetails(id, userAccount, created, name, telephoneNumber, jobName,
                                jobType, jobTypeOther, organisationName, organisationType, organisationTypeOther,
                                postcode, region, approvalStatus, approvalStatusUpdated, approvalStatusUpdatedBy,
                                rejectionReason, termsAndConditionsAccepted, roles);
    }
}
