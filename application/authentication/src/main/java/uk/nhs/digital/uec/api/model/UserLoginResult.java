package uk.nhs.digital.uec.api.model;

import java.util.Set;
import java.util.SortedSet;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Represents the data returned when a user logs in or returns to the application. */
@AllArgsConstructor
@Data
public class UserLoginResult {

  private String emailAddress;

  private String region;

  private Set<String> roles;

  private String jobType;

  private String jobTypeOther;

  private String organisationType;

  private String organisationTypeOther;

  private String postcode;

  private String name;

  private SortedSet<Location> savedLocations;

  private String ccg;
}
