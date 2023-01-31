package uk.nhs.digital.uec.api.service;

import java.time.LocalDateTime;

import uk.nhs.digital.uec.api.domain.User;



/**
 * Service for handling notifications to users.
 */
public interface NotificationService {

    /**
     * Sends a registration approval message to a user.
     *
     * @param user the user
     */
    void sendApprovalMessage(User user);

    /**
     * Sends a registration rejection message to a user.
     *
     * @param user the user
     */
    void sendRejectionMessage(User user);

    /**
     * Sends a password reset message to a user.
     *
     * @param user the user
     */
    void sendSuccessfulPasswordResetMessage(User user, LocalDateTime date);

  /**
   * Sends a message to  complete partII registration step.
   *
   * @param user the user
   */
  void sendMessageForRegistrationPartII(User user);


}
