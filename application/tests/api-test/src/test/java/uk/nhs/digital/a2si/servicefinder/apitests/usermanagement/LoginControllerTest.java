package uk.nhs.digital.a2si.servicefinder.apitests.usermanagement;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static uk.nhs.digital.a2si.servicefinder.apitests.ApiTestsHelper.USER_MANAGEMENT_HOST;
import static uk.nhs.digital.a2si.servicefinder.apitests.ApiTestsHelper.NEW_USER_PASSWORD;
import static uk.nhs.digital.a2si.servicefinder.apitests.ApiTestsHelper.USER_MANAGEMENT_PORT;
import static uk.nhs.digital.a2si.servicefinder.apitests.ApiTestsHelper.VALID_ORIGIN;
import static uk.nhs.digital.a2si.servicefinder.apitests.ApiTestsHelper.generateTestEmailAddress;
import static uk.nhs.digital.a2si.servicefinder.apitests.ApiTestsHelper.registerTestUser;
import static uk.nhs.digital.a2si.servicefinder.apitests.ApiTestsHelper.requestAccount;
import static uk.nhs.digital.a2si.servicefinder.apitests.ApiTestsHelper.registerTestUserUnapproved;

public class LoginControllerTest {

    private static final String LOGIN_API_PATH = "/api/login";

    @Before
    @SuppressFBWarnings
    public void setUp() {
        RestAssured.baseURI = USER_MANAGEMENT_HOST;
        RestAssured.port = USER_MANAGEMENT_PORT;
        RestAssured.useRelaxedHTTPSValidation();
    }

    @Test
    public void canLoginWithCorrectCredentials200() throws Exception {
        // Given
        RequestSpecification httpRequest = RestAssured.given();
        String testEmailAddress = generateTestEmailAddress("login");
        registerTestUser(testEmailAddress);

        // When
        Response response = httpRequest
            .header("Origin", VALID_ORIGIN)
            .header("Content-Type", "application/json")
            .body(Map.of("emailAddress", testEmailAddress, "password", NEW_USER_PASSWORD))
            .post(LOGIN_API_PATH);

        // Then
        assertThat(response.getBody().asString(), response.getStatusCode(), is(HttpStatus.SC_OK));
        assertThat(response.getCookie("ACCESS_TOKEN"), not(emptyString()));
        assertThat(response.getCookie("REFRESH_TOKEN"), not(emptyString()));
        ResponseBody body = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(body.asString());
        assertThat(jsonResponse, notNullValue());
        assertThat(jsonResponse.size(), not(0));
        assertThat(jsonResponse.get("emailAddress").asText(), is(testEmailAddress));
    }

    @Test
    public void loginWhenIncompleteRegistrationReturns400() throws Exception {
        // Given
        RequestSpecification httpRequest = RestAssured.given();
        String testEmailAddress = generateTestEmailAddress("login");
        requestAccount(testEmailAddress);

        // When
        Response response = httpRequest
            .header("Origin", VALID_ORIGIN)
            .header("Content-Type", "application/json")
            .body(Map.of("emailAddress", testEmailAddress, "password", NEW_USER_PASSWORD))
            .post(LOGIN_API_PATH);

        // Then
        assertThat(response.getBody().asString(), response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        ResponseBody body = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(body.asString());
        assertThat(jsonResponse, notNullValue());
        assertThat(jsonResponse.size(), not(0));
        assertThat(jsonResponse.get("status").asText(), is("BAD_REQUEST"));
        assertThat(jsonResponse.get("message").asText(), is("Incomplete registration"));
    }

    @Test
    public void loginWhenCompleteRegistrationButNotApprovedReturns400() throws Exception {
        // Given
        RequestSpecification httpRequest = RestAssured.given();
        String testEmailAddress = String.format("%s_%d_%s", "test", System.currentTimeMillis(), "@test.com");
        registerTestUserUnapproved(testEmailAddress);

        // When
        Response response = httpRequest
            .header("Origin", VALID_ORIGIN)
            .header("Content-Type", "application/json")
            .body(Map.of("emailAddress", testEmailAddress, "password", NEW_USER_PASSWORD))
            .post(LOGIN_API_PATH);

        // Then
        assertThat(response.getBody().asString(), response.getStatusCode(), is(HttpStatus.SC_BAD_REQUEST));
        ResponseBody body = response.getBody();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(body.asString());
        assertThat(jsonResponse, notNullValue());
        assertThat(jsonResponse.size(), not(0));
        assertThat(jsonResponse.get("status").asText(), is("BAD_REQUEST"));
        assertThat(jsonResponse.get("message").asText(), is("User account is not approved"));
    }

}
