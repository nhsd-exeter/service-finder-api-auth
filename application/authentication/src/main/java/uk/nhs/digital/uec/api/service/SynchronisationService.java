package uk.nhs.digital.uec.api.service;

import uk.nhs.digital.uec.api.domain.User;

/**
 * Service for ensuring specific user properties in the identity provider stay in sync when the user properties in the database change.
 */
public interface SynchronisationService {

    /**
     * Synchronise a users roles
     *
     * @param user The users whose roles we want to synchronise
     */
    void synchroniseRoles(User user);
}
