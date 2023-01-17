package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when a request uses an email address which is not registered when calling the Cognito API.
 */
public class EmailAddressNotRegisteredException extends UserManagementException {

    public EmailAddressNotRegisteredException(String message) {
        super(message);
    }

}
