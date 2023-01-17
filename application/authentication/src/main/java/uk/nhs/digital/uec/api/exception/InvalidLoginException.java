package uk.nhs.digital.uec.api.exception;

/**
 * {@link Exception} thrown when an invalid login exception occurs when calling the Cognito API.
 */
public class InvalidLoginException extends Exception {

    public InvalidLoginException(String message) {
        super(message);
    }

}
