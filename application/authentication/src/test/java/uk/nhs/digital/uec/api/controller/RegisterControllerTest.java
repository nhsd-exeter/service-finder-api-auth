package uk.nhs.digital.uec.api.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.nhs.digital.uec.api.adapter.UserAdapter;
import uk.nhs.digital.uec.api.adapter.UserRegistrationAdapter;
import uk.nhs.digital.uec.api.exception.InvalidRegistrationDetailsException;
import uk.nhs.digital.uec.api.model.ApprovalStatus;
import uk.nhs.digital.uec.api.model.EmailVerification;
import uk.nhs.digital.uec.api.model.RegistrationCompleteAccount;
import uk.nhs.digital.uec.api.model.RegistrationRequestAccount;
import uk.nhs.digital.uec.api.model.UnconfirmedUserVerification;
import uk.nhs.digital.uec.api.service.RegistrationService;
import uk.nhs.digital.uec.api.service.UserService;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.nhs.digital.uec.api.testsupport.UserRegistrationTestFactory.atestUserRegistrationRequestAccount;
import static uk.nhs.digital.uec.api.testsupport.UserRegistrationTestFactory.atestUserRegistrationCompleteAccount;

/**
 * Test for {@link RegisterController}
 */
@RunWith(MockitoJUnitRunner.class)
public class RegisterControllerTest {

    private static final String VALIDATION_ERROR_MESSAGE = "There are validation errors";

    private static final String EMAIL_ADDRESS = "bill2@example.com";

    private static final String VERIFICATION_CODE = "123456";

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private UserRegistrationAdapter adapter;

    @Mock
    private UserService userService;

    @Mock
    private RegistrationService registrationService;

    @InjectMocks
    private RegisterController registerController;

    private MockMvc mockMvc;

