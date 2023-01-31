package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.domain.User;

/**
 * Service for recording important events, to allow auditing
 */
public interface AuditService {

    /**
     * Record the deletion of a user
     * @param deletedUser The user who has been deleted
     * @param actor The user performing the deletion.
     */
    void recordDeletion(User deletedUser, User actor);

}
