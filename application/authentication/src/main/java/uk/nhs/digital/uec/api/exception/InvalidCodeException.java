package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when an invalid confirm user or reset password code is used when calling the Cognito API.
 */
public class InvalidCodeException extends UserManagementException {

    public InvalidCodeException(String message) {
        super(message);
    }
}
