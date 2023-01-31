package uk.nhs.digital.uec.api.exception;

public class ApprovedAccountAlreadyRegisteredException
  extends UserManagementException {

  private static final long serialVersionUID = 3198261664938368667L;

  public ApprovedAccountAlreadyRegisteredException(String account) {
    super(
      UserManagementExceptionCode.APPROVED_ACCOUNT_ALREADY_REGISTERED.getMessage() +
      account
    );
  }

  public String getCode() {
    return UserManagementExceptionCode.APPROVED_ACCOUNT_ALREADY_REGISTERED.getCode();
  }
}
