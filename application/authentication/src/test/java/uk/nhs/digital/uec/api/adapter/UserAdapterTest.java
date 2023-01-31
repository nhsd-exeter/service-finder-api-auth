package uk.nhs.digital.uec.api.adapter;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import uk.nhs.digital.uec.api.model.UserDetail;
import uk.nhs.digital.uec.api.model.UserLoginResult;
import uk.nhs.digital.uec.api.domain.JobType;
import uk.nhs.digital.uec.api.domain.OrganisationType;
import uk.nhs.digital.uec.api.domain.Region;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserAccountBuilder;
import uk.nhs.digital.uec.api.domain.UserAccountSummary;
import uk.nhs.digital.uec.api.domain.UserBuilder;
import uk.nhs.digital.uec.api.domain.UserSortCriteria;
import uk.nhs.digital.uec.api.repository.JobTypeRepository;
import uk.nhs.digital.uec.api.repository.OrganisationTypeRepository;
import uk.nhs.digital.uec.api.repository.RegionRepository;
import uk.nhs.digital.uec.api.repository.RoleRepository;
import uk.nhs.digital.uec.api.repository.UserRepository;
import uk.nhs.digital.uec.api.service.PagedQuery;
import uk.nhs.digital.uec.api.service.PagedResult;
import uk.nhs.digital.uec.api.service.SortOrder;
import uk.nhs.digital.uec.api.domain.UserSummary;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static uk.nhs.digital.uec.api.domain.Role.ROLE_ADMIN;
import static uk.nhs.digital.uec.api.domain.Role.ROLE_SEARCH;

@RunWith(MockitoJUnitRunner.class)
public class UserAdapterTest {

    private static final long USER_ID = 12345;

    private static final JobType ADMIN_JOB_TYPE = new JobType(1, "Administrator", "ADMIN");

    private static final JobType OTHER_JOB_TYPE = new JobType(2, "Other job type", "OTHER");

    private static final OrganisationType NHS_DIGITAL = new OrganisationType(1, "NHS Digital", "NHSD");

    private static final OrganisationType OTHER_ORGANISATION_TYPE = new OrganisationType(2, "Other organisation type", "OTHER");

    private static final Region TEST_REGION = new Region(1, "Test Region", "TEST");

    private static final Region TEST_REGION_ADMIN = new Region(2, "Test Region Admin", "TEST_ADMIN");

    private static final Role ADMIN_ROLE = new Role(1, "Administrator", ROLE_ADMIN);

    private static final Role SEARCH_ROLE = new Role(2, "Search", ROLE_SEARCH);

    private String regionCode = "region_code";

    private String roleCode = "role_code";

    private Region region = new Region(1, "", regionCode);

    private String jobTypeCode = "OTHER";

    private JobType jobType = new JobType(1, "", jobTypeCode);

    private String orgTypeCode = "OTHER";

    private OrganisationType orgType = new OrganisationType(1, "", orgTypeCode);

    private User testAdmin;

    private User testUser;

    private UserAccount testUserAccountAdmin;

    private UserAccount testUserAccount;

    LocalDateTime registrationDate = LocalDateTime.of(2019, 10, 1, 10, 12, 45);

    @Mock
    private JobTypeRepository jobTypeRepository;

    @Mock
    private OrganisationTypeRepository organisationTypeRepository;

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserAdapter userAdapter;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Before
    public void setUp() {
        testAdmin = new UserBuilder().createUser();
        testAdmin.setId(1);
        testAdmin.setName("Test Admin");
        testAdmin.setJobName("Application administrator");
        testAdmin.setJobType(ADMIN_JOB_TYPE);
        testAdmin.setJobTypeOther("IT admin");
        testAdmin.setOrganisationName("NHS Digital");
        testAdmin.setOrganisationType(NHS_DIGITAL);
        testAdmin.setOrganisationTypeOther("");
        testAdmin.setRegion(TEST_REGION_ADMIN);
        testAdmin.setRoles(new TreeSet<>(Arrays.asList(ADMIN_ROLE)));

        testUser = new UserBuilder().createUser();
        testUser.setApprovalStatus("APPROVED");
        testUser.setApprovalStatusUpdated(LocalDateTime.of(2019, 10, 1, 10, 44, 7));
        testUser.setApprovalStatusUpdatedBy(testAdmin);
        testUser.setCreated(registrationDate);
        testUser.setEmailAddress("test@example.com");
        testUser.setEmailAddressVerified(true);
        testUser.setId(2);
        testUser.setIdentityProviderId("01234567-89ab-cdef-0123-456789abcdef");
        testUser.setJobName("Job Name");
        testUser.setJobType(OTHER_JOB_TYPE);
        testUser.setJobTypeOther("Job Type");
        testUser.setName("Test User");
        testUser.setOrganisationName("Job Name");
        testUser.setOrganisationType(OTHER_ORGANISATION_TYPE);
        testUser.setOrganisationTypeOther("Job Type");
        testUser.setPostcode("BS1 6AA");
        testUser.setRegion(TEST_REGION);
        testUser.setRoles(new TreeSet<>(Arrays.asList(SEARCH_ROLE)));
        testUser.setTelephoneNumber("07496 103425");
        testUser.setTermsAndConditionsAccepted(LocalDateTime.of(2019, 10, 1, 10, 12, 46));
        testUser.setUpdated(LocalDateTime.of(2019, 10, 1, 11, 2, 19));
        testUser.setUserState(User.USER_STATE_ACTIVE);
    }

