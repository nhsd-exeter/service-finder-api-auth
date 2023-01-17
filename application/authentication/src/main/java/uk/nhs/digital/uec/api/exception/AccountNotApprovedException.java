package uk.nhs.digital.uec.api.exception;

public class AccountNotApprovedException extends UserManagementException {

  private static final long serialVersionUID = 3198261664938368667L;

  public AccountNotApprovedException(String message) {
    super(message);
  }

  public AccountNotApprovedException() {
    super("User account is not approved");
  }
}
