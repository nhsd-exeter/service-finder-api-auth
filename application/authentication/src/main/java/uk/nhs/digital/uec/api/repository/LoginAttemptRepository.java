package uk.nhs.digital.uec.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.digital.uec.api.domain.LoginAttempt;

import java.util.Optional;

/**
 * Repository of {@link LoginAttempt}s.
 */
@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {

    Optional<LoginAttempt> findByEmailAddressIgnoreCase(String emailAddress);

}
