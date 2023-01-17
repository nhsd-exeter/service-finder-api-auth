package uk.nhs.digital.uec.api.model;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link UnconfirmedUserVerification}
 */
public class UnconfirmedUserVerificationTest {

  private Validator validator;

  @Before
  public void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  @Test
  public void shouldConstruct() {
    // Given
    UnconfirmedUserVerification unconfirmedUserVerification = new UnconfirmedUserVerification(
      "test@example.com"
    );

    // When
    Set<ConstraintViolation<UnconfirmedUserVerification>> constraintViolations = validator.validate(
      unconfirmedUserVerification
    );

    // Then
    assertThat(constraintViolations.size(), is(0));
  }

  @Test
  public void shouldFailToConstructGivenInvalidEmail() {
    // Given
    String emailAddress = "AuthenticationResultTypeTestFactory.example";
    UnconfirmedUserVerification unconfirmedUserVerification = new UnconfirmedUserVerification(
      emailAddress
    );

    // When
    Set<ConstraintViolation<UnconfirmedUserVerification>> constraintViolations = validator.validate(
      unconfirmedUserVerification
    );

    // Given
    assertThat(constraintViolations.size(), is(1));
    assertThat(
      constraintViolations.iterator().next().getMessage(),
      is("email address must be valid")
    );
  }

  @Test
  public void shouldFailToConstructGivenBlankEmail() {
    // Given
    String emailAddress = "";
    UnconfirmedUserVerification unconfirmedUserVerification = new UnconfirmedUserVerification(
      emailAddress
    );

    // When
    Set<ConstraintViolation<UnconfirmedUserVerification>> constraintViolations = validator.validate(
      unconfirmedUserVerification
    );

    // Given
    assertThat(constraintViolations.size(), is(1));
    assertThat(
      constraintViolations.iterator().next().getMessage(),
      is("email address must not be blank")
    );
  }

  @Test
  public void shouldFailToConstructGivenWhitespaceEmail() {
    // Given
    String emailAddress = " ";
    UnconfirmedUserVerification unconfirmedUserVerification = new UnconfirmedUserVerification(
      emailAddress
    );

    // When
    Set<ConstraintViolation<UnconfirmedUserVerification>> constraintViolations = validator.validate(
      unconfirmedUserVerification
    );

    // Given
    assertThat(constraintViolations.size(), is(2));
    List<String> constraintViolationMessages = constraintViolations
      .stream()
      .map(ConstraintViolation::getMessage)
      .collect(Collectors.toList());
    assertThat(
      constraintViolationMessages,
      containsInAnyOrder(
        "email address must not be blank",
        "email address must be valid"
      )
    );
  }

  @Test
  public void shouldFailToConstructGivenNullEmail() {
    // Given
    String emailAddress = null;
    UnconfirmedUserVerification unconfirmedUserVerification = new UnconfirmedUserVerification(
      emailAddress
    );

    // When
    Set<ConstraintViolation<UnconfirmedUserVerification>> constraintViolations = validator.validate(
      unconfirmedUserVerification
    );

    // Given
    assertThat(constraintViolations.size(), is(1));
    assertThat(
      constraintViolations.iterator().next().getMessage(),
      is("email address must not be blank")
    );
  }
}
