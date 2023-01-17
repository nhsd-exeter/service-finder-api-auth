package uk.nhs.digital.uec.api.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.uec.api.model.Coordinates;
import uk.nhs.digital.uec.api.model.Location;
import uk.nhs.digital.uec.api.domain.SavedLocation;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserAccountBuilder;
import uk.nhs.digital.uec.api.service.impl.SavedLocationServiceImpl;

@RunWith(MockitoJUnitRunner.class)
public class SavedLocationServiceImplTest {

  private final String userIdentification = "userIdentification";
  private final String postcode = "EX1 1AL";

  private Location location;

  private SavedLocation savedLocation;

  private UserAccount account;

  @Mock
  private UserService mockUserService;

  @Mock
  private User userMock;

  @InjectMocks
  private SavedLocationServiceImpl classUnderTest;

  @Before
  public void Setup() {
    location = new Location(postcode, "Description", new Coordinates(0.0, 0.0), null);
    savedLocation = new SavedLocation();
    savedLocation.setId(1);
    savedLocation.setPostcode(postcode);
    savedLocation.setDescription("Description");
    savedLocation.setLatitude(0.0);
    savedLocation.setLongitude(0.0);
    savedLocation.setDatedAdded(LocalDateTime.now());
    SortedSet<SavedLocation> savedLocationsSet = new TreeSet<>();
    savedLocationsSet.add(savedLocation);
    account = new UserAccountBuilder().withSavedLocations(savedLocationsSet).build();
  }

  @Test
  public void saveNewLocation() {
    // Given

    given(mockUserService.getByIdentityProviderId(userIdentification)).willReturn(userMock);
    given(userMock.getUserAccount()).willReturn(account);
    // Then
    Set<Location> results = classUnderTest.save(location, userIdentification);

    // Then
    verify(mockUserService, times(1)).saveUser(userMock);
    verify(mockUserService, times(1)).getByIdentityProviderId(userIdentification);
    assertFalse(results.isEmpty());
  }

  @Test
  public void missingArgumentsWhenSavingLocation() {
    // Given
    String expectedMessage = "Not a valid postcode";

    // When
    Exception exception = assertThrows(
        IllegalArgumentException.class,
        () -> {
          classUnderTest.save(new Location(), userIdentification);
        });

    // Then
    assertTrue(expectedMessage.contains(exception.getMessage()));
  }

  @Test
  public void deleteLocationForUser() {
    // Given
    given(mockUserService.getByIdentityProviderId(userIdentification)).willReturn(userMock);
    given(userMock.getUserAccount()).willReturn(account);

    // when
    Set<Location> results = classUnderTest.delete(location, userIdentification);

    // Then
    verify(mockUserService, times(1)).saveUser(userMock);
    verify(mockUserService, times(1)).getByIdentityProviderId(userIdentification);
    assertTrue(results.isEmpty());
  }

  @Test
  public void getListOfLocationsForUser() {
    // Given
    given(mockUserService.getByIdentityProviderId(userIdentification)).willReturn(userMock);
    given(userMock.getUserAccount()).willReturn(account);

    // when
    Set<Location> results = classUnderTest.get(userIdentification);

    // Then
    verify(mockUserService, times(1)).getByIdentityProviderId(userIdentification);
    assertTrue(results.stream().anyMatch(l -> l.getPostcode().equals(postcode)));
  }
}