    private RegistrationRequestAccount registrationRequestAccount;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).setControllerAdvice(new UserManagementControllerAdvice()).build();
        registrationRequestAccount = atestUserRegistrationRequestAccount();
    }

    @Test
    public void requestRegistrationRequestSuccess() throws Exception {

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/requestAccount")
                .contentType("application/json")
                .content(asJsonString(registrationRequestAccount)));

        verify(registrationService).requestAccount(registrationRequestAccount);
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void requestRegistrationRequestNoEmail() throws Exception {

        registrationRequestAccount.setEmailAddress(null);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/requestAccount")
                .contentType("application/json")
                .content(asJsonString(registrationRequestAccount)));

        // Then
        verify(registrationService, never()).requestAccount(registrationRequestAccount);
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void requestRegistrationRequestInvalidEmail() throws Exception {

        registrationRequestAccount.setEmailAddress("invalid");

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/requestAccount")
                .contentType("application/json")
                .content(asJsonString(registrationRequestAccount)));

        // Then
        verify(registrationService, never()).requestAccount(registrationRequestAccount);
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void requestRegistrationRequestNoPassword() throws Exception {

        registrationRequestAccount.setPassword(null);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/requestAccount")
                .contentType("application/json")
                .content(asJsonString(registrationRequestAccount)));

        // Then
        verify(registrationService, never()).requestAccount(registrationRequestAccount);
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void requestRegistrationRequestPasswordTooShort() throws Exception {

        registrationRequestAccount.setPassword("1234567");

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/requestAccount")
                .contentType("application/json")
                .content(asJsonString(registrationRequestAccount)));

        // Then
        verify(registrationService, never()).requestAccount(registrationRequestAccount);
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void requestRegistrationAlreadyRegistered() throws Exception {

        registrationRequestAccount.setEmailAddress("already_registered@dummy.com");

        List<String> validationMessages = new ArrayList<>();
        validationMessages.add("That email address is already registered.");

        doThrow(new InvalidRegistrationDetailsException("There are validation errors", validationMessages))
            .when(registrationService).requestAccount(registrationRequestAccount);

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/requestAccount")
                .contentType("application/json")
                .content(asJsonString(registrationRequestAccount)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void verifyAccount() throws Exception
    {
        EmailVerification emailVerification =
            new EmailVerification(EMAIL_ADDRESS, VERIFICATION_CODE);

        ApprovalStatus approvalStatus = new ApprovalStatus(true);

        when(registrationService.verifyAccount(emailVerification)).thenReturn(approvalStatus);

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/verifyAccount")
                .contentType("application/json")
                .content(asJsonString(emailVerification)));

        resultActions.andExpect(status().isOk());
    }






    @Test
    public void requestRegistrationCompleteSuccess() throws Exception {

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(atestUserRegistrationCompleteAccount())));

        // Then
        verify(registrationService).completeRegistration(atestUserRegistrationCompleteAccount());
        resultActions.andExpect(status().isOk());
    }

    @Test
    public void requestRegistrationCompleteError() throws Exception {

        RegistrationCompleteAccount regComplAcc = new RegistrationCompleteAccount();

        List<String> validationMessages = new ArrayList<>();
        validationMessages.add("Problem with complete reg.");

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestGivenBlankEmail() throws Exception {
        // Given
        String validationMessage = "email address must not be blank";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setEmailAddress("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenInvalidEmail() throws Exception {
        // Given
        String validationMessage = "email address must be valid";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setEmailAddress("1234");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenBlankName() throws Exception {
        // Given
        String validationMessage = "name must not be blank";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setName("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenBlankJobTitle() throws Exception {
        // Given
        String validationMessage = "job title must not be blank";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setJobTitle("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenBlankJobRoleType() throws Exception {
        // Given
        String validationMessage = "job role type must not be blank";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setJobRoleType("");
        regComplAcc.setJobRoleTypeOtherDetails("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenBlankOrgName() throws Exception {
        // Given
        String validationMessage = "organisation name must not be blank";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setOrganisationName("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenBlankOrgType() throws Exception {
        // Given
        String validationMessage = "organisation type must not be blank";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setOrganisationType("");
        regComplAcc.setOrganisationTypeOtherDetails("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenBlankWorkplacePostcode() throws Exception {
        // Given
        String validationMessage = "workplacePostcode must not be blank";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setWorkplacePostcode("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenBlankRegion() throws Exception {
        // Given
        String validationMessage = "region must not be empty";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setRegion("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenFalseTermsAndConds() throws Exception {
        // Given
        String validationMessage = "acceptedTermsAndConditions must be true";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setAcceptedTermsAndConditions(false);

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenJobRoleOtherJobTypeOther() throws Exception {
        // Given
        String validationMessage = "jobRoleTypeOther must not be set unless jobRoleType is OTHER";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setJobRoleType("DOCTOR");
        regComplAcc.setJobRoleTypeOtherDetails("Hello");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenJobRoleOtherNullJobTypeOther() throws Exception {
        // Given
        String validationMessage = "jobRoleTypeOther must be set when jobRoleType is OTHER";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setJobRoleType("OTHER");
        regComplAcc.setJobRoleTypeOtherDetails(null);

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenOrgRoleOtherOrgTypeOther() throws Exception {
        // Given
        String validationMessage = "organisationTypeOther must not be set unless organisationType is OTHER";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setOrganisationType("ACUTE");
        regComplAcc.setOrganisationTypeOtherDetails("Hello");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldReturnBadRequestGivenOrgRoleOtherNullOrgTypeOther() throws Exception {
        // Given
        String validationMessage = "organisationTypeOther must be set when organisationType is OTHER";

        RegistrationCompleteAccount regComplAcc = atestUserRegistrationCompleteAccount();
        regComplAcc.setOrganisationType("OTHER");
        regComplAcc.setOrganisationTypeOtherDetails("");

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/completeRegistration")
                .contentType("application/json")
                .content(asJsonString(regComplAcc)));

        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{'status':'BAD_REQUEST', 'message': '" + VALIDATION_ERROR_MESSAGE + "', 'errors': ['" + validationMessage + "']}"));
    }

    @Test
    public void shouldResendConfirmationLink() throws Exception {
        // Given
        UnconfirmedUserVerification unconfirmedUserVerification = new UnconfirmedUserVerification(EMAIL_ADDRESS);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/resendLink")
                .contentType("application/json")
                .content(asJsonString(unconfirmedUserVerification)));

        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(content().json("{'approved':false}"));
        then(userService)
            .should()
            .resendConfirmationMessage(unconfirmedUserVerification.getEmailAddress());
    }

    @Test
    public void shouldResendConfirmationLinkWithApprovedEmail() throws Exception {
        // Given
        UnconfirmedUserVerification unconfirmedUserVerification =
            new UnconfirmedUserVerification("nhsTestExample@nhs.net");

        // When
        when(userService.resendConfirmationMessage(unconfirmedUserVerification.getEmailAddress()))
            .thenReturn(true);

        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/resendLink")
                .contentType("application/json")
                .content(asJsonString(unconfirmedUserVerification)));


        // Then
        resultActions.andExpect(status().isOk());
        resultActions.andExpect(content().json("{'approved':true}"));
        then(userService)
            .should()
            .resendConfirmationMessage(unconfirmedUserVerification.getEmailAddress());
    }

    @Test
    public void shouldFailToResendLinkGivenEmptyEmailAddress() throws Exception {
        // Given
        String emailAddress = "";
        UnconfirmedUserVerification unconfirmedUserVerification = new UnconfirmedUserVerification(emailAddress);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/resendLink")
                .contentType("application/json")
                .content(asJsonString(unconfirmedUserVerification)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailToResendLinkGivenInvalidEmailAddress() throws Exception {
        // Given
        String emailAddress = "test.example";
        UnconfirmedUserVerification unconfirmedUserVerification = new UnconfirmedUserVerification(emailAddress);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post("/api/register/resendLink")
                .contentType("application/json")
                .content(asJsonString(unconfirmedUserVerification)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
