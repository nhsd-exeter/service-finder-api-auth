package uk.nhs.digital.uec.api.exception;

/**
 * {@link RuntimeException} thrown when an invalid code is used to look up a domain object.
 */
public class InvalidEntityCodeException extends RuntimeException {

    public InvalidEntityCodeException(String message) {
        super(message);
    }

}
