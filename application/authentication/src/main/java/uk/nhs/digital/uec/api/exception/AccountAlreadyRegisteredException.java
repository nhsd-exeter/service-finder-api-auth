package uk.nhs.digital.uec.api.exception;

public class AccountAlreadyRegisteredException extends UserManagementException {

  private static final long serialVersionUID = 3198261664938368667L;

  public AccountAlreadyRegisteredException(String account) {
    super(
      UserManagementExceptionCode.ACCOUNT_ALREADY_REGISTERED.getMessage() +
      account
    );
  }

  public String getCode() {
    return UserManagementExceptionCode.ACCOUNT_ALREADY_REGISTERED.getCode();
  }
}
