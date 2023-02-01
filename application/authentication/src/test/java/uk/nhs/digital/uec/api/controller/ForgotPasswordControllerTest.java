package uk.nhs.digital.uec.api.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import uk.nhs.digital.uec.api.adapter.UserAdapter;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserDetails;
import uk.nhs.digital.uec.api.exception.UserIncompleteException;
import uk.nhs.digital.uec.api.exception.UserManagementException;
import uk.nhs.digital.uec.api.model.ForgotPasswordModel;
import uk.nhs.digital.uec.api.service.UserService;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.nhs.digital.uec.api.testsupport.JsonUtil.asJsonString;

/**
 * Test for {@link ForgotPasswordController}
 */
public class ForgotPasswordControllerTest {

    private static final String EMAIL_ADDRESS = "test@example.com";

    private static final String INVALID_EMAIL_ADDRESS = "test.example.com";

    private static final String FORGOT_PASSWORD_ENDPOINT = "/api/forgotPassword";

    private static final String JSON_CONTENT_TYPE = "application/json";

    private static final String ERROR_MESSAGE = "error_message";

    @Mock
    private UserService userService;

    @Mock
    private UserAdapter userAdapter;

    @Mock
    private User user;

    @Mock
    private UserDetails userDetails;

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
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel(EMAIL_ADDRESS);
        given(userAdapter.toUser(EMAIL_ADDRESS)).willReturn(Optional.of(user));
        given(user.getUserDetails()).willReturn((userDetails));
        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(FORGOT_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(forgotPasswordModel)));

        // Then
        resultActions.andExpect(status().isOk());
        then(userService)
            .should()
            .forgotPassword(forgotPasswordModel, user);
    }

    @Test
    public void shouldFailGivenNullEmailAddress() throws Exception {

        // Given
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel(null);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(FORGOT_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(forgotPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldFailGivenInvalidEmailAddress() throws Exception {

        // Given
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel(INVALID_EMAIL_ADDRESS);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(FORGOT_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(forgotPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void shouldReturnInternalServerErrorGivenUserManagementException() throws Exception {

        // Given
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel(EMAIL_ADDRESS);
        given(userAdapter.toUser(EMAIL_ADDRESS)).willReturn(Optional.of(user));
        given(user.getUserDetails()).willReturn((userDetails));
        willThrow(new UserManagementException(ERROR_MESSAGE))
            .given(userService)
            .forgotPassword(forgotPasswordModel, user);

        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(FORGOT_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(forgotPasswordModel)));

        // Then
        resultActions.andExpect(status().isInternalServerError());
        then(userService)
            .should()
            .forgotPassword(forgotPasswordModel, user);
        resultActions.andExpect(content().json("{'status':'INTERNAL_SERVER_ERROR', 'message':'" + ERROR_MESSAGE + "'}"));
    }

    @Test
    public void shouldReturnIncompleteUserRegistrationError() throws Exception {

        // Given
        ForgotPasswordModel forgotPasswordModel = new ForgotPasswordModel(EMAIL_ADDRESS);
        given(userAdapter.toUser(EMAIL_ADDRESS)).willReturn(Optional.of(user));
        willThrow(new UserIncompleteException("User has not completed registration"))
            .given(userService)
            .forgotPassword(forgotPasswordModel, user);
        // When
        ResultActions resultActions = this.mockMvc.perform(
            post(FORGOT_PASSWORD_ENDPOINT)
                .contentType(JSON_CONTENT_TYPE)
                .content(asJsonString(forgotPasswordModel)));

        // Then
        resultActions.andExpect(status().isBadRequest());
    }

}