    @Test
    public void testToUserDetail() {
        // Given

        // When
        UserDetail userDetail = userAdapter.toUserDetail(testUser);

        // Then
        assertThat(userDetail.getApprovalStatus(), is(testUser.getApprovalStatus()));
        assertThat(userDetail.getEmailAddress(), is(testUser.getEmailAddress()));
        assertThat(userDetail.getRegistrationDate(), is(registrationDate));
        assertThat(userDetail.getJobName(), is(testUser.getJobName()));
        assertThat(userDetail.getJobType(), is(testUser.getJobType().getCode()));
        assertThat(userDetail.getJobTypeOther(), is(testUser.getJobTypeOther()));
        assertThat(userDetail.getName(), is(testUser.getName()));
        assertThat(userDetail.getOrganisationName(), is(testUser.getOrganisationName()));
        assertThat(userDetail.getOrganisationType(), is(testUser.getOrganisationType().getCode()));
        assertThat(userDetail.getOrganisationTypeOther(), is(testUser.getOrganisationTypeOther()));
        assertThat(userDetail.getPostcode(), is(testUser.getPostcode()));
        assertThat(userDetail.getRegion(), is(testUser.getRegion().getCode()));
        assertThat(userDetail.getRoles(), is(testUser.getRoles().stream().map(Role::getCode).collect(Collectors.toSet())));
        assertThat(userDetail.getTelephoneNumber(), is(testUser.getTelephoneNumber()));
    }

    @Test
    public void toUserLoginResult() {
        // When
        UserLoginResult userLoginResult = userAdapter.toUserLoginResult(testUser);

        // Then
        assertThat(userLoginResult.getEmailAddress(), is(testUser.getEmailAddress()));
        assertThat(userLoginResult.getRegion(), is(testUser.getRegion().getCode()));
        assertThat(userLoginResult.getRoles(), is(testUser.getRoles().stream().map(Role::getCode).collect(Collectors.toSet())));
        assertThat(userLoginResult.getJobType(), is(testUser.getJobType().getName()));
        assertThat(userLoginResult.getJobTypeOther(), is(testUser.getJobTypeOther()));
        assertThat(userLoginResult.getOrganisationType(), is(testUser.getOrganisationType().getName()));
        assertThat(userLoginResult.getOrganisationTypeOther(), is(testUser.getOrganisationTypeOther()));
        assertThat(userLoginResult.getPostcode(), is(testUser.getPostcode()));
        assertThat(userLoginResult.getSavedLocations(), is(testUser.getLocations()));
    }

