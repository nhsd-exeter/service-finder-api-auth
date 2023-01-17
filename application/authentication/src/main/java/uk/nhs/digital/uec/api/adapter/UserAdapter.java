package uk.nhs.digital.uec.api.adapter;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserAccountSummary;
import uk.nhs.digital.uec.api.domain.UserBuilder;
import uk.nhs.digital.uec.api.domain.UserSummary;
import uk.nhs.digital.uec.api.exception.InvalidEntityCodeException;
import uk.nhs.digital.uec.api.model.UserDetail;
import uk.nhs.digital.uec.api.model.UserLogin;
import uk.nhs.digital.uec.api.model.UserLoginResult;
import uk.nhs.digital.uec.api.repository.JobTypeRepository;
import uk.nhs.digital.uec.api.repository.OrganisationTypeRepository;
import uk.nhs.digital.uec.api.repository.RegionRepository;
import uk.nhs.digital.uec.api.repository.RoleRepository;
import uk.nhs.digital.uec.api.repository.UserRepository;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.PagedResult;

/**
 * Adapter class for converting {@link User} objects into other objects needed by controller
 * methods.
 */
@Component
@AllArgsConstructor
@Slf4j
public class UserAdapter {

  private final RegionRepository regionRepository;

  private final RoleRepository roleRepository;

  private final JobTypeRepository jobTypeRepository;

  private final OrganisationTypeRepository organisationTypeRepository;

  private final UserRepository userRepository;

  /**
   * Converts a {@link User} object to a {@link UserDetail} object.
   *
   * @param user the {@link User} object
   * @return the {@link UserDetail} object, or <code>null</code> if the user object is <code>null
   * </code>
   */
  public UserDetail toUserDetail(User user) {
    return new UserDetail(
        user.getEmailAddress(),
        user.isEmailAddressVerified(),
        user.getCreated(),
        user.getName(),
        user.getTelephoneNumber(),
        user.getJobName(),
        user.getJobType().getCode(),
        user.getJobTypeOther(),
        user.getOrganisationName(),
        user.getOrganisationType().getCode(),
        user.getOrganisationTypeOther(),
        user.getPostcode(),
        user.getRegion().getCode(),
        user.getApprovalStatus(),
        user.getRejectionReason(),
        user.getRoles().stream().map(Role::getCode).collect(Collectors.toCollection(TreeSet::new)),
        user.getLastLoggedIn(),
        this.toInactive(user.getUserState()));
  }

  public User toUser(UserDetail userDetail, long id) {
    return new UserBuilder()
        .withId(id)
        .withEmailAddress(userDetail.getEmailAddress())
        .withName(userDetail.getName())
        .withTelephoneNumber(userDetail.getTelephoneNumber())
        .withJobName(userDetail.getJobName())
        .withJobType(
            jobTypeRepository
                .findByCode(userDetail.getJobType())
                .orElseThrow(
                    () ->
                        new InvalidEntityCodeException(
                            "Invalid job type code: " + userDetail.getJobType())))
        .withJobTypeOther(userDetail.getJobTypeOther())
        .withOrganisationName(userDetail.getOrganisationName())
        .withOrganisationType(
            organisationTypeRepository
                .findByCode(userDetail.getOrganisationType())
                .orElseThrow(
                    () ->
                        new InvalidEntityCodeException(
                            "Invalid org type code: " + userDetail.getOrganisationType())))
        .withOrganisationTypeOther(userDetail.getOrganisationTypeOther())
        .withApprovalStatus(userDetail.getApprovalStatus())
        .withPostcode(userDetail.getPostcode())
        .withRegion(
            regionRepository
                .findByCode(userDetail.getRegion())
                .orElseThrow(
                    () ->
                        new InvalidEntityCodeException(
                            "Invalid region code:" + " " + userDetail.getRegion())))
        .withRoles(
            userDetail.getRoles().stream()
                .map(
                    code ->
                        roleRepository
                            .findByCode(code)
                            .orElseThrow(
                                () -> new InvalidEntityCodeException("Invalid role code: " + code)))
                .collect(Collectors.toCollection(TreeSet::new)))
        .withRejectionReason(userDetail.getRejectionReason())
        .withLastLoggedIn(userDetail.getLastLoggedIn())
        .withUserState(this.toUserState(userDetail.isInactive()))
        .createUser();
  }

  public Optional<User> toUser(String emailAddress) {
    return userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(emailAddress);
  }

