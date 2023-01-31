package uk.nhs.digital.uec.api.domain;

import java.util.Arrays;
import lombok.Getter;

public enum UserFilterCriteria {
  NAME("name", FilterType.LINKED_STRING_CASE_INSENSITIVE_LIKE),
  EMAIL_ADDRESS("emailAddress", FilterType.STRING_CASE_INSENSITIVE_LIKE),
  EMAIL_ADDRESS_VERIFIED("emailAddressVerified", FilterType.BOOLEAN_VALUE),
  APPROVAL_STATUS("approvalStatus", FilterType.LINKED_STRING_EXACT_MATCH),
  REGION("region", FilterType.LINKED_JOIN_BY_CODE),
  ROLES("roles", FilterType.LINKED_JOIN_BY_CODE),
  ORGANISATION_TYPE("organisationType", FilterType.LINKED_JOIN_BY_CODE),
  JOB_TYPE("jobType", FilterType.LINKED_JOIN_BY_CODE),
  REGISTERED_DATE("registeredDate", FilterType.DATE_EXACT_MATCH),
  USER_STATE("userState", FilterType.STRING_CASE_INSENSITIVE_LIKE);

  @Getter
  private final String name;

  @Getter
  private final FilterType filterType;

  UserFilterCriteria(String name, FilterType filterType) {
    this.name = name;
    this.filterType = filterType;
  }

  public static UserFilterCriteria forName(String name) {
    return Arrays
      .stream(values())
      .filter(x -> x.getName().equals(name))
      .findFirst()
      .orElseThrow(() ->
        new IllegalArgumentException(
          "No UserFilterCriteria matching field name '" + name + "'"
        )
      );
  }
}
