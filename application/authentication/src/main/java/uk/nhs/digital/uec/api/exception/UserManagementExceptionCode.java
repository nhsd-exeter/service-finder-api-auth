package uk.nhs.digital.uec.api.exception;

public enum UserManagementExceptionCode {

    ACCOUNT_ALREADY_REGISTERED("REG01", "The account is already registered for email address: "),
    APPROVED_ACCOUNT_ALREADY_REGISTERED("REG02", "The approved account is already registered for email address: "),
    REGISTRATION_EXPIRED_CODE("REG03", "The verification code has expired for account: "),
    INVALID_CODE("REG04", "The verification code is invalid for account: "),
    EMAIL_ADDRESS_NOT_REGISTERED("REG05", "Account is not registered: ");

    private final String code;

    private final String message;

    UserManagementExceptionCode(String code, String message){
        this.code = code;
        this.message = message;
    }

    public String getCode()
    {
        return this.code;
    }

    public String getMessage()
    {
        return this.message;
    }

}
