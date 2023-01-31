package uk.nhs.digital.uec.api.service;

import java.util.List;

import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserChange;


/**
 * Service for recording changes to a user
 */
public interface UserChangeService {

    /**
     * Record a user verifying
     * @param user The user who has been verified
     */
    void recordVerification(User user);

    /**
     * Record a change made to a user
     * @param user The user who has been updated
     * @param editedUser The user edits that will be made
     * @param actor The user performing the update.
     */
    void recordUpdate(User user, User editedUser, User actor);

    List<UserChange> getAllRecordsByUser(long userId);

    void deleteAllRecordsByUser(long userId);

}
