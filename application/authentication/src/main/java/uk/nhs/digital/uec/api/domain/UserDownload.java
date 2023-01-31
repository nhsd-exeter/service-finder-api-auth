package uk.nhs.digital.uec.api.domain;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An individual that may register to use the Service Finder or Admin tool. */
@Entity
@Table(name = "user_download")
@NoArgsConstructor
@Setter
@Getter
public class UserDownload {

  private String name;

  private String userState;

  @Id private String emailAddress;

  private Boolean emailAddressVerified;

  private String approvalStatus;

  private Date lastLoggedIn;

  private String region;

  private String orgType;

  private String jobType;

  private Date created;

  private Long numericUserId;

  private String identityProviderId;

  private Date updated;

  private Date termsAndConditionsAccepted;

  private String rejectionReason;

  private String roles;

  private String jobName;

  private String organisationName;

  private String postcode;

  private String telephoneNumber;

  public UserDownload(
      String name,
      String userState,
      String emailAddress,
      Boolean emailAddressVerified,
      String approvalStatus,
      Date lastLoggedIn,
      String region,
      String orgType,
      String jobType,
      Date created,
      Long numericUserId,
      String identityProviderId,
      Date updated,
      Date termsAndConditionsAccepted,
      String rejectionReason,
      String roles,
      String jobName,
      String organisationName,
      String postcode,
      String telephoneNumber) {

    this.name = name;
    this.userState = userState;
    this.emailAddress = emailAddress;
    this.emailAddressVerified = emailAddressVerified;
    this.approvalStatus = approvalStatus;
    this.lastLoggedIn = lastLoggedIn;
    this.region = region;
    this.orgType = orgType;
    this.jobType = jobType;
    this.created = created;
    this.numericUserId = numericUserId;
    this.identityProviderId = identityProviderId;
    this.updated = updated;
    this.termsAndConditionsAccepted = termsAndConditionsAccepted;
    this.rejectionReason = rejectionReason;
    this.roles = roles;
    this.jobName = jobName;
    this.organisationName = organisationName;
    this.postcode = postcode;
    this.telephoneNumber = telephoneNumber;
  }
}
