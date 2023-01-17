package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when an invalid credentials exception occurs when calling the Cognito API.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException(String message) {
        super(message);
    }

}
