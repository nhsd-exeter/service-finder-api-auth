package uk.nhs.digital.uec.api.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.SortedSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.nhs.digital.uec.api.model.Location;

import org.springframework.data.annotation.LastModifiedDate;


/** An individual that may register to use the Service Finder or Admin tool. */
@Entity
@Table(name = "user_account")
@NoArgsConstructor
@Setter
@Getter
public class UserAccount {

  public static final String USER_STATE_INACTIVE = "INACTIVE";

  public static final String USER_STATE_ACTIVE = "ACTIVE";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private LocalDateTime created;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDateTime updated;

  @ManyToOne
  @JoinColumn(name = "updated_by", referencedColumnName = "id")
  @JsonBackReference
  private UserAccount updatedBy;

  @Column(nullable = false, unique = true)
  private String identityProviderId;

  @Column(nullable = false)
  private boolean emailAddressVerified;

  @Column(nullable = false, unique = true)
  private String emailAddress;

  private LocalDateTime lastLoggedIn;

  @Column(nullable = false)
  private String userState;

  private LocalDateTime inactiveDate;

  @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
  private UserDetails userDetails;

  @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("datedAdded")
  @JsonManagedReference
  private SortedSet<SavedLocation> savedLocations;

  public UserAccount(
      long id,
      String identityProviderId,
      boolean emailAddressVerified,
      String emailAddress,
      LocalDateTime lastLoggedIn,
      String userState,
      SortedSet<Location> lo) {

    this.id = id;
    this.identityProviderId = identityProviderId;
    this.emailAddressVerified = emailAddressVerified;
    this.emailAddress = emailAddress;
    this.lastLoggedIn = lastLoggedIn;
    this.userState = userState;
    this.savedLocations = getSavedLocations();
  }

  public UserAccount(
      long id,
      LocalDateTime created,
      LocalDateTime updated,
      UserAccount updatedBy,
      String identityProviderId,
      boolean emailAddressVerified,
      String emailAddress,
      LocalDateTime lastLoggedIn,
      String userState,
      UserDetails userDetails,
      SortedSet<SavedLocation> savedLocations) {
    this.id = id;
    this.created = created;
    this.updated = updated;
    this.updatedBy = updatedBy;
    this.identityProviderId = identityProviderId;
    this.emailAddressVerified = emailAddressVerified;
    this.emailAddress = emailAddress;
    this.lastLoggedIn = lastLoggedIn;
    this.userState = userState;
    this.userDetails = userDetails;
    this.savedLocations = savedLocations;
  }

  public static boolean hasField(String fieldName) {
    return Arrays.stream(UserAccount.class.getDeclaredFields())
        .anyMatch(f -> f.getName().equals(fieldName));
  }

  @PrePersist
  private void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    created = now;
    updated = now;
  }

  @PreUpdate
  private void preUpdate() {
    updated = LocalDateTime.now();
  }

  public User convertToUser() {
    return new User(this);
  }
}
