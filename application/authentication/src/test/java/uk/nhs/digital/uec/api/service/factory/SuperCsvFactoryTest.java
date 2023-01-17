package uk.nhs.digital.uec.api.service.factory;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import uk.nhs.digital.uec.api.domain.UserDownload;

/** Test for {@link UserCsvFactory} */
public class SuperCsvFactoryTest {

  private static final String CSV_OUTPUT = "Name,User state,Email address,Email verification status,Status,Last"
      + " Login,Region,Organisation type,Job type,Date/time created,Numeric user ID,Identity"
      + " provider ID,Date/time last updated,Date/time terms and conditions last"
      + " accepted,Rejection reason,Roles,Job title,Organisation name,Workplace"
      + " postcode,Telephone number\r\n"
      + "test name,active,email@test1.com,Verified,PENDING,20/05/2021 15:45,Test"
      + " Region,Administrator,Administrator,20/05/2021 15:45,0,ABC,20/05/2021 15:45,20/05/2021"
      + " 15:45,rejection_reason,\"Administrator,Search\",job_name,org_name,bs23,01234567890\r\n"
      + "test name,active,email@test2.com,Unverified,PENDING,20/05/2021 15:45,Test"
      + " Region,Administrator,Administrator,20/05/2021 15:45,0,ABC,20/05/2021 15:45,20/05/2021"
      + " 15:45,rejection_reason,\"Administrator,Search\",job_name,org_name,bs23,01234567890\r\n";

  @Test
  public void createCsv() throws IOException, ParseException {

    List<UserDownload> users = this.createTestUsers();
    SuperCsvFactory superCsvFactory = new SuperCsvFactory();

    String filename = "test.csv";

    superCsvFactory.writeCsv(users, csvHeaders, csvElements, getProcessors(), filename);

    byte[] csv = superCsvFactory.readCsv(filename);
    superCsvFactory.deleteCsv(filename);
    String csvString = new String(csv);

    assertThat(csvString, is(CSV_OUTPUT));
  }

  private List<UserDownload> createTestUsers() throws ParseException {

    List<UserDownload> users = new ArrayList<>();

    users.add(createTestUser("email@test1.com", true));
    users.add(createTestUser("email@test2.com", false));

    return users;
  }

  private UserDownload createTestUser(String emailAddress, Boolean emailAddressVerified)
      throws ParseException {
    return new UserDownload(
        "test name",
        "active",
        emailAddress,
        emailAddressVerified,
        "PENDING",
        new SimpleDateFormat("yyyyMMdd:hhmmss").parse("20210520:154532"),
        "Test Region",
        "Administrator",
        "Administrator",
        new SimpleDateFormat("yyyyMMdd:hhmmss").parse("20210520:154532"),
        0L,
        "ABC",
        new SimpleDateFormat("yyyyMMdd:hhmmss").parse("20210520:154532"),
        new SimpleDateFormat("yyyyMMdd:hhmmss").parse("20210520:154532"),
        "rejection_reason",
        "Administrator,Search",
        "job_name",
        "org_name",
        "bs23",
        "01234567890");
  }

  private static final String[] csvHeaders = new String[] {
      "Name",
      "User state",
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
  };

  private static final String[] csvElements = new String[] {
      "name",
      "userState",
      "emailAddress",
      "emailAddressVerified",
      "approvalStatus",
      "lastLoggedIn",
      "region",
      "orgType",
      "jobType",
      "created",
      "numericUserId",
      "identityProviderId",
      "updated",
      "termsAndConditionsAccepted",
      "rejectionReason",
      "roles",
      "jobName",
      "organisationName",
      "postcode",
      "telephoneNumber"
  };

  /**
   * Sets up the processors used for the examples. There are 10 CSV columns, so 10
   * processors are
   * defined. All values are converted to Strings before writing (there's no need
   * to convert them),
   * and null values will be written as empty columns (no need to convert them to
   * "").
   *
   * @return the cell processors
   */
  private static CellProcessor[] getProcessors() {

    final CellProcessor[] processors = new CellProcessor[] {
        null, // name
        null, // user state
        null, // email
        new FmtBool("Verified", "Unverified"), // emailAddressVerified
        null, // approvalStatus
        new Optional(new FmtDate("dd/MM/yyyy HH:mm")), // lastLoggedIn
        null, // region
        null, // orgType
        null, // jobType
        new Optional(new FmtDate("dd/MM/yyyy HH:mm")), // created
        null, // numericUserId
        null, // identityProviderId
        new Optional(new FmtDate("dd/MM/yyyy HH:mm")), // updated
        new Optional(new FmtDate("dd/MM/yyyy HH:mm")), // termsAndConditionsAccepted
        null, // rejectionReason
        null, // roles
        null, // jobName
        null, // organisationName
        null, // postcode
        null, // telephoneNumber
    };

    return processors;
  }
}
