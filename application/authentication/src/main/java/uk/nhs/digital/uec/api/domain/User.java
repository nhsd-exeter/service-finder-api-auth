package uk.nhs.digital.uec.api.domain;

import static uk.nhs.digital.uec.api.domain.Role.ROLE_ADMIN;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import uk.nhs.digital.uec.api.service.impl.SavedLocationServiceImpl;
import uk.nhs.digital.uec.api.exception.InvalidApprovalStatusChangeException;
import uk.nhs.digital.uec.api.exception.MissingRejectionReasonException;
import uk.nhs.digital.uec.api.exception.RejectionReasonUpdateException;
import uk.nhs.digital.uec.api.model.Location;

/** An individual that may register to use the Service Finder or Admin tool. */
public class User {

  public static final String APPROVAL_STATUS_APPROVED = "APPROVED";

  public static final String APPROVAL_STATUS_PENDING = "PENDING";

  public static final String APPROVAL_STATUS_REJECTED = "REJECTED";

  public static final String USER_STATE_INACTIVE = "INACTIVE";

  public static final String USER_STATE_ACTIVE = "ACTIVE";

  private UserAccount userAccount;

  private User updatedBy;

  private User approvalStatusUpdatedBy;

  /** Default constructor will need to initialise UserAccount object */
  public User() {
    UserAccount userAccount = new UserAccount();
    userAccount.setUserDetails(new UserDetails());
    this.setUserAccount(userAccount);
  }

  public User(UserAccount userAccount) {
    this.userAccount = userAccount;
  }

  @PostConstruct
  public void initialise() {
    this.updatedBy = convertUserAccountToUser(userAccount.getUpdatedBy());
    this.approvalStatusUpdatedBy =
        convertUserAccountToUser(userAccount.getUserDetails().getApprovalStatusUpdatedBy());
  }

  public void updateDetailsFromUser(User updatedUser, User admin) {
    setUpdatedBy(admin);
    setName(updatedUser.getName());
    setTelephoneNumber(updatedUser.getTelephoneNumber());
    setJobName(updatedUser.getJobName());
    setJobType(updatedUser.getJobType());
    setJobTypeOther(updatedUser.getJobTypeOther());
    setOrganisationName(updatedUser.getOrganisationName());
    setOrganisationType(updatedUser.getOrganisationType());
    setOrganisationTypeOther(updatedUser.getOrganisationTypeOther());
    setPostcode(updatedUser.getPostcode());
    setRegion(updatedUser.getRegion());
    setUpdated(LocalDateTime.now());
  }

  private boolean validateApprovalStatusChange(
      String updatedApprovalStatus, String updatedRejectionReason, String oldUserState) {
    if (oldUserState.equals(USER_STATE_ACTIVE)
        && updatedApprovalStatus.equals(APPROVAL_STATUS_REJECTED)) {
      return true;
    }

    if (this.getUserState().equals(USER_STATE_INACTIVE)
        && !updatedApprovalStatus.equals(APPROVAL_STATUS_APPROVED)) {
      throw new InvalidApprovalStatusChangeException(
          "Cannot change to " + updatedApprovalStatus + " while user is inactive");
    }

    if (!isValidApprovalStatusChange(updatedApprovalStatus)) {
      throw new InvalidApprovalStatusChangeException(
          "Cannot change from " + getApprovalStatus() + " to " + updatedApprovalStatus);
    }

    return true;
  }

  public void processApprovalStatusChange(
      String updatedApprovalStatus,
      String updatedRejectionReason,
      String oldUserState,
      User admin,
      Clock clock) {

    this.validateApprovalStatusChange(updatedApprovalStatus, updatedRejectionReason, oldUserState);

    if (this.getUserState().equals(USER_STATE_INACTIVE)
        && updatedApprovalStatus.equals(APPROVAL_STATUS_APPROVED)) {
      this.setUserState(USER_STATE_ACTIVE);
      this.setInactiveDate(null);
    }

    if (updatedApprovalStatus.equals(APPROVAL_STATUS_REJECTED)) {
      if (updatedRejectionReason == null) {
        throw new MissingRejectionReasonException();
      }
      this.setUserState(USER_STATE_INACTIVE);
      this.setInactiveDate(LocalDateTime.now());
    } else if (updatedRejectionReason != null
        && (!updatedRejectionReason.equals(this.getRejectionReason()))) {
      throw new RejectionReasonUpdateException();
    }

    this.setApprovalStatus(updatedApprovalStatus);
    this.setApprovalStatusUpdated(LocalDateTime.now(clock));
    this.setApprovalStatusUpdatedBy(admin);
    this.setUpdatedBy(admin);
    if (updatedApprovalStatus.equals(APPROVAL_STATUS_REJECTED)) {
      this.setRejectionReason(updatedRejectionReason);
    }
  }

