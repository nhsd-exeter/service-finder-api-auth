package uk.nhs.digital.a2si.servicefinder.apitests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;

public class ApiTestsHelper {


  public static final String TEST_USER_NAME = "SF Test Account";
  public static final String NEW_USER_PASSWORD = "new_password";
  public static final String SUPER_USER_EMAIL_ADDRESS = "super@nhs.net";
  public static final String SUPER_USER_PASSWORD = "password";
  public static final String SEARCH_EMAIL_ADDRESS = "search@nhs.net";
  public static final String TEST_USER_CONFIRMATION_CODE_INVALID = "654321";
  public static final String TEST_USER_CONFIRMATION_CODE_EXPIRED = "000000";
  public static final String TEST_USER_CONFIRMATION_CODE = "123456";
  public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
  public static final String ACCOUNT_ALREADY_EXISTS_IN_COGNITO = "account_in_cognito@nhs.net";
  private static final String REQUEST_ACCOUNT_PATH = "/api/register/requestAccount";
  private static final String VERIFY_ACCOUNT_PATH = "/api/register/verifyAccount";
  private static final String COMPLETE_REG_PATH = "/api/register/completeRegistration";

  public static Response retrieveSuperUserLoginResponse() {
    return retrieveLoginResponse("super");
  }

  public static Response retrieveAdminLoginResponse() {
    return retrieveLoginResponse("admin");
  }

  public static Response retrieveApproverLoginResponse() {
    return retrieveLoginResponse("approver");
  }

  public static Response retrieveReporterLoginResponse() {
    return retrieveLoginResponse("reporter");
  }

  public static Response retrieveSearchLoginResponse() {
    return retrieveLoginResponse("search");
  }

  public static Response retrieveUnconfirmedLoginResponse() {
    return retrieveLoginResponse("unconfirmed");
  }

  private static Response retrieveLoginResponse(String username) {
    RequestSpecification httpRequest = getRequestSpecification();
    String urlSuffix = "/api/login";
    Map<String, String> loginParameters = new HashMap<>();
    loginParameters.put("emailAddress", username + "@nhs.net");
    loginParameters.put("password", "password");

    return httpRequest
        .header("Origin", VALID_ORIGIN)
        .header("Content-Type", "application/json")
        .body(loginParameters)
        .post(urlSuffix);
  }

  public static void registerTestUsers(int numberOfUsers, String prefix) {
    for (int counter = 0; counter < numberOfUsers; counter++) {
      registerTestUser(generateTestEmailAddress(prefix));
    }
  }

  public static void requestAccounts(int numberOfUsers, String prefix) {
    for (int counter = 0; counter < numberOfUsers; counter++) {
      requestAccount(generateTestEmailAddress(prefix));
    }
  }

  public static String generateTestEmailAddress(String prefix) {
    return String.format("%s_%d_%s", prefix, System.currentTimeMillis(), SUPER_USER_EMAIL_ADDRESS);
  }

