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
 * Test for {@link UserLogin}
 */
public class UserLoginTest {

    private Validator validator;

    @Before
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void shouldConstruct() {
        // Given
        UserLogin userLogin = new UserLogin();
        userLogin.setEmailAddress("AuthenticationResultTypeTestFactory@example.com");
        userLogin.setPassword("password");

        // When
        Set<ConstraintViolation<UserLogin>> constraintViolations = validator.validate(userLogin);

        // Then
        assertThat(constraintViolations.size(), is(0));
    }

    @Test
    public void shouldFailToConstructGivenInvalidEmail() {
        // Given
        String emailAddress = "AuthenticationResultTypeTestFactory.example";
        UserLogin userLogin = new UserLogin();
        userLogin.setEmailAddress(emailAddress);
        userLogin.setPassword("password");

        // When
        Set<ConstraintViolation<UserLogin>> constraintViolations = validator.validate(userLogin);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("email must be valid"));
    }

    @Test
    public void shouldFailToConstructGivenBlankPassword() {
        // Given
        String password = "";
        UserLogin userLogin = new UserLogin();
        userLogin.setEmailAddress("AuthenticationResultTypeTestFactory@example.org");
        userLogin.setPassword(password);

        // When
        Set<ConstraintViolation<UserLogin>> constraintViolations = validator.validate(userLogin);

        // Given
        assertThat(constraintViolations.size(), is(1));
        assertThat(constraintViolations.iterator().next().getMessage(), is("password must not be blank"));
    }

}
