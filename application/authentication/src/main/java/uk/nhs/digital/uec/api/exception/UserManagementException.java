package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when there is a server error when calling the Cognito API.
 */
public class UserManagementException extends RuntimeException {

    public UserManagementException(String message) {
        super(message);
    }

}
