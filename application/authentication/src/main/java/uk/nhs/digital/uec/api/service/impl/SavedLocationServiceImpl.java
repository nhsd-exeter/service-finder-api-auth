package uk.nhs.digital.uec.api.service.impl;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.domain.SavedLocation;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.model.Coordinates;
import uk.nhs.digital.uec.api.model.Location;
import uk.nhs.digital.uec.api.service.SavedLocationService;
import uk.nhs.digital.uec.api.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class SavedLocationServiceImpl implements SavedLocationService {

  private static final String POSTCODE_REGEX =
      "^[A-Z]{1,2}[0-9R][0-9A-Z]? [0-9][ABD-HJLNP-UW-Z]{2}$";
  @Autowired private final UserService userService;

  private static SavedLocation convert(Location location) {
    return SavedLocation.builder()
        .postcode(location.getPostcode())
        .description(location.getDescription())
        .latitude(location.getCoords().getLat())
        .longitude(location.getCoords().getLng())
        .datedAdded(LocalDateTime.now())
        .build();
  }

  public static Location convert(SavedLocation savedLocation) {
    return Location.builder()
        .postcode(savedLocation.getPostcode())
        .description(savedLocation.getDescription())
        .dateAdded(savedLocation.getDatedAdded())
        .coords(
            Coordinates.builder()
                .lat(savedLocation.getLatitude())
                .lng(savedLocation.getLongitude())
                .build())
        .build();
  }

  @Override
  public Set<Location> save(Location location, String userIdentification) {
    verify(location, userIdentification);
    User user = userService.getByIdentityProviderId(userIdentification);
    SavedLocation newSavedLocation = convert(location);
    newSavedLocation.setUserAccount(user.getUserAccount());
    user.getUserAccount().getSavedLocations().add(newSavedLocation);
    userService.saveUser(user);
    return user.getUserAccount().getSavedLocations().stream()
        .map(SavedLocationServiceImpl::convert)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<Location> delete(Location location, String userIdentification) {
    verify(location, userIdentification);
    User user = userService.getByIdentityProviderId(userIdentification);
    user.getUserAccount()
        .getSavedLocations()
        .removeIf(
            savedLocation -> savedLocation.getPostcode().equalsIgnoreCase(location.getPostcode()));
    userService.saveUser(user);
    return user.getUserAccount().getSavedLocations().stream()
        .map(SavedLocationServiceImpl::convert)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<Location> get(String userIdentification) {
    CheckArgument.hasText(userIdentification, "Issue with identity provider");
    return userService
        .getByIdentityProviderId(userIdentification)
        .getUserAccount()
        .getSavedLocations()
        .stream()
        .map(SavedLocationServiceImpl::convert)
        .collect(Collectors.toSet());
  }

  private Location verify(Location location, String userIdentification) {
    CheckArgument.hasText(userIdentification, "Issue with identity provider");
    CheckArgument.matches(location.getPostcode(), POSTCODE_REGEX, "Not a valid postcode");
    String postcode = location.getPostcode();
    location.setPostcode(postcode.toUpperCase());
    if (Objects.nonNull(location.getDescription()) && location.getDescription().isEmpty()) {
      location.setDescription(null);
    }
    return location;
  }
}