    @Test
    public void testToUserSummaries() {
        // Given
        PagedQuery pagedQuery = new PagedQuery(0, 50, Map.of(UserSortCriteria.EMAIL_ADDRESS, SortOrder.ASCENDING), Collections.emptyMap());
        Page<User> users = new PageImpl<>(List.of(testAdmin, testUser), PageRequest.of(0, 50), 2);

        // When
        PagedResult<UserSummary> pagedResult = userAdapter.toUserSummaries(pagedQuery, users);

        // Then
        assertThat(pagedResult.getQuery(), is(pagedQuery));
        assertThat(pagedResult.getNumber(), is(0));
        assertThat(pagedResult.getNumberOfElements(), is(2));
        assertThat(pagedResult.getSize(), is(50));
        assertThat(pagedResult.getTotalElements(), is(2L));
        assertThat(pagedResult.getTotalPages(), is(1));
        assertThat(pagedResult.hasContent(), is(true));
        assertThat(pagedResult.hasNext(), is(false));
        assertThat(pagedResult.hasPrevious(), is(false));
        assertThat(pagedResult.isFirst(), is(true));
        assertThat(pagedResult.isLast(), is(true));

        List<UserSummary> userSummaries = pagedResult.getContent();
        assertThat(userSummaries, notNullValue());
        assertThat(userSummaries.size(), is(2));

        UserSummary approver = userSummaries.get(0);
        assertThat(approver.getApprovalStatus(), is(testAdmin.getApprovalStatus()));
        assertThat(approver.getEmailAddress(), is(testAdmin.getEmailAddress()));
        assertThat(approver.getId(), is(testAdmin.getId()));
        assertThat(approver.getJobType(), is(ADMIN_JOB_TYPE.getCode()));
        assertThat(approver.getName(), is(testAdmin.getName()));
        assertThat(approver.getOrganisationType(), is(NHS_DIGITAL.getCode()));
        assertThat(approver.getRegion(), notNullValue());
        assertThat(approver.getRegion(), is(TEST_REGION_ADMIN.getCode()));
        assertThat(approver.getRoles(), notNullValue());
        assertThat(approver.getRoles().size(), is(1));
        assertThat(approver.getRoles(), is(Collections.singleton(ADMIN_ROLE.getCode())));
        assertThat(approver.getRegistrationDate(), is(testAdmin.getCreated()));

        UserSummary user = userSummaries.get(1);
        assertThat(user.getApprovalStatus(), is(testUser.getApprovalStatus()));
        assertThat(user.getEmailAddress(), is(testUser.getEmailAddress()));
        assertThat(user.getId(), is(testUser.getId()));
        assertThat(user.getJobType(), is(OTHER_JOB_TYPE.getCode()));
        assertThat(user.getName(), is(testUser.getName()));
        assertThat(user.getOrganisationType(), is(OTHER_ORGANISATION_TYPE.getCode()));
        assertThat(user.getRegion(), notNullValue());
        assertThat(user.getRegion(), is(TEST_REGION.getCode()));
        assertThat(user.getRoles(), notNullValue());
        assertThat(user.getRoles().size(), is(1));
        assertThat(user.getRoles(), is(Collections.singleton(SEARCH_ROLE.getCode())));
        assertThat(user.getRegistrationDate(), is(testUser.getCreated()));
    }

    // @Test
    // public void toUserShouldSuccessfullyConvertFromUserDetail() {
    //     //Given
    //     UserDetail userDetail = createTestUserDetail();
    //     given(jobTypeRepository.findByCode(UserDetailTestFactory.jobTypeCode)).willReturn(Optional.of(UserDetailTestFactory.jobType));
    //     given(organisationTypeRepository.findByCode(UserDetailTestFactory.orgTypeCode)).willReturn(Optional.of(UserDetailTestFactory.orgType));
    //     given(regionRepository.findByCode(UserDetailTestFactory.regionCode)).willReturn(Optional.of(UserDetailTestFactory.region));
    //     given(roleRepository.findByCode(UserDetailTestFactory.roleCode)).willReturn(Optional.of(UserDetailTestFactory.role));

    //     //When
    //     User user = userAdapter.toUser(userDetail, USER_ID);

    //     //Then
    //     assertThat(user.getName(), is(UserDetailTestFactory.name));
    //     assertThat(user.getEmailAddress(), is(UserDetailTestFactory.emailAddress));
    //     assertThat(user.getTelephoneNumber(), is(UserDetailTestFactory.telephoneNumber));
    //     assertThat(user.getJobName(), is(UserDetailTestFactory.jobName));
    //     assertThat(user.getJobType().getCode(), is(UserDetailTestFactory.jobTypeCode));
    //     assertThat(user.getJobTypeOther(), is(UserDetailTestFactory.jobTypeOther));
    //     assertThat(user.getOrganisationName(), is(UserDetailTestFactory.orgName));
    //     assertThat(user.getOrganisationType().getCode(), is(UserDetailTestFactory.orgTypeCode));
    //     assertThat(user.getOrganisationTypeOther(), is(UserDetailTestFactory.orgTypeOther));
    //     assertThat(user.getPostcode(), is(UserDetailTestFactory.postcode));
    //     assertThat(user.getRejectionReason(), is(UserDetailTestFactory.rejectionReason));
    //     assertThat(user.getRegion().getCode(), is(UserDetailTestFactory.regionCode));
    //     assertThat(user.getRoles().size(), is(1));
    //     assertThat(user.getRoles().iterator().next().getCode(), is(UserDetailTestFactory.roleCode));
    // }

    // @Test
    // public void toUserShouldThrowInvalidDomainCodeExceptionGivenInvalidJobType() {
    //     //Given
    //     given(jobTypeRepository.findByCode(UserDetailTestFactory.jobTypeCode)).willReturn(Optional.empty());

    //     // Expectations
    //     exceptionRule.expect(InvalidEntityCodeException.class);
    //     exceptionRule.expectMessage("Invalid job type code: " + UserDetailTestFactory.jobTypeCode);

    //     //When
    //     userAdapter.toUser(createTestUserDetail(), USER_ID);
    // }

