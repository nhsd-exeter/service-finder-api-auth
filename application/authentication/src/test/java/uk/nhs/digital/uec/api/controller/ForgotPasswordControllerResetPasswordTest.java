package uk.nhs.digital.uec.api.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.amazonaws.services.cognitoidp.model.ExpiredCodeException;

import uk.nhs.digital.uec.api.adapter.UserAdapter;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.exception.InvalidCodeException;
import uk.nhs.digital.uec.api.model.ResetPasswordModel;
import uk.nhs.digital.uec.api.service.LoginAttemptService;
import uk.nhs.digital.uec.api.service.UserService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.nhs.digital.uec.api.testsupport.JsonUtil.asJsonString;

/**
 * Test for {@link ForgotPasswordController}
 */
public class ForgotPasswordControllerResetPasswordTest {

    private static final String EMAIL_ADDRESS = "test@example.com";

    private static final String CODE = "123456";

    private static final String PASSWORD = "abcdefgh";

    private static final String INVALID_EMAIL_ADDRESS = "test.example.com";

    private static final String RESET_PASSWORD_ENDPOINT = "/api/forgotPassword/reset";

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String ERROR_MESSAGE = "error_message";

    @Mock
    private UserService userService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private User user;

    @InjectMocks
    private ForgotPasswordController forgotPasswordController;

    private MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(forgotPasswordController).setControllerAdvice(new UserManagementControllerAdvice()).build();
    }

    @Test
    public void shouldPassOnValidRequest() throws Exception {
        // Given
        given(userAdapter.toUser(anyString())).willReturn(Optional.of(user));
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(EMAIL_ADDRESS, CODE, PASSWORD);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));

        // Then
        resultActions.andExpect(status().isOk());
        then(userService)
            .should()
            .resetPassword(resetPasswordModel, user);
        then(loginAttemptService)
            .should()
            .remove(EMAIL_ADDRESS);
    }

    @Test
    public void shouldReturnBadRequestGivenNullEmailAddress() throws Exception {
        // Given
        given(userAdapter.toUser(anyString())).willReturn(Optional.of(user));
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(null, CODE, PASSWORD);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
        verify(loginAttemptService, times(0)).remove(EMAIL_ADDRESS);
    }

    @Test
    public void shouldReturnBadRequestGivenInvalidEmailAddress() throws Exception {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(INVALID_EMAIL_ADDRESS, CODE, PASSWORD);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestGivenNullCode() throws Exception {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(EMAIL_ADDRESS, null, PASSWORD);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestGivenInvalidCode() throws Exception {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(EMAIL_ADDRESS, "", PASSWORD);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestGivenNullPassword() throws Exception {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(EMAIL_ADDRESS, CODE, null);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestGivenInvalidPassword() throws Exception {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(EMAIL_ADDRESS, CODE, "x");

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnBadRequestGivenInvalidCodeException() throws Exception {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(EMAIL_ADDRESS, CODE, PASSWORD);
        given(userAdapter.toUser(anyString())).willReturn(Optional.of(user));
        willThrow(new InvalidCodeException(ERROR_MESSAGE))
            .given(userService)
            .resetPassword(resetPasswordModel, user);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));

        // Then
        then(userService)
            .should()
            .resetPassword(resetPasswordModel, user);
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{\"status\":\"BAD_REQUEST\",\"message\":\"Invalid code\",\"errors\":[]}"));
    }

    @Test
    public void shouldReturnBadRequestGivenExpiredCodeException() throws Exception {
        // Given
        ResetPasswordModel resetPasswordModel = new ResetPasswordModel(EMAIL_ADDRESS, CODE, PASSWORD);
        given(userAdapter.toUser(anyString())).willReturn(Optional.of(user));
        willThrow(new ExpiredCodeException(ERROR_MESSAGE))
            .given(userService)
            .resetPassword(resetPasswordModel, user);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(RESET_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(resetPasswordModel)));
        // Then
        then(userService)
            .should()
            .resetPassword(resetPasswordModel, user);
        resultActions.andExpect(status().isBadRequest());
        resultActions.andExpect(content().json("{\"status\":\"BAD_REQUEST\",\"message\":\"Code expired\",\"errors\":[]}"));
    }

}
