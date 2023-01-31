package uk.nhs.digital.uec.api.repository;

import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Repository of {@link User users}. */
@Slf4j
@Component
public class UserRepository extends CustomUserRepositoryImpl {

  @Autowired
  public UserRepository(
      EntityManager entityManager,
      UserAccountRepository userAccountRepository,
      UserDetailsRepository userDetailsRepository) {
    super(entityManager, userAccountRepository, userDetailsRepository);
  }

  /**
   * Gets the user by a matching identity provider ID.
   *
   * @param identityProviderId the identity provider ID
   * @return an <code>Optional</code> object which may contain the user matching the given identity
   *     provider ID
   */
  public Optional<User> findByIdentityProviderId(String identityProviderId) {
    Optional<UserAccount> optionalUserAccount =
        userAccountRepository.findByIdentityProviderId(identityProviderId);
    if (optionalUserAccount.isPresent()) {
      return Optional.of(optionalUserAccount.get().convertToUser());
    }
    return Optional.empty();
  }

  /**
   * Gets the user by a matching email address, ignoring case
   *
   * @param emailAddress the email address
   * @return an <code>Optional</code> object which may contain the user matching the given email
   *     address
   */
  public Optional<User> findFirstByEmailAddressIgnoreCaseOrderByIdAsc(String emailAddress) {
    Optional<UserAccount> optionalUserAccount =
        userAccountRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(emailAddress);
    log.info("Looking for user account {}", emailAddress);
    if (optionalUserAccount.isPresent()) {
      log.info("Found user account! {}", emailAddress);
      return Optional.of(optionalUserAccount.get().convertToUser());
    }
    log.info("User account not found");
    return Optional.empty();
  }

  /**
   * Gets the user by a matching id
   *
   * @param id the id
   * @return an <code>Optional</code> object which may contain the user matching the given id
   */
  public Optional<User> findById(Long id) {
    Optional<UserAccount> optionalUserAccount = userAccountRepository.findById(id);
    if (optionalUserAccount.isPresent()) {
      return Optional.of(optionalUserAccount.get().convertToUser());
    }
    return Optional.empty();
  }

  /**
   * Gets the user by a matching id
   *
   * @param id the id
   * @return The user matching the given id
   */
  public User getOne(long id) {
    UserAccount userAccount = userAccountRepository.getOne(id);
    User user = userAccount.convertToUser();
    return user;
  }

  public User save(User user) {
    UserAccount userAccount = userAccountRepository.saveAndFlush(user.getUserAccount());
    return userAccount.convertToUser();
  }

  public User saveAndFlush(User user) {
    UserAccount userAccount = userAccountRepository.saveAndFlush(user.getUserAccount());
    return userAccount.convertToUser();
  }

  public void delete(User user) {
    if (user.getUserAccount().getUserDetails() != null) {
      log.info("Deleting user details : " + user.getUserAccount().getUserDetails().getId());
      userDetailsRepository.delete(user.getUserAccount().getUserDetails());
    }
    log.info("Deleting user account : " + user.getUserAccount().getId());
    userAccountRepository.delete(user.getUserAccount());
  }
}
