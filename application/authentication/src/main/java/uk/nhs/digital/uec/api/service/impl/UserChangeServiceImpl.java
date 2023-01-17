package uk.nhs.digital.uec.api.service.impl;

import static uk.nhs.digital.uec.api.domain.Role.ROLE_SEARCH;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserChange;
import uk.nhs.digital.uec.api.exception.InvalidEntityCodeException;
import uk.nhs.digital.uec.api.repository.RoleRepository;
import uk.nhs.digital.uec.api.repository.UserChangeRepository;
import uk.nhs.digital.uec.api.service.UserChangeService;

/*
 * The {@link UserChangeService} default implementation.
 */
@Service
public class UserChangeServiceImpl implements UserChangeService {

  private final RoleRepository roleRepository;
  private final UserChangeRepository userChangeRepository;

  private static final Integer VALUE_LENGTH = 255;

  @Autowired
  public UserChangeServiceImpl(
      UserChangeRepository userChangeRepository, RoleRepository roleRepository) {
    this.userChangeRepository = userChangeRepository;
    this.roleRepository = roleRepository;
  }

  /** {@inheritDoc} */
  @Override
  public void recordVerification(User user) {
    recordChange(user, user, "unverified", "verified", UserChange.FieldType.EMAIL_VERIFICATION);
  }

  /** {@inheritDoc} */
  @Override
  public void recordUpdate(User user, User editedUser, User actor) {
    recordNameUpdate(user, editedUser, actor);
    recordStatusUpdate(user, editedUser, actor);
    recordStateUpdate(user, editedUser, actor);
    recordRejectionReasonUpdate(user, editedUser, actor);
    recordRoleUpdate(user, editedUser, actor);
    recordJobTitleUpdate(user, editedUser, actor);
    recordJobTypeUpdate(user, editedUser, actor);
    recordOtherJobTypeUpdate(user, editedUser, actor);
    recordOrganisationNameUpdate(user, editedUser, actor);
    recordOrganisationTypeUpdate(user, editedUser, actor);
    recordOtherOrganisationTypeUpdate(user, editedUser, actor);
    recordTelephoneNumberUpdate(user, editedUser, actor);
    recordPostcodeUpdate(user, editedUser, actor);
    recordRegionUpdate(user, editedUser, actor);
  }

  @Override
  public List<UserChange> getAllRecordsByUser(long userId) {
    Optional<List<UserChange>> userChanges =
        userChangeRepository.findAllByUserIdOrderByUpdatedAsc(userId);
    if (userChanges.isPresent()) {
      return userChanges.get();
    }
    return new ArrayList<UserChange>();
  }

  @Override
  public void deleteAllRecordsByUser(long userId) {
    userChangeRepository.deleteAll(getAllRecordsByUser(userId));
  }

