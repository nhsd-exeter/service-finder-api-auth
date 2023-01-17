package uk.nhs.digital.uec.api.exception;

/**
 * Exception thrown when the approval status change attempted is not permitted.
 *
 */
public class InvalidApprovalStatusChangeException extends RuntimeException {

    public InvalidApprovalStatusChangeException(String message) {
        super(message);
    }
}