    // @Test
    // public void toUserShouldThrowInvalidDomainCodeExceptionGivenInvalidOrgType() {
    //     //Given
    //     given(jobTypeRepository.findByCode(UserDetailTestFactory.jobTypeCode)).willReturn(Optional.of(jobType));
    //     given(organisationTypeRepository.findByCode(UserDetailTestFactory.orgTypeCode)).willReturn(Optional.empty());

    //     // Expectations
    //     exceptionRule.expect(InvalidEntityCodeException.class);
    //     exceptionRule.expectMessage("Invalid org type code: " + UserDetailTestFactory.orgTypeCode);

    //     //When
    //     userAdapter.toUser(createTestUserDetail(), USER_ID);
    // }

    // @Test
    // public void toUserShouldThrowInvalidDomainCodeExceptionGivenInvalidRegionType() {
    //     //Given
    //     given(jobTypeRepository.findByCode(UserDetailTestFactory.jobTypeCode)).willReturn(Optional.of(jobType));
    //     given(organisationTypeRepository.findByCode(UserDetailTestFactory.orgTypeCode)).willReturn(Optional.of(orgType));
    //     given(regionRepository.findByCode(UserDetailTestFactory.regionCode)).willReturn(Optional.empty());

    //     // Expectations
    //     exceptionRule.expect(InvalidEntityCodeException.class);
    //     exceptionRule.expectMessage("Invalid region code: " + regionCode);

    //     //When
    //     userAdapter.toUser(createTestUserDetail(), USER_ID);
    // }

    // @Test
    // public void toUserShouldThrowInvalidDomainCodeExceptionGivenInvalidRoleType() {
    //     //Given
    //     given(jobTypeRepository.findByCode(UserDetailTestFactory.jobTypeCode)).willReturn(Optional.of(jobType));
    //     given(organisationTypeRepository.findByCode(UserDetailTestFactory.orgTypeCode)).willReturn(Optional.of(orgType));
    //     given(regionRepository.findByCode(UserDetailTestFactory.regionCode)).willReturn(Optional.of(region));
    //     given(roleRepository.findByCode(UserDetailTestFactory.roleCode)).willReturn(Optional.empty());

    //     // Expectations
    //     exceptionRule.expect(InvalidEntityCodeException.class);
    //     exceptionRule.expectMessage("Invalid role code: " + roleCode);

    //     //When
    //     userAdapter.toUser(createTestUserDetail(), USER_ID);
    // }

    @Test
    public void toUserByEmailAddressShouldReturnUser() {
        //Given
        String emailAddress = "test@test.com";
        User persistentUser = new User();
        given(userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(emailAddress)).willReturn(Optional.of(persistentUser));

        //When
        Optional<User> userResponse = userAdapter.toUser(emailAddress);

        //Then
        assertThat(userResponse.get(), is(persistentUser));
    }

    @Test
    public void toUserByIdShouldReturnUser() {
        //Given
        Long userId = 123456L;
        User persistentUser = new User();
        given(userRepository.findById(userId)).willReturn(Optional.of(persistentUser));

        //When
        Optional<User> userResponse = userAdapter.toUser(userId);

        //Then
        assertThat(userResponse.get(), is(persistentUser));
    }

    @Test
    public void testToUserAccountSummaries() {

        //Mock values
        testUserAccountAdmin = new UserAccountBuilder().build();
        testUserAccountAdmin.setId(1);
        testUserAccountAdmin.setEmailAddress("abcd1234@nhs.net");
        testUserAccountAdmin.setEmailAddressVerified(false);
        testUserAccountAdmin.setCreated(LocalDateTime.now());
        testUserAccountAdmin.setUserState("ACTIVE");

        // Given
        PagedQuery pagedQuery = new PagedQuery(0, 50, Map.of(UserSortCriteria.EMAIL_ADDRESS, SortOrder.ASCENDING), Collections.emptyMap());
        PageImpl<UserAccount> userAccounts = new PageImpl<>(List.of(testUserAccountAdmin), PageRequest.of(0, 50), 2);

        // When
        PagedResult<UserAccountSummary> pagedResult = userAdapter.toUserAccountSummaries(pagedQuery, userAccounts);

        // Then
        List<UserAccountSummary> userSummaries = pagedResult.getContent();
        assertThat(userSummaries, notNullValue());
        assertThat(userSummaries.size(), is(1));

        UserAccountSummary userAccountSummary = userSummaries.get(0);
        assertThat(userAccountSummary.getEmailAddress(), is(testUserAccountAdmin.getEmailAddress()));
        assertThat(userAccountSummary.getCreatedDate(), is(testUserAccountAdmin.getCreated()));
        assertThat(userAccountSummary.getUserState(), is(testUserAccountAdmin.getUserState()));
        assertThat(userAccountSummary.isEmailAddressVerified(), is(testUserAccountAdmin.isEmailAddressVerified()));
    }

}