  public void addRole(Role role) {
    if (this.getRoles() == null) {
      TreeSet<Role> roles = new TreeSet<Role>();
      this.setRoles(roles);
    }
    this.getRoles().add(role);
  }

  public boolean isAdmin() {
    return this.getRoles().stream().anyMatch(r -> r.getCode().equals(ROLE_ADMIN));
  }

  public boolean isApproved() {
    return this.getApprovalStatus() != null
        ? this.getApprovalStatus().equals(APPROVAL_STATUS_APPROVED)
        : false;
  }

  private boolean isValidApprovalStatusChange(String updatedApprovalStatus) {
    boolean isValidApprovalStatusChange = false;
    switch (updatedApprovalStatus) {
      case APPROVAL_STATUS_REJECTED:
        isValidApprovalStatusChange = (APPROVAL_STATUS_PENDING.equals(getApprovalStatus()));
        break;
      case APPROVAL_STATUS_APPROVED:
        isValidApprovalStatusChange =
            ((APPROVAL_STATUS_PENDING.equals(getApprovalStatus()))
                || (APPROVAL_STATUS_REJECTED.equals(getApprovalStatus())));
        break;
      default:
        isValidApprovalStatusChange = false;
    }
    return isValidApprovalStatusChange;
  }

  public UserDetails getUserDetails() {
    return this.getUserAccount().getUserDetails();
  }

  public void setUserDetails(UserDetails userDetails) {
    this.getUserAccount().setUserDetails(userDetails);
  }

  public UserAccount getUserAccount() {
    return userAccount;
  }

  public void setUserAccount(UserAccount userAccount) {
    this.userAccount = userAccount;
  }

  public long getId() {
    return this.getUserAccount().getId();
  }

  public void setId(long id) {
    this.getUserAccount().setId(id);
  }

  public LocalDateTime getCreated() {
    return this.getUserAccount().getCreated();
  }

  public void setCreated(LocalDateTime created) {
    this.getUserAccount().setCreated(created);
  }

  public LocalDateTime getUpdated() {
    return this.getUserAccount().getUpdated();
  }

  public void setUpdated(LocalDateTime updated) {
    this.getUserAccount().setUpdated(updated);
  }

  public User getUpdatedBy() {
    return this.updatedBy;
  }

  public void setUpdatedBy(User updatedBy) {
    this.getUserAccount().setUpdatedBy(updatedBy.getUserAccount());
    this.updatedBy = updatedBy;
  }

  public String getIdentityProviderId() {
    return this.getUserAccount().getIdentityProviderId();
  }

  public void setIdentityProviderId(String identityProviderId) {
    this.getUserAccount().setIdentityProviderId(identityProviderId);
  }

  public boolean isEmailAddressVerified() {
    return this.getUserAccount().isEmailAddressVerified();
  }

  public void setEmailAddressVerified(boolean emailAddressVerified) {
    this.getUserAccount().setEmailAddressVerified(emailAddressVerified);
  }

  public String getEmailAddress() {
    return this.getUserAccount().getEmailAddress();
  }

  public void setEmailAddress(String emailAddress) {
    this.getUserAccount().setEmailAddress(emailAddress);
  }

