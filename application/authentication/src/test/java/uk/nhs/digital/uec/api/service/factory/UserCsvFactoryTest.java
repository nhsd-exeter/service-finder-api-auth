package uk.nhs.digital.uec.api.service.factory;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import org.junit.Test;

import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.testsupport.UserTestFactory;

/**
 * Test for {@link UserCsvFactory}
 */
public class UserCsvFactoryTest {

    private static final String CSV_OUTPUT = "Name,Email address,Email verification status,Status,Last Login,Region,"
            + "Organisation type,Job type,Date/time created,Numeric user ID,Identity provider ID,"
            + "Date/time last updated,Date/time terms and conditions last accepted,Rejection reason,Roles,Job title,"
            + "Organisation name,Workplace postcode,Telephone number"
            + "\n"
            + "name,email@test.com,Verified,PENDING,29/05/2020 10:22,Test Region,Administrator,Administrator,29/05/2020 10:22,"
            + "0,0,29/05/2020 10:22,29/05/2020 10:22,rejection_reason,Administrator,job_name,org_name,bs23,01234567890"
            + "\n"
            + ",email@test.com,Verified,,29/05/2020 10:22,,,,29/05/2020 10:22,0,0,29/05/2020 10:22,,,,,,,"
            + "\n"
            + ",,Unverified,,,,,,,0,,,,,,,,,"
            + "\n";

    @Test
    public void shouldCreate() {
        // Given
        List<User> users = this.createUserTestData();
        UserCsvFactory userCsvFactory = new UserCsvFactory();

        // When
        byte[] csv = userCsvFactory.create(users);
        String csvString = new String(csv);
        // Then
        assertThat(csvString, is(CSV_OUTPUT));
    }

    private List<User> createUserTestData() {
        ArrayList<User> users = new ArrayList<User>();
        User user = UserTestFactory.createTestUser();
        user.setCreated(LocalDateTime.of(2020, 05, 29, 10, 22));
        user.setApprovalStatusUpdated(LocalDateTime.of(2020, 05, 29, 10, 22));
        user.setUpdated(LocalDateTime.of(2020, 05, 29, 10, 22));
        user.setLastLoggedIn(LocalDateTime.of(2020, 05, 29, 10, 22));
        user.setTermsAndConditionsAccepted(LocalDateTime.of(2020, 05, 29, 10, 22));
        user.setTelephoneNumber("01234567890");
        user.setIdentityProviderId("0");
        users.add(user);

        User incompleteUser = UserTestFactory.createTestUser();
        incompleteUser.setUserDetails(null);
        incompleteUser.getUserAccount().setLastLoggedIn(LocalDateTime.of(2020, 05, 29, 10, 22));
        incompleteUser.getUserAccount().setUpdated(LocalDateTime.of(2020, 05, 29, 10, 22));
        incompleteUser.getUserAccount().setCreated(LocalDateTime.of(2020, 05, 29, 10, 22));
        incompleteUser.getUserAccount().setIdentityProviderId("0");
        users.add(incompleteUser);

        User strangeUser = new User();
        users.add(strangeUser);

        return users;
    }

}