  public Optional<User> toUser(Long id) {
    return userRepository.findById(id);
  }

  public Credentials toCredentials(UserLogin userLogin, User user) {
    log.info("Deserialize user credentials for {}",user.getEmailAddress());
    return new Credentials(user.getEmailAddress(), userLogin.getPassword());
  }

  private String toUserState(boolean inactive) {
    if (inactive) {
      return User.USER_STATE_INACTIVE;
    }
    return User.USER_STATE_ACTIVE;
  }

  private boolean toInactive(String userState) {
    if (userState.equals(User.USER_STATE_INACTIVE)) {
      return true;
    }
    return false;
  }

  /**
   * Converts a {@link User} object to a {@link UserLoginResult} object.
   *
   * @param user The user logging in
   * @return the {@link UserLoginResult} object, or <code>null</code> if the user object is <code>
   * null</code>
   */
  public UserLoginResult toUserLoginResult(User user) {
    return new UserLoginResult(
        user.getEmailAddress(),
        user.getRegion().getCode(),
        getRoles(user.getRoles(), Role::getCode),
        user.getJobType().getName(),
        user.getJobTypeOther(),
        user.getOrganisationType().getName(),
        user.getOrganisationTypeOther(),
        user.getPostcode(),
        user.getName(),
        user.getLocations(),null);
  }

  /**
   * Converts a {@link Page} of {@link User} objects to a {@link PagedResult} of {@link UserSummary}
   * objects.
   *
   * @param pagedQuery the {@link PagedQuery} containing the query parameters
   * @param users the {@link Page} of {@link User} objects
   * @return a {@link PagedResult} of {@link UserSummary} objects
   */
  public PagedResult<UserSummary> toUserSummaries(
      @NotNull PagedQuery pagedQuery, @NotNull Page<User> users) {
    List<UserSummary> userSummaries =
        users.getContent().stream().map(this::toUserSummary).collect(toList());
    return new PagedResult<>(userSummaries, users, pagedQuery);
  }

  /**
   * Converts a {@link User} object to a {@link UserSummary} object.
   *
   * @param user the {@link User} object
   * @return the {@link UserSummary} object
   */
  private UserSummary toUserSummary(@NotNull User user) {
    if (user.getUserDetails() == null) {
      return new UserSummary(
          user.getId(),
          null,
          user.getEmailAddress(),
          user.isEmailAddressVerified(),
          null,
          getRoles(user.getRoles(), Role::getCode),
          null,
          null,
          null,
          user.getCreated());
    }
    return new UserSummary(
        user.getId(),
        user.getName(),
        user.getEmailAddress(),
        user.isEmailAddressVerified(),
        user.getApprovalStatus(),
        getRoles(user.getRoles(), Role::getCode),
        user.getRegion().getCode(),
        user.getOrganisationType().getCode(),
        user.getJobType().getCode(),
        user.getCreated());
  }

  private SortedSet<String> getRoles(Set<Role> roles, Function<Role, String> mappingFunction) {
    return Optional.ofNullable(roles).orElseGet(Collections::emptySet).stream()
        .map(mappingFunction)
        .collect(Collectors.toCollection(TreeSet::new));
  }

  /**
   * Converts a {@link Page} of {@link User} objects to a {@link PagedResult} of {@link
   * UserAccountSummary} objects.
   *
   * @param pagedQuery the {@link PagedQuery} containing the query parameters
   * @param userAccounts the {@link Page} of {@link UserAccount} objects
   * @return a {@link PagedResult} of {@link UserAccountSummary} objects
   */
  public PagedResult<UserAccountSummary> toUserAccountSummaries(
      @NotNull PagedQuery pagedQuery, @NotNull Page<UserAccount> userAccounts) {
    List<UserAccountSummary> userAccountSummaries =
        userAccounts.getContent().stream().map(this::toUserAccountSummary).collect(toList());
    return new PagedResult<>(userAccountSummaries, userAccounts, pagedQuery);
  }

  /**
   * Converts a {@link UserAccount} object to a {@link UserAccountSummary} object.
   *
   * @param userAccount the {@link UserAccount} object
   * @return the {@link UserAccoutSummary} object
   */
  private UserAccountSummary toUserAccountSummary(UserAccount userAccount) {
    return new UserAccountSummary(
        userAccount.getId(),
        userAccount.isEmailAddressVerified(),
        userAccount.getEmailAddress(),
        userAccount.getCreated(),
        userAccount.getUserState());
  }
}
