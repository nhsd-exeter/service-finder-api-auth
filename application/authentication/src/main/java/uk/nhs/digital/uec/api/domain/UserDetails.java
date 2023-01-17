package uk.nhs.digital.uec.api.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.time.LocalDateTime;
import java.util.SortedSet;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An individual that may register to use the Service Finder or Admin tool. */
@Entity
@Table(name = "user_details")
@NoArgsConstructor
@Setter
@Getter
public class UserDetails {

  public static final String APPROVAL_STATUS_APPROVED = "APPROVED";

  public static final String APPROVAL_STATUS_PENDING = "PENDING";

  public static final String APPROVAL_STATUS_REJECTED = "REJECTED";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToOne
  @JoinColumn(name = "user_account_id", referencedColumnName = "id")
  @JsonBackReference
  private UserAccount userAccount;

  @Column(nullable = false)
  private LocalDateTime created;

  @Column(nullable = false)
  private String name;

  private String telephoneNumber;

  @Column(nullable = false)
  private String jobName;

  @ManyToOne
  @JoinColumn(name = "job_type_id", referencedColumnName = "id", nullable = false)
  private JobType jobType;

  private String jobTypeOther;

  @Column(nullable = false)
  private String organisationName;

  @ManyToOne
  @JoinColumn(name = "organisation_type_id", referencedColumnName = "id", nullable = false)
  private OrganisationType organisationType;

  private String organisationTypeOther;

  private String postcode;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinTable(
      name = "user_region",
      joinColumns = {@JoinColumn(name = "user_details_id")},
      inverseJoinColumns = {@JoinColumn(name = "region_id")})
  @OrderBy("id")
  private Region region;

  @Column(nullable = false)
  private String approvalStatus;

  private LocalDateTime approvalStatusUpdated;

  @ManyToOne
  @JoinColumn(name = "approval_status_updated_by", referencedColumnName = "id")
  @JsonBackReference
  private UserAccount approvalStatusUpdatedBy;

  private String rejectionReason;

  private LocalDateTime termsAndConditionsAccepted;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinTable(
      name = "user_role",
      joinColumns = {@JoinColumn(name = "user_details_id")},
      inverseJoinColumns = {@JoinColumn(name = "role_id")})
  @OrderBy("id")
  private SortedSet<Role> roles;

  public UserDetails(
      long id,
      UserAccount userAccount,
      String name,
      String telephoneNumber,
      String jobName,
      JobType jobType,
      String jobTypeOther,
      String organisationName,
      OrganisationType organisationType,
      String organisationTypeOther,
      String postcode,
      Region region,
      String approvalStatus,
      LocalDateTime approvalStatusUpdated,
      UserAccount approvalStatusUpdatedBy,
      String rejectionReason,
      LocalDateTime termsAndConditionsAccepted,
      SortedSet<Role> roles) {

    this.id = id;
    this.userAccount = userAccount;
    this.name = name;
    this.telephoneNumber = telephoneNumber;
    this.jobName = jobName;
    this.jobType = jobType;
    this.jobTypeOther = jobTypeOther;
    this.organisationName = organisationName;
    this.organisationType = organisationType;
    this.organisationTypeOther = organisationTypeOther;
    this.postcode = postcode;
    this.region = region;
    this.approvalStatus = approvalStatus;
    this.approvalStatusUpdated = approvalStatusUpdated;
    this.approvalStatusUpdatedBy = approvalStatusUpdatedBy;
    this.rejectionReason = rejectionReason;
    this.termsAndConditionsAccepted = termsAndConditionsAccepted;
    this.roles = roles;
  }

  public UserDetails(
      long id,
      UserAccount userAccount,
      LocalDateTime created,
      String name,
      String telephoneNumber,
      String jobName,
      JobType jobType,
      String jobTypeOther,
      String organisationName,
      OrganisationType organisationType,
      String organisationTypeOther,
      String postcode,
      Region region,
      String approvalStatus,
      LocalDateTime approvalStatusUpdated,
      UserAccount approvalStatusUpdatedBy,
      String rejectionReason,
      LocalDateTime termsAndConditionsAccepted,
      SortedSet<Role> roles) {

    this.id = id;
    this.userAccount = userAccount;
    this.created = created;
    this.name = name;
    this.telephoneNumber = telephoneNumber;
    this.jobName = jobName;
    this.jobType = jobType;
    this.jobTypeOther = jobTypeOther;
    this.organisationName = organisationName;
    this.organisationType = organisationType;
    this.organisationTypeOther = organisationTypeOther;
    this.postcode = postcode;
    this.region = region;
    this.approvalStatus = approvalStatus;
    this.approvalStatusUpdated = approvalStatusUpdated;
    this.approvalStatusUpdatedBy = approvalStatusUpdatedBy;
    this.rejectionReason = rejectionReason;
    this.termsAndConditionsAccepted = termsAndConditionsAccepted;
    this.roles = roles;
  }

  @PrePersist
  private void prePersist() {
    LocalDateTime now = LocalDateTime.now();
    created = now;
  }
}
