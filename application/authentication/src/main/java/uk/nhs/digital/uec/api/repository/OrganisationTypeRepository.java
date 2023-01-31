package uk.nhs.digital.uec.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.digital.uec.api.domain.OrganisationType;

import java.util.Optional;

/**
 * Repository of {@link OrganisationType organisation types}.
 */
@Repository
public interface OrganisationTypeRepository extends JpaRepository<OrganisationType, Long> {

    Optional<OrganisationType> findByCode(String code);

}
