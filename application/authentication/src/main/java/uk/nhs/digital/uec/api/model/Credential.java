package uk.nhs.digital.uec.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Credential {

  private String emailAddress;
  private String password;
}
