package uk.nhs.digital.uec.api.service;


import java.util.Set;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;

import uk.nhs.digital.uec.api.common.model.LoginResult;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.model.EmailVerification;
import uk.nhs.digital.uec.api.model.ForgotPasswordModel;
import uk.nhs.digital.uec.api.model.RegistrationResult;
import uk.nhs.digital.uec.api.model.ResetPasswordModel;

public interface CognitoService {

    /**
     * Verifies the email address of a user.
     *
     * @param emailVerification a user email address and code encapsulated in an object
     */
    void verify(EmailVerification emailVerification);

    /**
     * Resends the confirmation message for a user.
     *
     * @param emailAddress the email address to resend the confirmation message
     */
    void resendConfirmationMessage(String emailAddress);

    /**
     * Registers a new user
     *
     * @param {@link Credentials} Details of the credentials
     */
    RegistrationResult register(Credentials credentials);

    /**
     * Authenticate a user
     *
     * @param credentials the credentials supplied by the user, must not be null
     * @return {@link LoginResult}, will never be null
     * @throws InvalidLoginException if invalid password or unauthorized Cognito login is detected
     */
    LoginResult authenticate(Credentials credentials) throws InvalidLoginException;

    /**
     * Authenticate a user using the refresh token and their identity provider id
     *
     * @param refreshToken the refresh token, must have text
     * @param identityProviderId the identity provider id of the user, must have text
     * @return {@link LoginResult}, will never be null
     */
    LoginResult authenticateWithRefreshToken(String refreshToken, String identityProviderId);

    /**
     * Handles a forgot password request.
     *
     * @param forgotPasswordModel the details of the forgot password request
     */
    void forgotPassword(ForgotPasswordModel forgotPasswordModel, User user);

    /**
     * Handles a reset password (confirm forgot password) request.
     *
     * @param resetPasswordModel the details of the reset password request
     */
    void resetPassword(ResetPasswordModel resetPasswordModel, User user);

  /**
     * Get the roles of a user
     *
     * @param emailAddress Email address of a user
     */
    Set<String> retrieveRoles(String emailAddress);

    /**
     * Add a role to a user
     *
     * @param emailAddress Email address of a user
     * @param roleCode The role to add
     */
    void addRole(String roleCode, String emailAddress);

    /**
     * Delete a role from a user
     *
     * @param emailAddress Email address of a user
     * @param roleCode The role to delete
     */
    void deleteRole(String roleCode, String emailAddress);

    /**
     * Deletes a user from Cognito
     *
     * @param user The user being deleted
     */
    void deleteUser(User user);

    /**
     * Get user details from Cognito by email address.
     *
     * @param emailAddress the email address of the user to return the user details for
     * @return {@link AdminGetUserResult}
     */
    AdminGetUserResult getUserByEmailAddress(String emailAddress);

    /**
     * Retrieve the value of the attribute specified for the specified user.
     *
     * @param emailAddress the email address of the user to retrieve the attribute value from.
     * @param attributeName the name of the attribute to retrieve the value from.
     */
    String retrieveUserAttribute(final String emailAddress, final String attributeName);

}