  public static void requestAccount(String emailAddress) {
    final Map<String, Object> requestAccountParameters = new HashMap<>();
    requestAccountParameters.put("emailAddress", emailAddress);
    requestAccountParameters.put("password", NEW_USER_PASSWORD);

    // When
    RequestSpecification httpRequest = getRequestSpecification();
    Response response =
        httpRequest
            .header("Origin", VALID_ORIGIN)
            .header("Content-Type", "application/json")
            .body(requestAccountParameters)
            .post(REQUEST_ACCOUNT_PATH);

    // Then
    assertThat(response.getBody().asString(), response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  public static void verifyAccount(String emailAddress) {
    Map<String, Object> verifyAccountParameters = new HashMap<>();
    verifyAccountParameters.put("emailAddress", emailAddress);
    verifyAccountParameters.put("code", "123456");

    // When
    RequestSpecification httpRequest = getRequestSpecification();
    Response response =
        httpRequest
            .header("Origin", VALID_ORIGIN)
            .header("Content-Type", "application/json")
            .body(verifyAccountParameters)
            .post(VERIFY_ACCOUNT_PATH);

    // Then
    assertThat(response.getBody().asString(), response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  public static void completeAccountRegistration(String emailAddress) {
    Map<String, Object> registerParameters = new HashMap<>();
    registerParameters.put("emailAddress", emailAddress);
    registerParameters.put("name", "Name");
    registerParameters.put("jobTitle", "ee");
    registerParameters.put("jobRoleType", "OCCUPATIONAL_THERAPIST");
    registerParameters.put("organisationName", "eee");
    registerParameters.put("organisationType", "GP_SURGERY");
    registerParameters.put("contactTelephoneNumber", "123");
    registerParameters.put("workplacePostcode", "BS1 1AS");
    registerParameters.put("region", "MIDLANDS");
    registerParameters.put("acceptedTermsAndConditions", "true");
    registerParameters.put("approved", "true");

    // When
    RequestSpecification httpRequest = getRequestSpecification();
    Response response =
        httpRequest
            .header("Origin", VALID_ORIGIN)
            .header("Content-Type", "application/json")
            .body(registerParameters)
            .post(COMPLETE_REG_PATH);

    // Then
    assertThat(response.getBody().asString(), response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  public static void completeAccountRegistrationUnapproved(String emailAddress) {
    Map<String, Object> registerParameters = new HashMap<>();
    registerParameters.put("emailAddress", emailAddress);
    registerParameters.put("name", "Name");
    registerParameters.put("jobTitle", "ee");
    registerParameters.put("jobRoleType", "OCCUPATIONAL_THERAPIST");
    registerParameters.put("organisationName", "eee");
    registerParameters.put("organisationType", "GP_SURGERY");
    registerParameters.put("contactTelephoneNumber", "123");
    registerParameters.put("workplacePostcode", "BS1 1AS");
    registerParameters.put("region", "MIDLANDS");
    registerParameters.put("acceptedTermsAndConditions", "true");
    registerParameters.put("approved", "false");

    // When
    RequestSpecification httpRequest = getRequestSpecification();
    Response response =
        httpRequest
            .header("Origin", VALID_ORIGIN)
            .header("Content-Type", "application/json")
            .body(registerParameters)
            .post(COMPLETE_REG_PATH);

    // Then
    assertThat(response.getBody().asString(), response.getStatusCode(), is(HttpStatus.SC_OK));
  }

  public static void registerTestUser(String emailAddress) {
    ApiTestsHelper.requestAccount(emailAddress);
    ApiTestsHelper.verifyAccount(emailAddress);
    ApiTestsHelper.completeAccountRegistration(emailAddress);
  }

  public static void registerTestUserUnapproved(String emailAddress) {
    ApiTestsHelper.requestAccount(emailAddress);
    ApiTestsHelper.verifyAccount(emailAddress);
    ApiTestsHelper.completeAccountRegistrationUnapproved(emailAddress);
  }

  private static RequestSpecification getRequestSpecification() {
    RestAssured.baseURI = USER_MANAGEMENT_HOST;
    RestAssured.port = USER_MANAGEMENT_PORT;
    RestAssured.useRelaxedHTTPSValidation();
    return RestAssured.given();
  }

  private static RequestSpecification getSFSRequestSpecification() {
    RestAssured.baseURI = SFS_HOST;
    RestAssured.port = SFS_PORT;
    RestAssured.useRelaxedHTTPSValidation();
    return RestAssured.given();
  }

  public static void createDosSearchEvent(
      String discriminatorTypeName, String discriminatorTypeId) {

    // When:
    String urlSuffix = "/api/report/events";
    RequestSpecification httpRequest = getSFSRequestSpecification();
    Response response =
        httpRequest
            .header("Origin", VALID_ORIGIN)
            .cookie(retrieveSuperUserLoginResponse().getDetailedCookies().get(ACCESS_TOKEN))
            .header("Content-Type", "application/json")
            .body(
                "{"
                    + "     \"eventType\":\"SEARCH\","
                    + "     \"user\":{"
                    + "         \"jobType\":\"Occupational Therapist\","
                    + "         \"jobTypeOther\":null,"
                    + "         \"organisationType\":\"GP Surgery\","
                    + "         \"organisationTypeOther\":null,"
                    + "         \"organisationPostcodeSector\":\"EX1\","
                    + "         \"regions\":["
                    + "             \"NHS South West Region\""
                    + "         ],"
                    + "         \"userName\":\"Bill Test\","
                    + "         \"userEmail\":\"admin@nhs.net\""
                    + "     },"
                    + "     \"data\":{"
                    + "         \"searchType\":\"serviceType\","
                    + "         \"searchPostcodeSector\":\"EX2\","
                    + "         \"dosSearchIds\":["
                    + "             \""
                    + discriminatorTypeId
                    + "\""
                    + "         ],"
                    + "         \"choicesIds\":[],"
                    + "         \"discriminatorNames\":["
                    + "             \""
                    + discriminatorTypeName
                    + "\""
                    + "         ],"
                    + "         \"searchResultsCount\":1,"
                    + "         \"nearestResultDistance\":1,"
                    + "         \"dosError\":\"\","
                    + "         \"searchUrl\":\"https:\\\\this.is.the.search.url\\\\EX12_2FD\""
                    + "    }"
                    + "}")
            .post(urlSuffix);

    // Then:
    String body = response.getBody().asString();
    assertThat(body, response.getStatusCode(), is(HttpStatus.SC_CREATED));
  }

  public static void createChoicesSearchEvent(
      String discriminatorTypeName, String discriminatorTypeId) {

    // When:
    String urlSuffix = "/api/report/events";
    RequestSpecification httpRequest = getSFSRequestSpecification();
    Response response =
        httpRequest
            .header("Origin", VALID_ORIGIN)
            .cookie(retrieveSuperUserLoginResponse().getDetailedCookies().get(ACCESS_TOKEN))
            .header("Content-Type", "application/json")
            .body(
                "{"
                    + "     \"eventType\":\"SEARCH\","
                    + "     \"user\":{"
                    + "         \"jobType\":\"Occupational Therapist\","
                    + "         \"jobTypeOther\":null,"
                    + "         \"organisationType\":\"GP Surgery\","
                    + "         \"organisationTypeOther\":null,"
                    + "         \"organisationPostcodeSector\":\"EX1\","
                    + "         \"regions\":["
                    + "             \"NHS South West Region\""
                    + "         ],"
                    + "         \"userName\":\"Bill Test\","
                    + "         \"userEmail\":\"admin@nhs.net\""
                    + "     },"
                    + "     \"data\":{"
                    + "         \"searchType\":\"serviceType\","
                    + "         \"searchPostcodeSector\":\"EX2\","
                    + "         \"dosSearchIds\":[],"
                    + "         \"choicesIds\":["
                    + "             \""
                    + discriminatorTypeId
                    + "\""
                    + "         ],"
                    + "         \"discriminatorNames\":["
                    + "             \""
                    + discriminatorTypeName
                    + "\""
                    + "         ],"
                    + "         \"searchResultsCount\":1,"
                    + "         \"dosError\":\"\","
                    + "         \"searchUrl\":\"https:\\\\this.is.the.search.url\\EX12_2FD\""
                    + "    }"
                    + "}")
            .post(urlSuffix);

    // Then:
    String body = response.getBody().asString();
    assertThat(body, response.getStatusCode(), is(HttpStatus.SC_CREATED));
  }

  public static Response createQuickFeedbackRecord(String urlSuffix) throws Exception {
    RequestSpecification httpRequest = getSFSRequestSpecification();

    String json =
        "{\r\n"
            + "   \"eventType\":\"FEEDBACK\",\r\n"
            + "   \"user\":{\r\n"
            + "      \"regions\":[\r\n"
            + "         \"London\",\r\n"
            + "         \"Wales\"\r\n"
            + "      ],\r\n"
            + "      \"jobType\":\"Administrator\",\r\n"
            + "      \"userName\":\"Test\",\r\n"
            + "      \"userEmail\":\"test@nhs.net\",\r\n"
            + "      \"organisationType\":\"NHS Digital\",\r\n"
            + "      \"organisationPostcodeSector\":\"EX2\"\r\n"
            + "   },\r\n"
            + "   \"data\":{\r\n"
            + "      \"feedback\":\"BAD\",\r\n"
            + "      \"additionalDetails\":\"some details were missing\",\r\n"
            + "      \"serviceId\":\"1234567890\",\r\n"
            + "      \"serviceSource\":\"DIRECTORY_OF_SERVICES\",\r\n"
            + "      \"serviceName\":\"Test Service\",\r\n"
            + "      \"servicePostcode\":\"EX2 5SE\",\r\n"
            + "      \"serviceCCG\":\"South West\",\r\n"
            + "      \"serviceRegion\":\"Southwest\"\r\n"
            + "   }\r\n"
            + "}";
    return httpRequest
        .header("Origin", SFS_ORIGIN)
        .cookie(retrieveSuperUserLoginResponse().getDetailedCookies().get(ACCESS_TOKEN))
        .header("Content-Type", "application/json")
        .body(json)
        .post(urlSuffix);
  }

  public static Response createUpdateQuickFeedbackRecord(String urlSuffix, String eventId)
      throws Exception {
    RequestSpecification httpRequest = getSFSRequestSpecification();
    String json =
        "{\r\n"
            + "    \"data\": {\r\n"
            + "        \"feedback\": \"BAD\",\r\n"
            + "        \"additionalDetails\": \"Some details missing - modified\",\r\n"
            + "        \"eventId\":  \""
            + eventId
            + "\"}\r\n}";
    return httpRequest
        .header("Origin", SFS_ORIGIN)
        .cookie(retrieveSuperUserLoginResponse().getDetailedCookies().get(ACCESS_TOKEN))
        .header("Content-Type", "application/json")
        .body(json)
        .patch(urlSuffix);
  }
}
