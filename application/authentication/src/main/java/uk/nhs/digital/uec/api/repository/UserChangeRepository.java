package uk.nhs.digital.uec.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.digital.uec.api.domain.UserChange;

/**
 * Repository of {@link UserChange userChanges}.
 */
@Repository
public interface UserChangeRepository extends JpaRepository<UserChange, Long>, CustomUserRepository {

    Optional<List<UserChange>> findAllByUserIdOrderByUpdatedAsc(long userId);

}
