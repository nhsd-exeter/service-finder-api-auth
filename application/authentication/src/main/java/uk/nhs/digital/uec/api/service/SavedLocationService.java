package uk.nhs.digital.uec.api.service;

import java.util.Set;

import uk.nhs.digital.uec.api.model.Location;


public interface SavedLocationService {

  Set<Location> save(Location location, String userIdentification);

  Set<Location> delete(Location location, String userIdentification);

  Set<Location> get(String userIdentification);
}