  public void recordNameUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getName(), editedUser.getName())) {
      return;
    }
    recordChange(user, actor, user.getName(), editedUser.getName(), UserChange.FieldType.NAME);
  }

  public void recordStatusUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getApprovalStatus(), editedUser.getApprovalStatus())) {
      return;
    }
    recordChange(
        user,
        actor,
        user.getApprovalStatus(),
        editedUser.getApprovalStatus(),
        UserChange.FieldType.STATUS);
  }

  protected void recordStateUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getUserState(), editedUser.getUserState())) {
      return;
    }
    recordChange(
        user, actor, user.getUserState(), editedUser.getUserState(), UserChange.FieldType.STATE);
  }

  public void recordRejectionReasonUpdate(User user, User editedUser, User actor) {
    if (editedUser.getApprovalStatus() == "REJECTED") {
      if (!hasChanged(user.getRejectionReason(), editedUser.getRejectionReason())) {
        return;
      }
      recordChange(
          user,
          actor,
          user.getRejectionReason(),
          editedUser.getRejectionReason(),
          UserChange.FieldType.REJECTION_REASON);
    }
  }

  public void recordRoleUpdate(User user, User editedUser, User actor) {
    // Here we need to make sure that 'Search' is always in the list of roles since this
    // role is always assumed to be assigned to the user, but this isn't always represented
    // this way in the database. For example, the search role isn't actually assigned to
    // the user until the user is approved.
    Role searchRole =
        this.roleRepository
            .findByCode(ROLE_SEARCH)
            .orElseThrow(() -> new InvalidEntityCodeException("Could not find search role"));
    user.addRole(searchRole);
    editedUser.addRole(searchRole);

    if (user.getRoles().equals(editedUser.getRoles())) {
      return;
    }
    recordChange(
        user,
        actor,
        formatRoles(user.getRoles()),
        formatRoles(editedUser.getRoles()),
        UserChange.FieldType.ROLE);
  }

  private String formatRoles(SortedSet<Role> roles) {
    StringBuilder roleList = new StringBuilder();
    String separator = "";
    for (Role role : roles) {
      roleList.append(separator);
      roleList.append(role.getName());
      separator = ",";
    }
    return roleList.toString();
  }

  public void recordJobTitleUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getJobName(), editedUser.getJobName())) {
      return;
    }
    recordChange(
        user, actor, user.getJobName(), editedUser.getJobName(), UserChange.FieldType.JOB_TITLE);
  }

  public void recordJobTypeUpdate(User user, User editedUser, User actor) {
    if (user.getJobType().equals(editedUser.getJobType())) {
      return;
    }
    recordChange(
        user,
        actor,
        user.getJobType().getName(),
        editedUser.getJobType().getName(),
        UserChange.FieldType.JOB_TYPE);
  }

  public void recordOtherJobTypeUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getJobTypeOther(), editedUser.getJobTypeOther())) {
      return;
    }
    recordChange(
        user,
        actor,
        user.getJobTypeOther(),
        editedUser.getJobTypeOther(),
        UserChange.FieldType.OTHER_JOB_TYPE);
  }

  public void recordOrganisationNameUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getOrganisationName(), editedUser.getOrganisationName())) {
      return;
    }
    recordChange(
        user,
        actor,
        user.getOrganisationName(),
        editedUser.getOrganisationName(),
        UserChange.FieldType.ORGANISATION_NAME);
  }

  public void recordOrganisationTypeUpdate(User user, User editedUser, User actor) {
    if (user.getOrganisationType().compareTo(editedUser.getOrganisationType()) == 0) {
      return;
    }
    recordChange(
        user,
        actor,
        user.getOrganisationType().getName(),
        editedUser.getOrganisationType().getName(),
        UserChange.FieldType.ORGANISATION_TYPE);
  }

  public void recordOtherOrganisationTypeUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getOrganisationTypeOther(), editedUser.getOrganisationTypeOther())) {
      return;
    }
    recordChange(
        user,
        actor,
        user.getOrganisationTypeOther(),
        editedUser.getOrganisationTypeOther(),
        UserChange.FieldType.OTHER_ORGANISATION_TYPE);
  }

  public void recordTelephoneNumberUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getTelephoneNumber(), editedUser.getTelephoneNumber())) {
      return;
    }
    recordChange(
        user,
        actor,
        user.getTelephoneNumber(),
        editedUser.getTelephoneNumber(),
        UserChange.FieldType.TELEPHONE_NUMBER);
  }

  public void recordPostcodeUpdate(User user, User editedUser, User actor) {
    if (!hasChanged(user.getPostcode(), editedUser.getPostcode())) {
      return;
    }
    recordChange(
        user, actor, user.getPostcode(), editedUser.getPostcode(), UserChange.FieldType.POSTCODE);
  }

  public void recordRegionUpdate(User user, User editedUser, User actor) {
    if (user.getRegion().compareTo(editedUser.getRegion()) == 0) {
      return;
    }
    recordChange(
        user,
        actor,
        user.getRegion().getName(),
        editedUser.getRegion().getName(),
        UserChange.FieldType.REGION);
  }

  public String formatValue(String value) {
    return value == null
        ? ""
        : value.substring(0, value.length() > VALUE_LENGTH - 1 ? VALUE_LENGTH : value.length());
  }

  protected void recordChange(
      User user, User actor, String oldValue, String newValue, UserChange.FieldType fieldType) {
    UserChange userChange =
        new UserChange(
            user,
            LocalDateTime.now(),
            actor,
            formatValue(oldValue),
            formatValue(newValue),
            fieldType);
    userChangeRepository.save(userChange);
  }

  public boolean hasChanged(String oldValue, String newValue) {

    if (oldValue == null && newValue == null) {
      return false;
    }

    if (oldValue == null && newValue.trim().isEmpty()) {
      return false;
    }

    if (newValue == null && oldValue.trim().isEmpty()) {
      return false;
    }

    if (oldValue != null && newValue != null && oldValue.trim().contentEquals(newValue.trim())) {
      return false;
    }

    return true;
  }
}
