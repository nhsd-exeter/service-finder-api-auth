package uk.nhs.digital.uec.api.model;

import org.junit.Before;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test for {@link EmailVerification}
 */
public class EmailVerificationTest {

    private static final String CODE = "123456";

    private static final String EMAIL_ADDRESS = "test@example.com";

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldConstruct() {
        // Given
        EmailVerification emailVerification = new EmailVerification(EMAIL_ADDRESS, CODE);

        // When
        Set<ConstraintViolation<EmailVerification>> constraintViolations = validator.validate(emailVerification);

        // Then
        assertThat(constraintViolations.size(), is(0));
    }

    @Test
    public void shouldFailToConstructGivenInvalidEmail() {
        // Given
        String emailAddress = "AuthenticationResultTypeTestFactory.example";
        EmailVerification emailVerification = new EmailVerification(emailAddress, CODE);

        // When
        Set<ConstraintViolation<EmailVerification>> constraintViolations = validator.validate(emailVerification);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("email address must be valid"));
    }

    @Test
    public void shouldFailToConstructGivenBlankEmail() {
        // Given
        String emailAddress = "";
        EmailVerification emailVerification = new EmailVerification(emailAddress, CODE);

        // When
        Set<ConstraintViolation<EmailVerification>> constraintViolations = validator.validate(emailVerification);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("email address must not be blank"));
    }

    @Test
    public void shouldFailToConstructGivenNullEmail() {
        // Given
        String emailAddress = null;
        EmailVerification emailVerification = new EmailVerification(emailAddress, CODE);

        // When
        Set<ConstraintViolation<EmailVerification>> constraintViolations = validator.validate(emailVerification);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("email address must not be blank"));
    }

    @Test
    public void shouldFailToConstructGivenBlankCode() {
        // Given
        String code = "";
        EmailVerification emailVerification = new EmailVerification(EMAIL_ADDRESS, code);

        // When
        Set<ConstraintViolation<EmailVerification>> constraintViolations = validator.validate(emailVerification);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("code must be 6 characters in length"));
    }

    @Test
    public void shouldFailToConstructGivenNullCode() {
        // Given
        String code = null;
        EmailVerification emailVerification = new EmailVerification(EMAIL_ADDRESS, code);

        // When
        Set<ConstraintViolation<EmailVerification>> constraintViolations = validator.validate(emailVerification);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("code must not be null"));
    }

    @Test
    public void shouldFailToConstructGivenShortCode() {
        // Given
        String code = "12345";
        EmailVerification emailVerification = new EmailVerification(EMAIL_ADDRESS, code);

        // When
        Set<ConstraintViolation<EmailVerification>> constraintViolations = validator.validate(emailVerification);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("code must be 6 characters in length"));
    }

}