  public String getName() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getName();
  }

  public void setName(String name) {
    this.getUserAccount().getUserDetails().setName(name);
  }

  public String getTelephoneNumber() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getTelephoneNumber();
  }

  public void setTelephoneNumber(String telephoneNumber) {
    this.getUserAccount().getUserDetails().setTelephoneNumber(telephoneNumber);
  }

  public String getJobName() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getJobName();
  }

  public void setJobName(String jobName) {
    this.getUserAccount().getUserDetails().setJobName(jobName);
  }

  public JobType getJobType() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getJobType();
  }

  public void setJobType(JobType jobType) {
    this.getUserAccount().getUserDetails().setJobType(jobType);
  }

  public String getJobTypeOther() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getJobTypeOther();
  }

  public void setJobTypeOther(String jobTypeOther) {
    this.getUserAccount().getUserDetails().setJobTypeOther(jobTypeOther);
  }

  public String getOrganisationName() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getOrganisationName();
  }

  public void setOrganisationName(String organisationName) {
    this.getUserAccount().getUserDetails().setOrganisationName(organisationName);
  }

  public OrganisationType getOrganisationType() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getOrganisationType();
  }

  public void setOrganisationType(OrganisationType organisationType) {
    this.getUserAccount().getUserDetails().setOrganisationType(organisationType);
  }

  public String getOrganisationTypeOther() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getOrganisationTypeOther();
  }

  public void setOrganisationTypeOther(String organisationTypeOther) {
    this.getUserAccount().getUserDetails().setOrganisationTypeOther(organisationTypeOther);
  }

  public String getPostcode() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getPostcode();
  }

  public void setPostcode(String postcode) {
    this.getUserAccount().getUserDetails().setPostcode(postcode);
  }

  public Region getRegion() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getRegion();
  }

  public void setRegion(Region region) {
    this.getUserAccount().getUserDetails().setRegion(region);
  }

  public String getApprovalStatus() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getApprovalStatus();
  }

  public void setApprovalStatus(String approvalStatus) {
    this.getUserAccount().getUserDetails().setApprovalStatus(approvalStatus);
  }

  public LocalDateTime getApprovalStatusUpdated() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getApprovalStatusUpdated();
  }

  public void setApprovalStatusUpdated(LocalDateTime approvalStatusUpdated) {
    this.getUserAccount().getUserDetails().setApprovalStatusUpdated(approvalStatusUpdated);
  }

  public User getApprovalStatusUpdatedBy() {
    return this.approvalStatusUpdatedBy;
  }

  public void setApprovalStatusUpdatedBy(User approvalStatusUpdatedBy) {
    this.getUserAccount()
        .getUserDetails()
        .setApprovalStatusUpdatedBy(approvalStatusUpdatedBy.getUserAccount());
    this.approvalStatusUpdatedBy = approvalStatusUpdatedBy;
  }

  public String getRejectionReason() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getRejectionReason();
  }

  public void setRejectionReason(String rejectionReason) {
    this.getUserAccount().getUserDetails().setRejectionReason(rejectionReason);
  }

  public LocalDateTime getTermsAndConditionsAccepted() {
    if (this.getUserAccount().getUserDetails() == null) {
      return null;
    }
    return this.getUserAccount().getUserDetails().getTermsAndConditionsAccepted();
  }

  public void setTermsAndConditionsAccepted(LocalDateTime termsAndConditionsAccepted) {
    this.getUserAccount()
        .getUserDetails()
        .setTermsAndConditionsAccepted(termsAndConditionsAccepted);
  }

  public SortedSet<Role> getRoles() {
    if (this.getUserAccount().getUserDetails() == null) {
      return new TreeSet<>();
    }
    if (this.getUserAccount().getUserDetails().getRoles() == null) {
      return new TreeSet<>();
    }
    return this.getUserAccount().getUserDetails().getRoles();
  }

  public void setRoles(SortedSet<Role> roles) {
    this.getUserAccount().getUserDetails().setRoles(roles);
  }

  public LocalDateTime getLastLoggedIn() {
    return this.getUserAccount().getLastLoggedIn();
  }

  public void setLastLoggedIn(LocalDateTime lastLoggedIn) {
    this.getUserAccount().setLastLoggedIn(lastLoggedIn);
  }

  public String getUserState() {
    return this.getUserAccount().getUserState();
  }

  public void setUserState(String userState) {
    this.getUserAccount().setUserState(userState);
  }

  public LocalDateTime getInactiveDate() {
    return this.getUserAccount().getInactiveDate();
  }

  public void setInactiveDate(LocalDateTime inactiveDate) {
    this.getUserAccount().setInactiveDate(inactiveDate);
  }

  private User convertUserAccountToUser(final UserAccount userAccount) {
    if (userAccount == null) {
      return null;
    }
    return userAccount.convertToUser();
  }

  public SortedSet<Location> getLocations() {
    if (this.getUserAccount().getSavedLocations() == null) {
      return new TreeSet<>();
    }
    return this.getUserAccount().getSavedLocations().stream()
        .map(SavedLocationServiceImpl::convert)
        .sorted(Comparator.reverseOrder())
        .collect(Collectors.toCollection(TreeSet::new));
  }
}
