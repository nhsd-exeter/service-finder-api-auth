package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
@Builder
public class Coordinates {
  private Double lat;
  private Double lng;
}
