package uk.nhs.digital.uec.api.domain;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;

@Entity
@Table(name = "saved_location")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Builder
public class SavedLocation implements Comparable<SavedLocation> {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(nullable = false)
  private String postcode;

  @Column(length = 75)
  private String description;

  private Double latitude;

  private Double longitude;

  @Column(nullable = false)
  private LocalDateTime datedAdded;

  @ManyToOne
  @JoinColumn(name = "user_account_id", referencedColumnName = "id")
  @JsonBackReference
  private UserAccount userAccount;

  @Override
  public int compareTo(SavedLocation o) {
    return ObjectUtils.compare(o.getDatedAdded(), this.getDatedAdded());
  }
}
