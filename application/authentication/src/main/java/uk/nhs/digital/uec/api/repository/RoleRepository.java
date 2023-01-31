package uk.nhs.digital.uec.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.digital.uec.api.domain.Role;

import java.util.Optional;

/**
 * Repository of {@link Role roles}.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByCode(String code);

}
