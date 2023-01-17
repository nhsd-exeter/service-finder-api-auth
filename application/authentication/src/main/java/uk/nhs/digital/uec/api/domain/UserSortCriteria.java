package uk.nhs.digital.uec.api.domain;

import java.util.Arrays;
import lombok.Getter;

public enum UserSortCriteria {
  NAME("name"),
  EMAIL_ADDRESS("emailAddress"),
  EMAIL_ADDRESS_VERIFIED("emailAddressVerified"),
  APPROVAL_STATUS("approvalStatus"),
  ORGANISATION_TYPE("organisationType"),
  JOB_TYPE("jobType"),
  CREATED("created");

  @Getter
  private final String name;

  private UserSortCriteria(String name) {
    this.name = name;
  }

  public static UserSortCriteria forName(String name) {
    return Arrays
      .stream(values())
      .filter(x -> x.getName().equals(name))
      .findFirst()
      .orElseThrow(() ->
        new IllegalArgumentException(
          "No UserSortCriteria matching field name '" + name + "'"
        )
      );
  }
}
