package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when a user is not authenticated to perform an action occurs when calling the Cognito API.
 */

public class AuthenticationException extends UserManagementException {

    public AuthenticationException(String message) {
        super(message);
    }

}
