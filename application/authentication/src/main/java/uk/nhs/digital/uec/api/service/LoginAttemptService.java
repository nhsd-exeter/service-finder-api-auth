package uk.nhs.digital.uec.api.service;

/**
 * The Login attempt service.
 */
public interface LoginAttemptService {

    /**
     * Remove all failed login attempts for a user account.
     *
     * @param emailAddress the email address of the user
     */
    void remove(String emailAddress);

    /**
     * Add a failed login attempt to a user account.
     *
     * @param emailAddress the email address of the user
     */
    void add(String emailAddress);

    /**
     * Check if the user account is blocked.
     *
     * @param emailAddress the email address of the user
     * @return the boolean true if the user has exceeded the maximum number of failed logins otherwise, return false
     */
    boolean isBlocked(String emailAddress);
}
