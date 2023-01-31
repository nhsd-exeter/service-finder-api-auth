package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when a user tries to login before completing their registration.
 */
public class UserIncompleteException extends RuntimeException {

    public UserIncompleteException(String message) {
        super(message);
    }

}
