package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when login attempts have exceeded for a given user.
 */
public class LoginFailedInactiveUserException extends UserManagementException {

    public LoginFailedInactiveUserException(String message) {
        super(message);
    }

    public LoginFailedInactiveUserException() {
        super("User account is inactive");
    }

}
