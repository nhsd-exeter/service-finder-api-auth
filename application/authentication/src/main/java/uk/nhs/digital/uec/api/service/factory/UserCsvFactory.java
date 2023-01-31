package uk.nhs.digital.uec.api.service.factory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.exception.CsvDownloadException;

/**
 * Factory that creates a User details CSV list.
 */
public class UserCsvFactory {

  private static final String COMMA_DELIMITER = ",";

  private static final String COMMA_WHITESPACE_DELIMITER = ", ";

  private static final String TEMP_CSV_FILE_LOCATION = "csvFile.csv";

  /**
   * Creates a CSV string.
   *
   * @param users the list of user summaries used to retrieve the {@link User}
   * @return the CSV string
   * @throws IOException
   */
  public byte[] create(List<User> users) {
    try {
      this.createCsvFile(users);
      return this.getCsvFile();
    } catch (IOException e) {
      throw new CsvDownloadException(e);
    }
  }

  private void createCsvFile(List<User> users) throws IOException {
    if (users != null && users.size() > 0) {
      boolean autoFlush = true;
      FileWriter fileWriter = new FileWriter(TEMP_CSV_FILE_LOCATION);
      PrintWriter printWriter = new PrintWriter(fileWriter, autoFlush);
      this.writeCsvLineToFile(
          printWriter,
          getColumnHeadings().toArray(String[]::new)
        );
      this.printSearchResultsToCsv(printWriter, users);
      printWriter.close();
      fileWriter.close();
    }
  }

  private byte[] getCsvFile() throws IOException {
    byte[] csvFile = null;
    csvFile = Files.readAllBytes(Paths.get(TEMP_CSV_FILE_LOCATION));
    File fileToDelete = new File(TEMP_CSV_FILE_LOCATION);
    fileToDelete.delete();
    return csvFile;
  }

  private List<String> getColumnHeadings() {
    return new ArrayList<>(
      List.of(
        "Name",
        "Email address",
        "Email verification status",
        "Status",
        "Last Login",
        "Region",
        "Organisation type",
        "Job type",
        "Date/time created",
        "Numeric user ID",
        "Identity provider ID",
        "Date/time last updated",
        "Date/time terms and conditions last accepted",
        "Rejection reason",
        "Roles",
        "Job title",
        "Organisation name",
        "Workplace postcode",
        "Telephone number"
      )
    );
  }

  private void printSearchResultsToCsv(
    PrintWriter printWriter,
    List<User> users
  ) {
    for (User user : users) {
      this.getSearchEventLine(printWriter, user);
    }
  }

  private void writeCsvEntry(PrintWriter printWriter, String csvEntry) {
    writeCsvEntry(printWriter, csvEntry, false);
  }

  private void writeCsvEntry(
    PrintWriter printWriter,
    String csvEntry,
    boolean endOfLine
  ) {
    printWriter.print(this.escapeSpecialCharacters(csvEntry));
    if (endOfLine) {
      printWriter.print("\n");
    } else {
      printWriter.print(COMMA_DELIMITER);
    }
  }

  private void writeCsvLineToFile(PrintWriter printWriter, String[] csvLine) {
    printWriter.println(this.convertToCsv(csvLine));
  }

  private void getSearchEventLine(PrintWriter printWriter, User user) {
    String jobType = this.getJobType(user);
    String orgType = this.getOrgType(user);
    String region = user.getRegion() == null
      ? null
      : user.getRegion().getName();

    this.writeCsvEntry(printWriter, getUserValueString(user.getName()));
    this.writeCsvEntry(printWriter, getUserValueString(user.getEmailAddress()));
    this.writeCsvEntry(
        printWriter,
        getUserValueString(
          user.isEmailAddressVerified() ? "Verified" : "Unverified"
        )
      );
    this.writeCsvEntry(
        printWriter,
        getUserValueString(user.getApprovalStatus())
      );
    this.writeCsvEntry(
        printWriter,
        getUserDateTimeString(user.getLastLoggedIn())
      );
    this.writeCsvEntry(printWriter, getUserValueString(region));
    this.writeCsvEntry(printWriter, getUserValueString(orgType));
    this.writeCsvEntry(printWriter, getUserValueString(jobType));
    this.writeCsvEntry(printWriter, getUserDateTimeString(user.getCreated()));
    this.writeCsvEntry(printWriter, getUserValueString(user.getId()));
    this.writeCsvEntry(
        printWriter,
        getUserValueString(user.getIdentityProviderId())
      );
    this.writeCsvEntry(printWriter, getUserDateTimeString(user.getUpdated()));
    this.writeCsvEntry(
        printWriter,
        getUserDateTimeString(user.getTermsAndConditionsAccepted())
      );
    this.writeCsvEntry(
        printWriter,
        getUserValueString(user.getRejectionReason())
      );
    this.writeCsvEntry(
        printWriter,
        getUserValueListString(
          user
            .getRoles()
            .stream()
            .map(role -> role.getName())
            .collect(Collectors.toList()),
          COMMA_WHITESPACE_DELIMITER
        )
      );
    this.writeCsvEntry(printWriter, getUserValueString(user.getJobName()));
    this.writeCsvEntry(
        printWriter,
        getUserValueString(user.getOrganisationName())
      );
    this.writeCsvEntry(printWriter, getUserValueString(user.getPostcode()));
    this.writeCsvEntry(
        printWriter,
        getUserValueString(user.getTelephoneNumber()),
        true
      );
  }

  private String getJobType(User user) {
    if (user.getJobType() != null) {
      if (user.getJobType().getName().equalsIgnoreCase("OTHER")) {
        return user.getJobTypeOther();
      }
      return user.getJobType().getName();
    }
    return null;
  }

  private String getOrgType(User user) {
    if (user.getOrganisationType() != null) {
      if (user.getOrganisationType().getName().equalsIgnoreCase("OTHER")) {
        return user.getOrganisationTypeOther();
      }
      return user.getOrganisationType().getName();
    }
    return null;
  }

  private String getUserDateTimeString(Object submissionDateTimeObject) {
    String formattedDateTime = "";
    if (submissionDateTimeObject != null) {
      DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy HH:mm"
      );
      LocalDateTime submissionDateTime = LocalDateTime.parse(
        submissionDateTimeObject.toString()
      );
      formattedDateTime = submissionDateTime.format(dateTimeFormatter);
    }
    return formattedDateTime;
  }

  private String getUserValueListString(
    Object userValueList,
    String delimiter
  ) {
    if (userValueList != null) {
      return userValueList instanceof List
        ? ((List<?>) userValueList).stream()
          .map(Object::toString)
          .collect(Collectors.joining(delimiter))
        : "";
    }
    return "";
  }

  private String getUserValueString(Object userValue) {
    return userValue == null ? "" : userValue.toString();
  }

  private String convertToCsv(String[] csvLine) {
    return Stream
      .of(csvLine)
      .map(this::escapeSpecialCharacters)
      .collect(Collectors.joining(COMMA_DELIMITER));
  }

  private String escapeSpecialCharacters(String csvLineData) {
    String escapedData = csvLineData.replaceAll("\\R", " ");
    if (
      csvLineData.contains(COMMA_DELIMITER) ||
      csvLineData.contains("\"") ||
      csvLineData.contains("'")
    ) {
      csvLineData = csvLineData.replace("\"", "\"\"");
      escapedData = "\"" + csvLineData + "\"";
    }
    return escapedData;
  }
}
