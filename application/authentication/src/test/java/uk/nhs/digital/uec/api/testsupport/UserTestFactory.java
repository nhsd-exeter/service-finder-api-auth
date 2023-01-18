package uk.nhs.digital.uec.api.testsupport;

import uk.nhs.digital.uec.api.domain.JobType;
import uk.nhs.digital.uec.api.domain.OrganisationType;
import uk.nhs.digital.uec.api.domain.Region;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserBuilder;

import java.util.*;

import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_PENDING;

public class UserTestFactory {

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

    public static final SortedSet<Role> roles = Collections.unmodifiableSortedSet(new TreeSet<>(List.of(role)));

    public static User createTestUser() {
        return atestUser().createUser();
    }

    public static UserBuilder atestUser() {
        return new UserBuilder()
                .withEmailAddress("email@test.com")
                .withEmailAddressVerified(true)
                .withName(name)
                .withTelephoneNumber(telephoneNumber)
                .withJobName(jobName)
                .withJobType(jobType)
                .withJobTypeOther(jobTypeOther)
                .withOrganisationName(orgName)
                .withOrganisationTypeOther(orgTypeOther)
                .withOrganisationType(orgType)
                .withPostcode(postcode)
                .withRegion(region)
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .withRejectionReason(rejectionReason)
                .withRoles(roles);
    }

}
