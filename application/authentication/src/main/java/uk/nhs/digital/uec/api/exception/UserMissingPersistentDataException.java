package uk.nhs.digital.uec.api.exception;

/**
 * Exception thrown when there is no persistent information for a user.
 */
public class UserMissingPersistentDataException extends UserManagementException {

    public UserMissingPersistentDataException() {
        super("User is missing persistent data");
    }

}
