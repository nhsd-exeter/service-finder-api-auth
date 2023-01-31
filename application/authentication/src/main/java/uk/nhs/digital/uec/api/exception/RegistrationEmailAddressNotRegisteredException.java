package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when a request uses an email address which is not registered when calling the Cognito API.
 */
public class RegistrationEmailAddressNotRegisteredException
  extends UserManagementException {

  private static final long serialVersionUID = -5328919693357289536L;

  public RegistrationEmailAddressNotRegisteredException(String account) {
    super(
      UserManagementExceptionCode.EMAIL_ADDRESS_NOT_REGISTERED.getMessage() +
      account
    );
  }

  public String getCode() {
    return UserManagementExceptionCode.EMAIL_ADDRESS_NOT_REGISTERED.getCode();
  }
}
