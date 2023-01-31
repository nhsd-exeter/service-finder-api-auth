package uk.nhs.digital.uec.api.domain;

import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import uk.nhs.digital.uec.api.testsupport.UserAccountTestFactory;

public class UserAccountTest {


    @Test
    public void createUserFromUserAccountWithUserDetails(){
        UserAccount userAccount = UserAccountTestFactory.createTestUserAccountWithUserDetails();
        User user = userAccount.convertToUser();

        assertThat(user.getEmailAddress(), is(UserAccountTestFactory.emailAddress));
        assertThat(user.isEmailAddressVerified(), is(UserAccountTestFactory.emailAddressVerified));
        assertThat(user.getIdentityProviderId(), is(UserAccountTestFactory.identityProviderId));
        assertThat(user.getUserState(), is(UserAccountTestFactory.userState));
        assertThat(user.getName(), is(UserAccountTestFactory.name));
        assertThat(user.getTelephoneNumber(), is(UserAccountTestFactory.telephoneNumber));
        assertThat(user.getJobName(), is(UserAccountTestFactory.jobName));
        assertThat(user.getJobType(), is(UserAccountTestFactory.jobType));
        assertThat(user.getJobTypeOther(), is(UserAccountTestFactory.jobTypeOther));
        assertThat(user.getPostcode(), is(UserAccountTestFactory.postcode));
        assertThat(user.getRejectionReason(), is(UserAccountTestFactory.rejectionReason));
        assertThat(user.getRegion(), is(UserAccountTestFactory.region));
        assertThat(user.getRoles(), is(UserAccountTestFactory.roles));
    }

}
