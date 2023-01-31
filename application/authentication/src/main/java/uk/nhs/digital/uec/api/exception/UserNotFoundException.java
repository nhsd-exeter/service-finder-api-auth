package uk.nhs.digital.uec.api.exception;

/**
 * Exception thrown when there is no matching user record for a given ID.
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("User not found");
    }

}
