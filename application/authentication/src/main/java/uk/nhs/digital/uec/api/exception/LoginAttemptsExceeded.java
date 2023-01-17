package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when login attempts have exceeded for a given user.
 */
public class LoginAttemptsExceeded extends UserManagementException {

    public LoginAttemptsExceeded(String message) {
        super(message);
    }

}
