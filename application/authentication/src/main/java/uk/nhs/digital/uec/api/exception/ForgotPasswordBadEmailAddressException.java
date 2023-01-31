package uk.nhs.digital.uec.api.exception;

/**
 * Exception thrown when a user initiates forgot password with an email address that doesn't match one in the database.
 * This exception is handled in UserManagementControllerAdvice which returns 200-OK to the user so we don't leak information
 */
public class ForgotPasswordBadEmailAddressException extends UserManagementException {

    public ForgotPasswordBadEmailAddressException(String message) {
        super(message);
    }

}
