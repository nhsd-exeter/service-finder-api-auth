package uk.nhs.digital.uec.api.exception;

public class RegistrationExpiredCodeException extends UserManagementException {

  private static final long serialVersionUID = 3198261664938368667L;

  public RegistrationExpiredCodeException(String account) {
    super(
      UserManagementExceptionCode.REGISTRATION_EXPIRED_CODE.getMessage() +
      account
    );
  }

  public String getCode() {
    return UserManagementExceptionCode.REGISTRATION_EXPIRED_CODE.getCode();
  }
}
