package uk.nhs.digital.uec.api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import uk.nhs.digital.uec.api.common.model.LoginResult;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.exception.NoRolesException;
import uk.nhs.digital.uec.api.model.ForgotPasswordModel;
import uk.nhs.digital.uec.api.model.ResetPasswordModel;

/**
 * Service for handling users.
 */
public interface UserService {
  /**
   * Logs the user in
   *
   * @param credentials the credentials for the login attempt
   * @param user the user returned from the DB that is trying to log in.
   * @return a payload containing all the required login results
   * @throws NoRolesException if no roles exist in the access token
   * @throws InvalidLoginException if unable to authenticate successfully via Cognito
   */
  LoginResult login(User user, Credentials credentials)
    throws NoRolesException, InvalidLoginException;

  /**
   * Authenticates using a refresh token
   *
   * @param refreshToken the refresh token
   * @param identityProviderId the identity provider ID
   * @return The result of the authentication
   */
  LoginResult loginWithRefreshToken(
    String refreshToken,
    String identityProviderId
  );

  /**
   * Gets a user by its unique numeric ID.
   *
   * @param id the unique user ID
   * @return the user object
   */
  User getById(long id);

  /**
   * Gets a user by email address
   *
   * @param emailAddress
   * @return user
   */
  Optional<User> getByEmailAddress(String emailAddress);

  /**
   * Gets a user by its unique identity provider ID.
   *
   * @param identityProviderId the identity provider ID
   * @return the user object
   */
  User getByIdentityProviderId(String identityProviderId);

  /**
   * Gets a paginated list of users matching a query.
   *
   * @param userSummaryQuery the user summary query
   * @return the paged result
   */
  Page<User> findUsers(PagedQuery userSummaryQuery);

  /**
   * Gets a paginated list of users matching a query.
   *
   * @param userFilterCriteria the user summary filter
   * @return the paged result
   */
  List<User> findUsers(Map<UserFilterCriteria, String> userFilterCriteria);

  /**
   * Saves a user.
   *
   * @param user the user to save.
   * @return the saved user object.
   */
  User saveUser(User user);

  /**
   * Updates a user.
   *
   * @param editedUser the user being edited
   * @param admin the logged in user performing the editing
   * @return the updated user object
   */
  User update(User editedUser, User admin);

  /**
   * Resends the confirmation message for a user.
   *
   * @param emailAddress an email address to resend the confirmation message for
   * @return true or false depending on if the user is in the approved domains list
   */
  boolean resendConfirmationMessage(String emailAddress);

  /**
   * Triggers the forgot password logic.
   *
   * @param forgotPasswordModel the forgot password model
   * @param user a user to resend the confirmation message for encapsulated in an object
   */
  void forgotPassword(ForgotPasswordModel forgotPasswordModel, User user);

  /**
   * Resets the password for the user.
   *
   * @param resetPasswordModel The payload containing the email address
   * @param user a user we are resetting the password for
   */
  void resetPassword(ResetPasswordModel resetPasswordModel, User user);

  /**
   * Deletes a user.
   *
   * @param userForDeletion The user to be deleted
   * @param actor the admin user performing the deletion
   */
  void delete(User userForDeletion, User actor);

  /**
   * Returns true if the email address supplied is within an approved domain; false otherwise.
   *
   * @param emailAddress the email address to check.
   * @return boolean.
   */
  boolean isEmailInApprovedDomain(final String emailAddress);

  /**
   * Updates the approved status and role for an approved user.
   *
   * @param user the user object to update.
   */
  void updatePropertiesForApprovedUser(User user);

  /**
   * Gets a paginated list of unregistered users matching a query.
   *
   * @param userSummaryQuery the user summary query
   * @return the paged result
   */
  Page<UserAccount> findUnregisteredUsers(PagedQuery userSummaryQuery);
}
