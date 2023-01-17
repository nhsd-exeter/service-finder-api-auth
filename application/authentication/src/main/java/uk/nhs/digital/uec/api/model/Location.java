package uk.nhs.digital.uec.api.model;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Location implements Comparable<Location> {
  private String postcode;
  private String description;
  private Coordinates coords;
  private LocalDateTime dateAdded;

  @Override
  public int compareTo(Location o) {
    return ObjectUtils.compare(this.dateAdded, o.dateAdded);
  }
}
