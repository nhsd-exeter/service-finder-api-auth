package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when a requested user attribute is not present in Cognito
 */
public class CognitoNoUserAttributeException extends RuntimeException {

  /**
   * Generated serial ID
   */
  private static final long serialVersionUID = 1345785334377236903L;

  public CognitoNoUserAttributeException(String message) {
    super(message);
  }
}
