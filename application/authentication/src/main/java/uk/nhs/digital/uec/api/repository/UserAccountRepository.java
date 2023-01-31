package uk.nhs.digital.uec.api.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.nhs.digital.uec.api.domain.UserAccount;

/**
 * Repository of {@link UserAccountRepository}.
 */
@Repository
public interface UserAccountRepository
  extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByIdentityProviderId(String identityProviderId);

  Optional<UserAccount> findFirstByEmailAddressIgnoreCaseOrderByIdAsc(
    String emailAddress
  );

  List<Optional<UserAccount>> findByUserDetailsIsNull();
}
