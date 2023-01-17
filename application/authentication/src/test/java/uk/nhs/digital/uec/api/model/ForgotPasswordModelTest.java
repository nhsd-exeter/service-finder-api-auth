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
 * Test for {@link ForgotPasswordModel}
 */
public class ForgotPasswordModelTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldConstruct() {
        // Given
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel();
        forgotPasswordModel.setEmailAddress("test@example.com");

        // When
        Set<ConstraintViolation<ForgotPasswordModel>> constraintViolations = validator.validate(forgotPasswordModel);

        // Then
        assertThat(constraintViolations.size(), is(0));
    }

    @Test
    public void shouldFailToConstructGivenNullEmail() {
        // Given
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel();

        // When
        Set<ConstraintViolation<ForgotPasswordModel>> constraintViolations = validator.validate(forgotPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("emailAddress must not be null"));
    }

    @Test
    public void shouldFailToConstructGivenInvalidEmail() {
        // Given
        String emailAddress = "test.example.com";
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel();
        forgotPasswordModel.setEmailAddress(emailAddress);

        // When
        Set<ConstraintViolation<ForgotPasswordModel>> constraintViolations = validator.validate(forgotPasswordModel);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("emailAddress must be valid"));
    }

}
