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
 * Test for {@link ResetPasswordModel}
 */
public class ResetPasswordModelTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldConstruct() {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setEmailAddress("test@example.com");
        resetPasswordModel.setCode("code");
        resetPasswordModel.setPassword("password");

        // When
        Set<ConstraintViolation<ResetPasswordModel>> constraintViolations = validator.validate(resetPasswordModel);

        // Then
        assertThat(constraintViolations.size(), is(0));
    }

    @Test
    public void shouldFailToConstructGivenNullEmail() {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setCode("code");
        resetPasswordModel.setPassword("password");

        // When
        Set<ConstraintViolation<ResetPasswordModel>> constraintViolations = validator.validate(resetPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("emailAddress must not be null"));
    }

    @Test
    public void shouldFailToConstructGivenInvalidEmail() {
        // Given
        String emailAddress = "test.example.com";
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setEmailAddress(emailAddress);
        resetPasswordModel.setCode("code");
        resetPasswordModel.setPassword("password");

        // When
        Set<ConstraintViolation<ResetPasswordModel>> constraintViolations = validator.validate(resetPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("emailAddress must be valid"));
    }

    @Test
    public void shouldFailToConstructGivenNullCode() {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setEmailAddress("test@example.com");
        resetPasswordModel.setPassword("password");

        // When
        Set<ConstraintViolation<ResetPasswordModel>> constraintViolations = validator.validate(resetPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("code must not be blank"));
    }

    @Test
    public void shouldFailToConstructGivenBlankCode() {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setEmailAddress("test@example.com");
        resetPasswordModel.setCode("");
        resetPasswordModel.setPassword("password");

        // When
        Set<ConstraintViolation<ResetPasswordModel>> constraintViolations = validator.validate(resetPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("code must not be blank"));
    }

    @Test
    public void shouldFailToConstructGivenNullPassword() {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setEmailAddress("test@example.com");
        resetPasswordModel.setCode("code");

        // When
        Set<ConstraintViolation<ResetPasswordModel>> constraintViolations = validator.validate(resetPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("password must not be null"));
    }

    @Test
    public void shouldFailToConstructGivenBlankPassword() {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setEmailAddress("test@example.com");
        resetPasswordModel.setCode("code");
        resetPasswordModel.setPassword("");

        // When
        Set<ConstraintViolation<ResetPasswordModel>> constraintViolations = validator.validate(resetPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("password must be at least 8 characters in length"));
    }

    @Test
    public void shouldFailToConstructGivenShortPassword() {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel();
        resetPasswordModel.setEmailAddress("test@example.com");
        resetPasswordModel.setCode("code");
        resetPasswordModel.setPassword("pass");

        // When
        Set<ConstraintViolation<ResetPasswordModel>> constraintViolations = validator.validate(resetPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("password must be at least 8 characters in length"));
    }

}
