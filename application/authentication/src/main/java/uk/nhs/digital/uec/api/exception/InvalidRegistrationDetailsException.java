package uk.nhs.digital.uec.api.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RuntimeException} thrown when an sign up details are invalid when calling the Cognito API.
 */
@Getter
public class InvalidRegistrationDetailsException extends UserManagementException {

    private List<String> errors = new ArrayList<>();

    public InvalidRegistrationDetailsException(String message) {
        super(message);
    }

    public InvalidRegistrationDetailsException(String message, List<String> errors) {
        this(message);
        this.errors = errors;
    }

}
