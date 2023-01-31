package uk.nhs.digital.uec.api.exception;

public class UserAccountMissingWhenVerifyingEmailException
  extends UserManagementException {

  private static final long serialVersionUID = 3198261664938368667L;

  public UserAccountMissingWhenVerifyingEmailException(String message) {
    super(message);
  }
}
