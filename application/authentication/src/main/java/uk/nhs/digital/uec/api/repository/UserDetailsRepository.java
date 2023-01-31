package uk.nhs.digital.uec.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import uk.nhs.digital.uec.api.domain.UserDetails;

/**
 * Repository of {@link Event events}.
 */
@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetails, Long> {
}
