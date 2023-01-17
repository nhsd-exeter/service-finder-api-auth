package uk.nhs.digital.uec.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;

import java.util.List;
import java.util.Map;

/**
 * Custom repository for complex {@link User} queries involving pagination, sorting and filtering.
 */
public interface CustomUserRepository {

    /**
     * Gets a page of {@link User}s based on the supplied page request and filter criteria.
     *
     * @param pageRequest the {@link PageRequest) page request} object
     * @param filterCriteria the filter criteria
     * @return the page of users
     */
    Page<User> findByQuery(PageRequest pageRequest, Map<UserFilterCriteria, String> filterCriteria);

    /**
     * Gets a list of {@link User}s based on the supplied page request and filter criteria.
     *
     * @param filterCriteria the filter criteria
     * @return the list of users
     */
    List<User> findByQuery(Map<UserFilterCriteria, String> filterCriteria);

    /**
     * Gets a page of {@link User}s based on the supplied page request and filter criteria.
     *
     * @param pageRequest the {@link PageRequest) page request} object
     * @param filterCriteria the filter criteria
     * @return the page of unregistered users
     */
    Page<UserAccount> findUnregisteredUserAccounts(PageRequest pageRequest, Map<UserFilterCriteria, String> filterCriteria);


}
