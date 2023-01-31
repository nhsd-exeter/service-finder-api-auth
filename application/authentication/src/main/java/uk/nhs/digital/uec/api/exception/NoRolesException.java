package uk.nhs.digital.uec.api.exception;

/**
 * {@link Exception} thrown when the user is detected to have no roles at login.
 */
public class NoRolesException extends Exception {

  public NoRolesException() {
  }

  public NoRolesException(String message) {
    super(message);
  }
}
