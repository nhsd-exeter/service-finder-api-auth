package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PostcodeMapping {
  private String postcode;
  private String name;
  private Integer easting;
  private Integer northing;
  private String ccg;
  private String organisationCode;
  private String nhs_region;
  private String icb;
  private String email;
  private String region;
  private String subRegion;
}
