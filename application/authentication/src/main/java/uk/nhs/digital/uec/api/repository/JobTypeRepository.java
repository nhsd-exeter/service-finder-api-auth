package uk.nhs.digital.uec.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.nhs.digital.uec.api.domain.JobType;

import java.util.Optional;

/**
 * Repository of {@link JobType job types}.
 */
@Repository
public interface JobTypeRepository extends JpaRepository<JobType, Long> {

    Optional<JobType> findByCode(String code);

}
