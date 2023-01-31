package uk.nhs.digital.uec.api.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RegistrationPartIIReminderMessage {

  @NotNull(message = "heading must not be null")
  private String heading;

  @NotNull(message = "url must not be null")
  private String url;

  private String paragraphPartOne;

  private String paragraphPartTwo;
}
