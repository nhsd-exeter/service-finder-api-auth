package uk.nhs.digital.uec.api.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.uec.api.domain.LoginAttempt;
import uk.nhs.digital.uec.api.repository.LoginAttemptRepository;
import uk.nhs.digital.uec.api.service.impl.LoginAttemptServiceDefaultImpl;

import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link LoginAttemptServiceDefaultImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class LoginAttemptServiceDefaultImplTest {

    private static final int MAX_LOGIN_ATTEMPTS = 10;

    @Mock
    private LoginAttemptRepository repository;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private LoginAttemptServiceDefaultImpl service;

    @Before
    public void setUp() {
        service = new LoginAttemptServiceDefaultImpl(repository, MAX_LOGIN_ATTEMPTS);
    }

    @Test
    public void shouldAddGivenNoPreviousLoginAttempt() {
        // Given
        String emailAddress = "test@example.com";
        given(repository.findByEmailAddressIgnoreCase(emailAddress)).willReturn(Optional.empty());

        // When
        service.add(emailAddress);

        // Then
        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(repository).saveAndFlush(captor.capture());
        verify(repository).saveAndFlush(captor.getValue());
    }

    @Test
    public void shouldAddGivenPreviousLoginAttempt() {
        // Given
        String emailAddress = "test@example.com";
        LoginAttempt loginAttempt = mock(LoginAttempt.class);
        given(loginAttempt.getAttempts()).willReturn(1);
        given(repository.findByEmailAddressIgnoreCase(emailAddress)).willReturn(Optional.of(loginAttempt));

        // When
        service.add(emailAddress);

        // Then
        verify(loginAttempt).setAttempts(2);
    }

    @Test
    public void removeShouldRemoveGivenLoginAttempt() {
        // Given
        String emailAddress = "test@example.com";
        LoginAttempt loginAttempt = new LoginAttempt(emailAddress, 1);
        given(repository.findByEmailAddressIgnoreCase(emailAddress)).willReturn(Optional.of(loginAttempt));

        // When
        service.remove(emailAddress);

        // Then
        ArgumentCaptor<LoginAttempt> captor = ArgumentCaptor.forClass(LoginAttempt.class);
        verify(repository).delete(captor.capture());
        verify(repository).delete(captor.getValue());
    }

    @Test
    public void removeShouldNotRemoveGivenNoLoginAttempt() {
        // Given
        String emailAddress = "test@example.com";
        given(repository.findByEmailAddressIgnoreCase(emailAddress)).willReturn(Optional.empty());

        // When
        service.remove(emailAddress);

        // Then
        verify(repository, times(0)).delete(any());
    }

    @Test
    public void isBlockedShouldReturnTrueGivenFailedLoginExceededLimit() {
        // Given
        String emailAddress = "test@example.com";
        LoginAttempt loginAttempt = new LoginAttempt(emailAddress, 10);
        given(repository.findByEmailAddressIgnoreCase(emailAddress)).willReturn(Optional.of(loginAttempt));

        // When
        boolean blocked = service.isBlocked(emailAddress);

        // Then
        assertThat(blocked, is(true));
    }

    @Test
    public void isBlockedShouldReturnFalseGivenNoFailedLoginExceededLimit() {
        // Given
        String emailAddress = "test@example.com";
        LoginAttempt loginAttempt = new LoginAttempt(emailAddress, 1);
        given(repository.findByEmailAddressIgnoreCase(emailAddress)).willReturn(Optional.of(loginAttempt));

        // When
        boolean blocked = service.isBlocked(emailAddress);

        // Then
        assertThat(blocked, is(false));
    }

    @Test
    public void addShouldThrowIllegalArgumentExceptionGivenEmptyEmailAddress() {
        // Given
        String emailAddress = "";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("emailAddress must have text");

        // When
        service.add(emailAddress);
    }

    @Test
    public void addShouldThrowIllegalArgumentExceptionGivenNullEmailAddress() {
        // Given
        String emailAddress = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("emailAddress must have text");

        // When
        service.add(emailAddress);
    }

    @Test
    public void removeShouldThrowIllegalArgumentExceptionGivenEmptyEmailAddress() {
        // Given
        String emailAddress = "";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("emailAddress must have text");

        // When
        service.remove(emailAddress);
    }

    @Test
    public void removeShouldThrowIllegalArgumentExceptionGivenNullEmailAddress() {
        // Given
        String emailAddress = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("emailAddress must have text");

        // When
        service.remove(emailAddress);
    }

    @Test
    public void isBlockedShouldThrowIllegalArgumentExceptionGivenEmptyEmailAddress() {
        // Given
        String emailAddress = "";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("emailAddress must have text");

        // When
        service.isBlocked(emailAddress);
    }

    @Test
    public void isBlockedShouldThrowIllegalArgumentExceptionGivenNullEmailAddress() {
        // Given
        String emailAddress = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("emailAddress must have text");

        // When
        service.isBlocked(emailAddress);
    }

}
