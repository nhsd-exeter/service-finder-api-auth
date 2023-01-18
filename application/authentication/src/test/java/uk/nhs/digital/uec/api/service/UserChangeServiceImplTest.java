package uk.nhs.digital.uec.api.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import uk.nhs.digital.uec.api.domain.JobType;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.Region;
import uk.nhs.digital.uec.api.domain.OrganisationType;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserChange;
import uk.nhs.digital.uec.api.domain.UserBuilder;
import uk.nhs.digital.uec.api.repository.UserChangeRepository;
import uk.nhs.digital.uec.api.repository.RoleRepository;
import static uk.nhs.digital.uec.api.domain.Role.ROLE_SEARCH;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import uk.nhs.digital.uec.api.service.impl.UserChangeServiceImpl;
import uk.nhs.digital.uec.api.service.impl.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Tests for {@link UserServiceImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class UserChangeServiceImplTest {

    @Mock
    private UserChangeRepository userChangeRepository;

    @Mock
    private RoleRepository roleRepository;

    private UserChangeServiceImpl userChangeService;

    private final User actor = new UserBuilder().createUser();

    private final User user = new UserBuilder().withName("Bob Jones").createUser();
    private final User editedUser = new UserBuilder().withName("Bob Jones").createUser();

    private final Role searchRole = new Role();
    private final Role reportRole = new Role();

    private final JobType currentJobType = new JobType();
    private final JobType editedJobType = new JobType();

    private final OrganisationType orgType1 = new OrganisationType();
    private final OrganisationType orgType2 = new OrganisationType();

    private final Region region = new Region();
    private final Region editRegion = new Region();

    {
        this.searchRole.setName("SEARCH");
        this.searchRole.setCode("SEARCH");

        this.reportRole.setName("REPORTER");
        this.reportRole.setCode("REPORTER");

        this.currentJobType.setName("Janitor");
        this.currentJobType.setCode("JANITOR");

        this.editedJobType.setName("Matron");
        this.editedJobType.setCode("MATRON");

        this.orgType1.setCode("1");
        this.orgType1.setName("org type 1");

        this.orgType2.setCode("2");
        this.orgType2.setName("org type 2");

        this.region.setCode("1");
        this.region.setName("Region 1");

        this.editRegion.setCode("2");
        this.editRegion.setName("Region 2");
    }

    @Captor
    private ArgumentCaptor<UserChange> userChangeCaptor;

    @Before
    public void setUp() {
        userChangeService = new UserChangeServiceImpl(userChangeRepository, roleRepository);

        SortedSet<Role> baseRoles = new TreeSet<Role>();
        baseRoles.add(searchRole);

        this.user.setEmailAddressVerified(false);
        this.user.setApprovalStatus("PENDING");
        this.user.setRejectionReason("");
        this.user.setRoles(baseRoles);
        this.user.setJobType(currentJobType);
        this.user.setJobName("Janitor");
        this.user.setJobTypeOther("Some other job");
        this.user.setOrganisationType(orgType1);
        this.user.setOrganisationName("Org1");
        this.user.setOrganisationTypeOther("Other org type");
        this.user.setTelephoneNumber("09876 112552");
        this.user.setPostcode("EX5 5TR");
        this.user.setRegion(region);

        this.editedUser.setEmailAddressVerified(false);
        this.editedUser.setApprovalStatus("PENDING");
        this.editedUser.setRejectionReason("");
        this.editedUser.setRoles(baseRoles);
        this.editedUser.setJobType(currentJobType);
        this.editedUser.setJobName("Janitor");
        this.editedUser.setJobTypeOther("Some other job");
        this.editedUser.setOrganisationType(orgType1);
        this.editedUser.setOrganisationName("Org1");
        this.editedUser.setOrganisationTypeOther("Other org type");
        this.editedUser.setTelephoneNumber("09876 112552");
        this.editedUser.setPostcode("EX5 5TR");
        this.editedUser.setRegion(region);

        given(this.roleRepository.findByCode(ROLE_SEARCH)).willReturn(Optional.of(this.searchRole));
    }

    @Test
    public void hasChangedReturnsCorrectValues() {
        assertThat(userChangeService.hasChanged(null, null), is(false));
        assertThat(userChangeService.hasChanged("a value", "a value"), is(false));
        assertThat(userChangeService.hasChanged("a value", null), is(true));
        assertThat(userChangeService.hasChanged(null, "a value"), is(true));
        assertThat(userChangeService.hasChanged("a value", "a value2"), is(true));
        assertThat(userChangeService.hasChanged(null, ""), is(false));
        assertThat(userChangeService.hasChanged("", null), is(false));
        assertThat(userChangeService.hasChanged(null, "   "), is(false));
        assertThat(userChangeService.hasChanged(" ", "   "), is(false));
        assertThat(userChangeService.hasChanged("", ""), is(false));
    }

    @Test
    public void formatValueReturnsCorrectValues() {
        String stringLength256 = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
                + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "123456";
        String stringLength255 = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
                + "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"
                + "12345678901234567890123456789012345678901234567890"
                + "12345";
        assertThat(userChangeService.formatValue(null), is(""));
        assertThat(userChangeService.formatValue("a string"), is("a string"));
        assertThat(userChangeService.formatValue(stringLength256), is(stringLength255));
    }

    @Test
    public void recordVerificationDoesRecordTheChange() {
        // When
        userChangeService.recordVerification(user);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), user, "unverified", "verified",
                UserChange.FieldType.EMAIL_VERIFICATION);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordNameDoesRecordTheChange() {
        // Given
        editedUser.setName("Jim Jones");

        // When
        userChangeService.recordNameUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "Bob Jones", "Jim Jones",
                UserChange.FieldType.NAME);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordStatusDoesRecordTheChange() {
        // Given
        editedUser.setApprovalStatus("APPROVED");
        // When
        userChangeService.recordStatusUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "PENDING", "APPROVED",
                UserChange.FieldType.STATUS);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordRejectionReasonDoesRecordTheChange() {
        // Given
        editedUser.setApprovalStatus("REJECTED");
        editedUser.setRejectionReason("Rejected reason");
        // When
        userChangeService.recordRejectionReasonUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "", "Rejected reason",
                UserChange.FieldType.REJECTION_REASON);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordRoleUpdateDoesRecordTheChange() {
        // Given
        SortedSet<Role> editedRoles = new TreeSet<Role>();
        editedRoles.add(searchRole);
        editedRoles.add(reportRole);

        editedUser.setRoles(editedRoles);

        // When
        userChangeService.recordRoleUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "SEARCH", "REPORTER,SEARCH",
                UserChange.FieldType.ROLE);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordJobTypeUpdateDoesRecordTheChange() {
        // Given
        editedUser.setJobType(editedJobType);
        // When
        userChangeService.recordJobTypeUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "Janitor", "Matron",
                UserChange.FieldType.JOB_TYPE);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordJobTitleUpdateDoesRecordTheChange() {
        // Given
        editedUser.setJobName("Matron");
        // When
        userChangeService.recordJobTitleUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "Janitor", "Matron",
                UserChange.FieldType.JOB_TITLE);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordOtherJobTypeUpdateDoesRecordTheChange() {
        // Given
        editedUser.setJobTypeOther("Another other job");
        // When
        userChangeService.recordOtherJobTypeUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "Some other job",
                "Another other job", UserChange.FieldType.OTHER_JOB_TYPE);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordOrganisationNameUpdateDoesRecordTheChange() {
        // Given
        editedUser.setOrganisationName("Org2");
        // When
        userChangeService.recordOrganisationNameUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "Org1", "Org2",
                UserChange.FieldType.ORGANISATION_NAME);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordOrganisationTypeUpdateDoesRecordTheChange() {
        // Given
        editedUser.setOrganisationType(orgType2);
        // When
        userChangeService.recordOrganisationTypeUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, orgType1.getName(),
                orgType2.getName(), UserChange.FieldType.ORGANISATION_TYPE);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordOtherOrganisationTypeUpdateDoesRecordTheChange() {
        // Given
        editedUser.setOrganisationTypeOther("Another org type");
        // When
        userChangeService.recordOtherOrganisationTypeUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "Other org type",
                "Another org type", UserChange.FieldType.OTHER_ORGANISATION_TYPE);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordTelephoneNumberUpdateDoesRecordTheChange() {
        // Given
        editedUser.setTelephoneNumber("07765 243552");
        // When
        userChangeService.recordTelephoneNumberUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "09876 112552", "07765 243552",
                UserChange.FieldType.TELEPHONE_NUMBER);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordPostcodeUpdateDoesRecordTheChange() {
        // Given
        editedUser.setPostcode("EX8 9LJ");
        // When
        userChangeService.recordPostcodeUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "EX5 5TR", "EX8 9LJ",
                UserChange.FieldType.POSTCODE);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordRegionUpdateDoesRecordTheChange() {
        // Given
        editedUser.setRegion(editRegion);
        // When
        userChangeService.recordRegionUpdate(user, editedUser, actor);

        // Then
        UserChange expectedUserChange = new UserChange(user, LocalDateTime.now(), actor, "Region 1", "Region 2",
                UserChange.FieldType.REGION);
        verify(userChangeRepository).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getValue(), expectedUserChange);
    }

    @Test
    public void recordUpdateDoesRecordTheChange() {
        // Given
        editedUser.setName("Jim Jones");
        editedUser.setPostcode("EX8 9LJ");
        editedUser.setRegion(editRegion);

        // When
        userChangeService.recordUpdate(user, editedUser, actor);

        // Then
        UserChange expectedNameUserChange = new UserChange(user, LocalDateTime.now(), actor, "Bob Jones", "Jim Jones",
                UserChange.FieldType.NAME);
        UserChange expectedPostcodeUserChange = new UserChange(user, LocalDateTime.now(), actor, "EX5 5TR", "EX8 9LJ",
                UserChange.FieldType.POSTCODE);
        UserChange expectedRegionUserChange = new UserChange(user, LocalDateTime.now(), actor, "Region 1", "Region 2",
                UserChange.FieldType.REGION);

        verify(userChangeRepository, times(3)).save(userChangeCaptor.capture());
        verifyUserChange(userChangeCaptor.getAllValues().get(0), expectedNameUserChange);
        verifyUserChange(userChangeCaptor.getAllValues().get(1), expectedPostcodeUserChange);
        verifyUserChange(userChangeCaptor.getAllValues().get(2), expectedRegionUserChange);
        userChangeService.recordUpdate(user, editedUser, actor);
    }

    @Test
    public void getAllRecordsByUserTest() {
        // Given
        editedUser.setName("Jim Jones");
        editedUser.setPostcode("EX8 9LJ");
        editedUser.setRegion(editRegion);
        userChangeService.recordUpdate(user, editedUser, actor);

        UserChange expectedNameUserChange = new UserChange(user, LocalDateTime.now(), actor, "Bob Jones", "Jim Jones",
                UserChange.FieldType.NAME);
        UserChange expectedPostcodeUserChange = new UserChange(user, LocalDateTime.now(), actor, "EX5 5TR", "EX8 9LJ",
                UserChange.FieldType.POSTCODE);
        UserChange expectedRegionUserChange = new UserChange(user, LocalDateTime.now(), actor, "Region 1", "Region 2",
                UserChange.FieldType.REGION);

        ArrayList<UserChange> givenUserChanges = new ArrayList<UserChange>();
        givenUserChanges.add(expectedNameUserChange);
        givenUserChanges.add(expectedPostcodeUserChange);
        givenUserChanges.add(expectedRegionUserChange);
        Optional<List<UserChange>> userChangesOptional = Optional.of(givenUserChanges);
        given(userChangeRepository.findAllByUserIdOrderByUpdatedAsc(editedUser.getId()))
                .willReturn(userChangesOptional);

        // When
        List<UserChange> userChanges = userChangeService.getAllRecordsByUser(editedUser.getId());

        // Then
        assertThat(userChanges.size(), is(3));
    }

    @Test
    public void getAllRecordsByUserTestWithNoResults() {
        // Given
        Optional<List<UserChange>> userChangesOptional = Optional.empty();
        given(userChangeRepository.findAllByUserIdOrderByUpdatedAsc(editedUser.getId()))
                .willReturn(userChangesOptional);

        // When
        List<UserChange> userChanges = userChangeService.getAllRecordsByUser(editedUser.getId());

        // Then
        assertThat(userChanges.size(), is(0));
    }

    private void verifyUserChange(UserChange userChange, UserChange expectedUserChange) {
        assertThat(userChange.getUser().getUserAccount().getId(),
                is(expectedUserChange.getUser().getUserAccount().getId()));
        assertThat(userChange.getUpdatedBy().getId(), is(expectedUserChange.getUpdatedBy().getId()));
        assertThat(userChange.getOriginalValue(), is(expectedUserChange.getOriginalValue()));
        assertThat(userChange.getNewValue(), is(expectedUserChange.getNewValue()));
        assertThat(userChange.getFieldType(), is(expectedUserChange.getFieldType()));
    }
}
