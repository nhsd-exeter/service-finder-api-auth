package uk.nhs.digital.uec.api.domain;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_APPROVED;
import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_PENDING;
import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_REJECTED;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import uk.nhs.digital.uec.api.exception.InvalidApprovalStatusChangeException;
import uk.nhs.digital.uec.api.exception.MissingRejectionReasonException;
import uk.nhs.digital.uec.api.exception.RejectionReasonUpdateException;
import uk.nhs.digital.uec.api.testsupport.UserTestFactory;

public class UserTest {

    private static final String REJECTION_REASON = "abcde";

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private long time = 99999;

    private Clock clock = Clock.fixed(Instant.ofEpochSecond(time), ZoneId.of("UTC"));

    private User actor = new UserBuilder().createUser();

    @Test
    public void updateDetailsFromUserSuccessfullyUpdatesUserDetails() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .createUser();

        User updatedUser = UserTestFactory.createTestUser();

        // When
        user.updateDetailsFromUser(updatedUser, actor);

        // Then
        assertThat(user.getUpdatedBy(), is(actor));
        assertThat(user.getApprovalStatus(), is(APPROVAL_STATUS_PENDING));
        assertThat(user.getName(), is(UserTestFactory.name));
        assertThat(user.getTelephoneNumber(), is(UserTestFactory.telephoneNumber));
        assertThat(user.getJobName(), is(UserTestFactory.jobName));
        assertThat(user.getJobType(), is(UserTestFactory.jobType));
        assertThat(user.getJobTypeOther(), is(UserTestFactory.jobTypeOther));
        assertThat(user.getOrganisationName(), is(UserTestFactory.orgName));
        assertThat(user.getOrganisationType(), is(UserTestFactory.orgType));
        assertThat(user.getOrganisationTypeOther(), is(UserTestFactory.orgTypeOther));
        assertThat(user.getApprovalStatus(), is(UserTestFactory.approvalStatus));
        assertThat(user.getPostcode(), is(UserTestFactory.postcode));
        assertThat(user.getRegion(), is(UserTestFactory.region));
    }

    @Test
    public void processApprovalStatusChangeRecordsValidApprovalStatusChanges() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .withUserState(User.USER_STATE_ACTIVE)
                .createUser();

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_APPROVED, null, User.USER_STATE_ACTIVE, actor, clock);

        // Then
        assertThat(user.getApprovalStatus(), is(APPROVAL_STATUS_APPROVED));
        assertThat(user.getApprovalStatusUpdatedBy(), is(actor));
        assertThat(user.getApprovalStatusUpdated().toEpochSecond(ZoneOffset.UTC), is(time));
    }

    @Test
    public void processApprovalStatusChangedWhenUserIsInactiveAndApprovalStatusSetToApproved() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .withUserState(User.USER_STATE_INACTIVE)
                .withInactiveDate(LocalDateTime.now())
                .createUser();

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_APPROVED, null, User.USER_STATE_INACTIVE, actor, clock);

        // Then
        assertThat(user.getApprovalStatus(), is(APPROVAL_STATUS_APPROVED));
        assertThat(user.getApprovalStatusUpdatedBy(), is(actor));
        assertThat(user.getApprovalStatusUpdated().toEpochSecond(ZoneOffset.UTC), is(time));
        assertThat(user.getUserState(), is(User.USER_STATE_ACTIVE));
        assertNull(user.getInactiveDate());
    }

    @Test
    public void processApprovalStatusChangedWhenUserIsInactiveAndApprovalStatusNotSetToApproved() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_REJECTED)
                .withUserState(User.USER_STATE_INACTIVE)
                .createUser();

        // Expectations
        exceptionRule.expect(InvalidApprovalStatusChangeException.class);

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_PENDING, null, User.USER_STATE_INACTIVE, actor, clock);
    }

    @Test
    public void processApprovalStatusChangeSucceedsGivenPendingToApproved() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .withUserState(User.USER_STATE_ACTIVE)
                .createUser();

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_APPROVED, null, User.USER_STATE_ACTIVE, actor, clock);

        // Then
        assertThat(user.getApprovalStatus(), is(APPROVAL_STATUS_APPROVED));
    }

    @Test
    public void processApprovalStatusChangeSucceedsGivenPendingToRejectedAndSetsUserToInactive() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .withUserState(User.USER_STATE_ACTIVE)
                .createUser();

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_REJECTED, REJECTION_REASON, User.USER_STATE_ACTIVE, actor,
                clock);

        // Then
        assertThat(user.getApprovalStatus(), is(APPROVAL_STATUS_REJECTED));
        assertThat(user.getRejectionReason(), is(REJECTION_REASON));
        assertThat(user.getUserState(), is(User.USER_STATE_INACTIVE));
        assertNotNull(user.getInactiveDate());
    }

    @Test
    public void processApprovalStatusChangeSucceedsGivenRejectedToApproved() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_REJECTED)
                .withUserState(User.USER_STATE_ACTIVE)
                .createUser();

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_APPROVED, null, User.USER_STATE_ACTIVE, actor, clock);

        // Then
        assertThat(user.getApprovalStatus(), is(APPROVAL_STATUS_APPROVED));
    }

    @Test
    public void processApprovalStatusChangeThrowsInvalidApprovalStatusChangeExceptionGivenInvalidApprovalStatusChanges() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_APPROVED)
                .withUserState(User.USER_STATE_ACTIVE)
                .createUser();

        // Expectations
        exceptionRule.expect(InvalidApprovalStatusChangeException.class);
        exceptionRule.expectMessage("Cannot change from APPROVED to PENDING");

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_PENDING, null, User.USER_STATE_ACTIVE, actor, clock);
    }

    @Test
    public void processApprovalStatusChangeThrowsMissingRejectionReasonExceptionGivenRejectingWithoutRejectionReason() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .withUserState(User.USER_STATE_ACTIVE)
                .createUser();

        // Expectations
        exceptionRule.expect(MissingRejectionReasonException.class);

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_REJECTED, null, User.USER_STATE_ACTIVE, actor, clock);
    }

    @Test
    public void processApprovalStatusChangeThrowsMissingRejectionReasonExceptionGivenNotRejectingWithRejectionReason() {

        // Given
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .withUserState(User.USER_STATE_ACTIVE)
                .createUser();

        // Expectations
        exceptionRule.expect(RejectionReasonUpdateException.class);

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_APPROVED, "abcde", User.USER_STATE_ACTIVE, actor, clock);
    }

    @Test
    public void processApprovalStatusChangeSucceedsGivenNotRejectingAndRejectionReasonSuppliedButIsUnchanged() {

        // Given
        String originalRejectionReason = "abcde";
        User user = new UserBuilder()
                .withApprovalStatus(APPROVAL_STATUS_PENDING)
                .withRejectionReason(originalRejectionReason)
                .withUserState(User.USER_STATE_ACTIVE)
                .createUser();

        // When
        user.processApprovalStatusChange(APPROVAL_STATUS_APPROVED, originalRejectionReason, User.USER_STATE_ACTIVE,
                actor, clock);
    }

}
