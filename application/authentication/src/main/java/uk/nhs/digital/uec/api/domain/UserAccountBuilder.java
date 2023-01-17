package uk.nhs.digital.uec.api.domain;

import java.time.LocalDateTime;
import java.util.SortedSet;

public class UserAccountBuilder {

  private long id;
  private LocalDateTime created;
  private LocalDateTime updated;
  private UserAccount updatedBy;
  private String identityProviderId;
  private boolean emailAddressVerified;
  private String emailAddress;
  private LocalDateTime lastLoggedIn;
  private String userState;
  private UserDetails userDetails;
  private SortedSet<SavedLocation> savedLocations;

  public UserAccountBuilder withId(long id) {
    this.id = id;
    return this;
  }

  public UserAccountBuilder withCreated(LocalDateTime created) {
    this.created = created;
    return this;
  }

  public UserAccountBuilder withUpdated(LocalDateTime updated) {
    this.updated = updated;
    return this;
  }

  public UserAccountBuilder withUpdatedBy(UserAccount updatedBy) {
    this.updatedBy = updatedBy;
    return this;
  }

  public UserAccountBuilder withIdentityProviderId(String identityProviderId) {
    this.identityProviderId = identityProviderId;
    return this;
  }

  public UserAccountBuilder withEmailAddressVerified(boolean emailAddressVerified) {
    this.emailAddressVerified = emailAddressVerified;
    return this;
  }

  public UserAccountBuilder withEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
    return this;
  }

  public UserAccountBuilder withLastLoggedIn(LocalDateTime lastLoggedIn) {
    this.lastLoggedIn = lastLoggedIn;
    return this;
  }

  public UserAccountBuilder withUserState(String userState) {
    this.userState = userState;
    return this;
  }

  public UserAccountBuilder withUserDetails(UserDetails userDetails) {
    this.userDetails = userDetails;
    return this;
  }

  public UserAccountBuilder withSavedLocations(SortedSet<SavedLocation> savedLocations) {
    this.savedLocations = savedLocations;
    return this;
  }

  public UserAccount build() {
    return new UserAccount(
        id,
        created,
        updated,
        updatedBy,
        identityProviderId,
        emailAddressVerified,
        emailAddress,
        lastLoggedIn,
        userState,
        userDetails,
        savedLocations);
  }
}
