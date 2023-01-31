package uk.nhs.digital.uec.api.exception;

public class CsvDownloadException extends RuntimeException {

  private static final long serialVersionUID = -5184520268129959026L;

  public CsvDownloadException(Exception e) {
    super("Unable to download csv", e);
  }
}
