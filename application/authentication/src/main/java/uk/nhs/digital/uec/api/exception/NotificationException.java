package uk.nhs.digital.uec.api.exception;

public class NotificationException extends RuntimeException {

    public NotificationException(String message, Throwable e) {
        super(message, e);
    }

}
