package uk.nhs.digital.uec.api.exception;

public class RegistrationInvalidCodeException extends UserManagementException {

  private static final long serialVersionUID = 1334628401664412394L;

  public RegistrationInvalidCodeException(String account) {
    super(UserManagementExceptionCode.INVALID_CODE.getMessage() + account);
  }

  public String getCode() {
    return UserManagementExceptionCode.INVALID_CODE.getCode();
  }
}
