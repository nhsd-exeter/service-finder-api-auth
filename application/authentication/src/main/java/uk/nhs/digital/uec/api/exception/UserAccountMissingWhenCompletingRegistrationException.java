package uk.nhs.digital.uec.api.exception;

public class UserAccountMissingWhenCompletingRegistrationException
  extends UserManagementException {

  private static final long serialVersionUID = 3198261664938368667L;

  public UserAccountMissingWhenCompletingRegistrationException(String message) {
    super(message);
  }
}
