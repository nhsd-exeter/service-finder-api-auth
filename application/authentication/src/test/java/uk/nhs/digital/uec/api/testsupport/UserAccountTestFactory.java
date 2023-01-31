package uk.nhs.digital.uec.api.testsupport;

import java.util.Arrays;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

import uk.nhs.digital.uec.api.domain.JobType;
import uk.nhs.digital.uec.api.domain.OrganisationType;
import uk.nhs.digital.uec.api.domain.Region;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserAccountBuilder;
import uk.nhs.digital.uec.api.domain.UserDetailsBuilder;



public class UserAccountTestFactory {

    public static final String emailAddress = "email@test.com";
    public static final boolean emailAddressVerified = true;
    public static final String identityProviderId = "identityProviderId";
    public static final String userState = "ACTIVE";
    public static final String name = "name";
    public static final String telephoneNumber = "01234";
    public static final String jobName = "job_name";
    public static final String jobTypeOther = "job_type_other";
    public static final String orgName = "org_name";
    public static final String orgTypeOther = "org_type_other";
    public static final String postcode = "bs23";
    public static final String rejectionReason = "rejection_reason";
    public static final String regionCode = "region_code";
    public static final String roleCode = "role_code";
    public static final String jobTypeCode = "OTHER";
    public static final String orgTypeCode = "OTHER";
    public static final JobType jobType = new JobType(1, "Administrator", jobTypeCode);
    public static final OrganisationType orgType = new OrganisationType(1, "Administrator", orgTypeCode);
    public static final Region region = new Region(1, "Test Region", regionCode);
    public static final Role role = new Role(1, "Administrator", roleCode);
    public static final String approvalStatus = "PENDING";
    public static final SortedSet<Role> roles = Collections.unmodifiableSortedSet(new TreeSet<>(Arrays.asList(role)));


    public static UserAccount createTestUserAccountWithUserDetails() {
        return aTestUserAccountBuilder().build();
    }

    public static UserAccountBuilder aTestUserAccountBuilder() {
        return new UserAccountBuilder()
            .withEmailAddress(emailAddress)
            .withEmailAddressVerified(emailAddressVerified)
            .withIdentityProviderId(identityProviderId)
            .withUserState(userState)
            .withUserDetails(aTestUserDetailsBuilder().build());
    }

    public static UserDetailsBuilder aTestUserDetailsBuilder(){
        return new UserDetailsBuilder()
            .withName(name)
            .withTelephoneNumber(telephoneNumber)
            .withJobName(jobName)
            .withJobType(jobType)
            .withJobTypeOther(jobTypeOther)
            .withOrganisationName(orgName)
            .withOrganisationTypeOther(orgTypeOther)
            .withOrganisationType(orgType)
            .withPostCode(postcode)
            .withRegion(region)
            .withRejectionReason(rejectionReason)
            .withApprovalStatus(approvalStatus)
            .withRoles(roles);
    }

}
