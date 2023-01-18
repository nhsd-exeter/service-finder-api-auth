package uk.nhs.digital.uec.api.service;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static uk.nhs.digital.uec.api.common.UserManagementCommonConstants.COGNITO_GROUPS;
import static uk.nhs.digital.uec.api.domain.Role.ROLE_ADMIN;
import static uk.nhs.digital.uec.api.domain.Role.ROLE_SEARCH;
import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_APPROVED;
import static uk.nhs.digital.uec.api.domain.User.APPROVAL_STATUS_PENDING;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import uk.nhs.digital.uec.api.common.filter.JwtDecoder;
import uk.nhs.digital.uec.api.common.model.LoginResult;
import uk.nhs.digital.uec.api.model.RegistrationResult;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserAccountBuilder;
import uk.nhs.digital.uec.api.domain.UserBuilder;
import uk.nhs.digital.uec.api.domain.UserChange;
import uk.nhs.digital.uec.api.domain.UserDetails;
import uk.nhs.digital.uec.api.domain.UserFilterCriteria;
import uk.nhs.digital.uec.api.exception.AccountNotApprovedException;
import uk.nhs.digital.uec.api.exception.ApprovedUserDeletionException;
import uk.nhs.digital.uec.api.exception.EmailAddressNotRegisteredException;
import uk.nhs.digital.uec.api.exception.InvalidApprovalStatusChangeException;
import uk.nhs.digital.uec.api.exception.InvalidLoginException;
import uk.nhs.digital.uec.api.exception.LoginAttemptsExceeded;
import uk.nhs.digital.uec.api.exception.LoginFailedInactiveUserException;
import uk.nhs.digital.uec.api.exception.NoRolesException;
import uk.nhs.digital.uec.api.exception.UserIncompleteException;
import uk.nhs.digital.uec.api.exception.UserMissingPersistentDataException;
import uk.nhs.digital.uec.api.exception.UserNotFoundException;
import uk.nhs.digital.uec.api.repository.RoleRepository;
import uk.nhs.digital.uec.api.repository.UserRepository;
import uk.nhs.digital.uec.api.service.factory.UserQueryPageRequestFactory;
import uk.nhs.digital.uec.api.service.impl.UserServiceImpl;
import uk.nhs.digital.uec.api.testsupport.PagedQueryTestFactory;

/**
 * Tests for {@link UserServiceImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class UserServiceImplTest {

    private static final Role ADMIN_ROLE = new Role(1, "Admin", ROLE_ADMIN);

    private static final Role SEARCH_ROLE = new Role(2, "Search", ROLE_SEARCH);

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CognitoService cognitoService;

    @Mock
    private SynchronisationService synchronisationService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private AuditService auditService;

    @Mock
    private UserChangeService userChangeService;

    @Mock
    private UserQueryPageRequestFactory userQueryPageRequestFactory;

    @Mock
    private JwtDecoder jwtDecoder;

    @Mock
    private User userMock;

    @Mock
    private User actor;

    @Captor
    private ArgumentCaptor<TreeSet<Role>> rolesCaptor;

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private UserServiceImpl userService;

    private User user;

    private final Clock clock = Clock.systemUTC();

    private Credentials credentials;

    private final String emailAddress = "ee2ee.com";

    private final String actorEmailAddress = "actor@nhs.net";

    private final long userId = 1234;

    private final String subId = "12345";

    private final String newRejectionReason = "rejection_reason";

    private final Role role = new Role();

    private final String adminEmailAddress = "admin@email.com";

    private final String[] approvedDomains = { "approved.com", "*.approvedsuffix.com" };

    @Before
    public void setUp() {
        userService = new UserServiceImpl(userRepository, roleRepository, cognitoService, synchronisationService,
                notificationService, loginAttemptService, auditService, userChangeService,
                clock, userQueryPageRequestFactory, jwtDecoder, adminEmailAddress, approvedDomains);
        given(roleRepository.findByCode(ROLE_SEARCH)).willReturn(Optional.of(role));
        user = new User();
        user.setUserState("ACTIVE");
        user.setEmailAddressVerified(true);
        user.setUserDetails(new UserDetails());
        credentials = new Credentials();
        given(actor.getEmailAddress()).willReturn(actorEmailAddress);
    }

    @Test
    public void loginShouldReturnLoginResult() throws InvalidLoginException, NoRolesException {
        // Given
        String accessToken = "access_token";
        LoginResult loginResult = new LoginResult(accessToken, "refresh_token");
        String emailAddress = "test@example.com";
        Credentials loginCredentials = new Credentials(emailAddress, "password");
        given(cognitoService.authenticate(loginCredentials)).willReturn(loginResult);
        DecodedJWT decodedJwt = mock(DecodedJWT.class);
        given(jwtDecoder.decode(accessToken)).willReturn(decodedJwt);
        Claim groupsClaim = mock(Claim.class);
        given(decodedJwt.getClaim(COGNITO_GROUPS)).willReturn(groupsClaim);
        given(groupsClaim.asList(String.class)).willReturn(new ArrayList<>());
        user.setApprovalStatus(APPROVAL_STATUS_APPROVED);
        user.setUserState(User.USER_STATE_ACTIVE);

        // When
        LoginResult returnedLoginResult = userService.login(user, loginCredentials);

        // Then
        verify(loginAttemptService).remove(emailAddress);
        assertThat(returnedLoginResult, is(loginResult));
        assertThat(user.getLastLoggedIn(), is(notNullValue()));
    }

    @Test
    public void loginShouldFailGivenInvalidLoginException() throws InvalidLoginException, NoRolesException {
        // Given
        String emailAddress = "test@example.com";
        String password = "password";
        Credentials loginCredentials = new Credentials(emailAddress, password);
        given(cognitoService.authenticate(loginCredentials)).willThrow(InvalidLoginException.class);
        user.setApprovalStatus(APPROVAL_STATUS_APPROVED);
        user.setUserState(User.USER_STATE_ACTIVE);

        // Expectations
        exceptionRule.expect(InvalidLoginException.class);
        exceptionRule.expectMessage(String.format("Invalid credentials for [%s]", emailAddress));

        // When
        userService.login(user, loginCredentials);

        // Then
        verify(loginAttemptService).add(emailAddress);
        assertThat(user.getLastLoggedIn(), is(nullValue()));
    }

    @Test
    public void loginShouldThrowNoRolesExceptionGivenNoGroupsInAccessToken()
            throws InvalidLoginException, NoRolesException {
        // Given
        String accessToken = "access_token";
        LoginResult loginResult = new LoginResult(accessToken, "refresh_token");
        Credentials loginCredentials = new Credentials();
        given(cognitoService.authenticate(loginCredentials)).willReturn(loginResult);
        DecodedJWT decodedJwt = mock(DecodedJWT.class);
        given(jwtDecoder.decode(accessToken)).willReturn(decodedJwt);
        Claim groupsClaim = mock(Claim.class);
        given(decodedJwt.getClaim(COGNITO_GROUPS)).willReturn(groupsClaim);
        given(groupsClaim.asList(String.class)).willReturn(null);
        user.setApprovalStatus(User.APPROVAL_STATUS_APPROVED);
        user.setUserState(User.USER_STATE_ACTIVE);

        // Expectations
        exceptionRule.expect(NoRolesException.class);

        // When
        userService.login(user, loginCredentials);
    }

    @Test
    public void loginShouldThrowUserInactiveExceptionGivenInactiveUser()
            throws InvalidLoginException, NoRolesException {
        // Given
        String emailAddress = "test@example.com";
        String password = "password";
        Credentials loginCredentials = new Credentials(emailAddress, password);
        user.setUserState(User.USER_STATE_INACTIVE);

        // Expectations
        exceptionRule.expect(LoginFailedInactiveUserException.class);
        exceptionRule.expectMessage("User account is inactive");

        // When
        user.setUserState("INACTIVE");
        userService.login(user, loginCredentials);
    }

    @Test
    public void loginShouldThrowLoginAttemptsExceededGivenBlockedEmailAddress()
            throws InvalidLoginException, NoRolesException {
        // Given
        String emailAddress = "test@example.com";
        String password = "password";
        given(loginAttemptService.isBlocked(emailAddress)).willReturn(true);
        Credentials loginCredentials = new Credentials(emailAddress, password);

        // Expectations
        exceptionRule.expect(LoginAttemptsExceeded.class);
        exceptionRule.expectMessage("Login attempts exceeded");

        // When
        userService.login(user, loginCredentials);
    }

    @Test
    public void loginShouldThrowExceptionGivenUnverifiedEmail()
            throws UserIncompleteException, NoRolesException, InvalidLoginException {
        // Given
        String emailAddress = "unverified@example.com";
        String password = "password";
        given(loginAttemptService.isBlocked(emailAddress)).willReturn(false);
        Credentials loginCredentials = new Credentials(emailAddress, password);

        // Expectations
        exceptionRule.expect(UserIncompleteException.class);
        exceptionRule.expectMessage("User has not completed registration");

        // When
        user.setEmailAddressVerified(false);

        userService.login(user, loginCredentials);
    }

    @Test
    public void loginShouldThrowExceptionGivenUnapprovedEmail()
            throws UserIncompleteException, NoRolesException, InvalidLoginException {
        // Given
        String emailAddress = "unapproved@example.com";
        String password = "password";
        given(loginAttemptService.isBlocked(emailAddress)).willReturn(false);
        Credentials loginCredentials = new Credentials(emailAddress, password);

        // Expectations
        exceptionRule.expect(AccountNotApprovedException.class);
        exceptionRule.expectMessage("User account is not approved");

        // When
        user.setEmailAddressVerified(true);
        user.setApprovalStatus("PENDING");

        userService.login(user, loginCredentials);
    }

    @Test
    public void loginShouldThrowExceptionGivenIncompleteReg()
            throws UserIncompleteException, NoRolesException, InvalidLoginException {
        // Given
        String emailAddress = "incomplete@example.com";
        String password = "password";
        given(loginAttemptService.isBlocked(emailAddress)).willReturn(false);
        Credentials loginCredentials = new Credentials(emailAddress, password);

        // Expectations
        exceptionRule.expect(UserIncompleteException.class);
        exceptionRule.expectMessage("User has not completed registration");

        // When
        user.setUserDetails(null);

        userService.login(user, loginCredentials);
    }

    @Test
    public void loginWithRefreshTokenShouldInvokeLoginService() {

        // Given
        String accessToken = "access_token";
        String refreshToken = "refresh_token";
        String identityProviderId = "id";
        LoginResult loginResult = new LoginResult(accessToken, refreshToken);
        given(cognitoService.authenticateWithRefreshToken(refreshToken, identityProviderId)).willReturn(loginResult);

        // When
        LoginResult returnedLoginResult = userService.loginWithRefreshToken(refreshToken, identityProviderId);

        // Then
        assertThat(returnedLoginResult, is(loginResult));
    }

    @Test
    public void shouldFindById() {
        // Given
        long userId = 1;
        User user = new UserBuilder().createUser();
        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        // When
        userService.getById(userId);

        // Then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findById(captor.capture());
        assertThat(captor.getValue(), is(userId));
    }

    @Test
    public void shouldThrowUserNotFoundException() {
        // Given
        long userId = 1;
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        // Expectations
        exceptionRule.expect(UserNotFoundException.class);
        exceptionRule.expectMessage("User not found");

        // When
        userService.getById(userId);

        // Then
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(userRepository).findById(captor.capture());
        assertThat(captor.getValue(), is(userId));
    }

    @Test
    public void shouldFindByIdentityProviderId() {
        // Given
        String identityProviderId = "123456";
        User user = new UserBuilder().createUser();
        given(userRepository.findByIdentityProviderId(identityProviderId)).willReturn(Optional.of(user));

        // When
        userService.getByIdentityProviderId(identityProviderId);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByIdentityProviderId(captor.capture());
        assertThat(captor.getValue(), is(identityProviderId));
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionGivenNullIdentityProviderId() {
        // Given
        String identityProviderId = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("identityProviderId must not be blank");

        // When
        userService.getByIdentityProviderId(identityProviderId);
    }

    @Test
    public void shouldThrowIllegalArgumentExceptionGivenEmptyIdentityProviderId() {
        // Given
        String identityProviderId = "";

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("identityProviderId must not be blank");

        // When
        userService.getByIdentityProviderId(identityProviderId);
    }

    @Test
    public void getByIdentityProviderIdShouldThrowUserMissingPersistentDataException() {
        // Given
        String identityProviderId = "123456";
        given(userRepository.findByIdentityProviderId(identityProviderId)).willReturn(Optional.empty());

        // Expectations
        exceptionRule.expect(UserMissingPersistentDataException.class);
        exceptionRule.expectMessage("User is missing persistent data");

        // When
        userService.getByIdentityProviderId(identityProviderId);

        // Then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(userRepository).findByIdentityProviderId(captor.capture());
        assertThat(captor.getValue(), is(identityProviderId));
    }

    @Test
    public void shouldFindUsers() {
        // Given
        PagedQuery query = PagedQueryTestFactory.apagedQuery(5);
        PageRequest pageRequest = userQueryPageRequestFactory.createPageRequest(query);
        Page<User> users = new PageImpl<>(Collections.singletonList(new UserBuilder().createUser()));
        given(userRepository.findByQuery(eq(pageRequest), anyMap())).willReturn(users);

        // When
        Page<User> pagedResult = userService.findUsers(query);

        // Then
        assertThat(pagedResult.getTotalElements(), is(1L));
        assertThat(pagedResult.getNumberOfElements(), is(1));
        assertThat(pagedResult.getTotalPages(), is(1));
        List<User> content = pagedResult.getContent();
        assertThat(content.size(), is(1));
        User user = content.get(0);
        assertThat(user.isEmailAddressVerified(), is(false));
    }

    @Test
    public void shouldFindUsersWithoutPaging() {
        // Given
        List<User> users = Collections.singletonList(new UserBuilder().createUser());
        Map<UserFilterCriteria, String> filterCriteria = new LinkedHashMap<>();
        given(userRepository.findByQuery(anyMap())).willReturn(users);

        // When
        List<User> userResult = userService.findUsers(filterCriteria);

        // Then
        assertThat(userResult.size(), is(1));
        User user = userResult.get(0);
        assertThat(user.isEmailAddressVerified(), is(false));
    }

    @Test
    public void shouldFailToConstructGivenNullQuery() {
        // Given
        PagedQuery query = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("query must not be null");

        // When
        userService.findUsers(query);
    }

    @Test
    public void updateShouldUpdatePersistentUserFromRequestGivenNoStatusChangeAndNonAdminUser() {
        // Given
        String approvalStatus = APPROVAL_STATUS_PENDING;
        User editedUser = new UserBuilder().withUserState(User.USER_STATE_ACTIVE).createUser();
        SortedSet<Role> roles = new TreeSet<>();
        editedUser.setId(userId);
        editedUser.setApprovalStatus(approvalStatus);
        editedUser.setRoles(roles);
        User persistentUser = mock(User.class);
        given(persistentUser.getApprovalStatus()).willReturn(approvalStatus);
        given(persistentUser.getUserState()).willReturn(User.USER_STATE_ACTIVE);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(anyLong())).willReturn(persistentUser);
        given(actor.isAdmin()).willReturn(false);

        // When
        User returnedUser = userService.update(editedUser, actor);

        // Then
        assertThat(returnedUser, is(persistentUser));
        verify(userRepository).getOne(userId);
        verify(persistentUser).updateDetailsFromUser(editedUser, actor);
        verify(synchronisationService).synchroniseRoles(persistentUser);
        verify(persistentUser, never()).processApprovalStatusChange(any(), any(), any(), any(), any());
        verify(persistentUser, never()).addRole(any());
    }

    @Test
    public void updateShouldUpdatePersistentUserFromRequestGivenApprovalStatusChange() {
        // Given
        String newApprovalStatus = APPROVAL_STATUS_APPROVED;
        User editedUser = new UserBuilder().withUserState(User.USER_STATE_ACTIVE).createUser();
        editedUser.setId(userId);
        editedUser.setApprovalStatus(newApprovalStatus);
        editedUser.setRejectionReason(newRejectionReason);
        editedUser.setRoles(new TreeSet<>());
        User persistentUser = mock(User.class);
        given(persistentUser.getApprovalStatus()).willReturn(APPROVAL_STATUS_PENDING, APPROVAL_STATUS_APPROVED);
        given(persistentUser.getUserState()).willReturn(User.USER_STATE_ACTIVE);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(anyLong())).willReturn(persistentUser);
        given(roleRepository.findByCode(ROLE_SEARCH)).willReturn(Optional.of(SEARCH_ROLE));

        // When
        User returnedUser = userService.update(editedUser, actor);

        // Then
        verify(persistentUser).setRoles(rolesCaptor.capture());
        SortedSet<Role> setRoles = rolesCaptor.getValue();
        assertThat(setRoles, contains(SEARCH_ROLE));
        assertThat(returnedUser, is(persistentUser));
        verify(userRepository).getOne(userId);
        verify(persistentUser).updateDetailsFromUser(editedUser, actor);
        verify(synchronisationService).synchroniseRoles(persistentUser);
        verify(persistentUser).processApprovalStatusChange(newApprovalStatus, newRejectionReason,
                User.USER_STATE_ACTIVE, actor, clock);
    }

    @Test
    public void updateShouldLeaveUserUnchangedGivenUserNotVerified() {
        // Given
        User editedUser = new UserBuilder().createUser();
        editedUser.setId(userId);
        editedUser.setApprovalStatus(APPROVAL_STATUS_APPROVED);
        editedUser.setUserState(User.USER_STATE_ACTIVE);
        editedUser.setRejectionReason(newRejectionReason);
        editedUser.setRoles(new TreeSet<>());
        User persistentUser = mock(User.class);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.FALSE);
        given(userRepository.getOne(anyLong())).willReturn(persistentUser);

        // When
        userService.update(editedUser, actor);

        // Then
        verify(persistentUser, never()).updateDetailsFromUser(any(), any());
        verify(persistentUser, never()).processApprovalStatusChange(any(), any(), any(), any(), any());
        verify(synchronisationService, never()).synchroniseRoles(any());
    }

    @Test
    public void updateShouldUpdatePersistentUserFromRequestGivenNoStatusChangeAndAdminUser() {
        // Given
        long userId = 1234;
        String newRejectionReason = "rejection_reason";
        SortedSet<Role> roles = new TreeSet<>();
        User editedUser = new UserBuilder().createUser();
        editedUser.setId(userId);
        editedUser.setRoles(roles);
        editedUser.setApprovalStatus(APPROVAL_STATUS_APPROVED);
        editedUser.setRejectionReason(newRejectionReason);
        editedUser.setEmailAddress(emailAddress);
        editedUser.setUserState(User.USER_STATE_ACTIVE);
        User persistentUser = mock(User.class);
        given(persistentUser.getApprovalStatus()).willReturn(APPROVAL_STATUS_APPROVED);
        given(persistentUser.getUserState()).willReturn(User.USER_STATE_ACTIVE);
        given(persistentUser.getEmailAddress()).willReturn(emailAddress);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(anyLong())).willReturn(persistentUser);
        given(roleRepository.findByCode(ROLE_SEARCH)).willReturn(Optional.of(SEARCH_ROLE));
        given(actor.isAdmin()).willReturn(true);

        // When
        User returnedUser = userService.update(editedUser, actor);

        // Then
        verify(persistentUser).setRoles(rolesCaptor.capture());
        SortedSet<Role> setRoles = rolesCaptor.getValue();
        assertThat(setRoles, hasItem(SEARCH_ROLE));
        assertThat(returnedUser, is(persistentUser));
        verify(userRepository).getOne(userId);
        verify(persistentUser).updateDetailsFromUser(editedUser, actor);
        verify(synchronisationService).synchroniseRoles(persistentUser);
        verify(persistentUser, never()).processApprovalStatusChange(any(), any(), any(), any(), any());
        verify(notificationService, never()).sendApprovalMessage(persistentUser);
    }

    @Test
    public void updateShouldNotRemoveAdminRoleIfEditingThemselves() {
        // Given
        String approvalStatus = APPROVAL_STATUS_PENDING;
        User editedUser = new UserBuilder().withUserState(User.USER_STATE_ACTIVE).createUser();
        SortedSet<Role> roles = new TreeSet<>();
        roles.add(SEARCH_ROLE);
        editedUser.setId(userId);
        editedUser.setApprovalStatus(approvalStatus);
        editedUser.setRoles(roles);
        editedUser.setEmailAddress(emailAddress);
        User persistentUser = mock(User.class);
        given(persistentUser.getApprovalStatus()).willReturn(approvalStatus);
        given(persistentUser.getUserState()).willReturn(User.USER_STATE_ACTIVE);
        given(persistentUser.getEmailAddress()).willReturn(emailAddress);
        given(persistentUser.isAdmin()).willReturn(Boolean.TRUE);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(anyLong())).willReturn(persistentUser);
        given(actor.isAdmin()).willReturn(true);
        given(actor.getEmailAddress()).willReturn(emailAddress);
        given(roleRepository.findByCode(ROLE_ADMIN)).willReturn(Optional.of(ADMIN_ROLE));

        // When
        User returnedUser = userService.update(editedUser, actor);

        // Then
        verify(persistentUser).setRoles(rolesCaptor.capture());
        SortedSet<Role> setRoles = rolesCaptor.getValue();
        assertThat(setRoles, hasItem(ADMIN_ROLE));
        assertThat(returnedUser, is(persistentUser));
        verify(userRepository).getOne(userId);
        verify(persistentUser).updateDetailsFromUser(editedUser, actor);
        verify(synchronisationService).synchroniseRoles(persistentUser);
    }

    @Test
    public void updateShouldNotRemoveAdminRoleIfEditingTheMainAdmin() {
        // Given
        String approvalStatus = APPROVAL_STATUS_PENDING;
        User editedUser = new UserBuilder().withUserState(User.USER_STATE_ACTIVE).createUser();
        SortedSet<Role> roles = new TreeSet<>();
        roles.add(SEARCH_ROLE);
        editedUser.setId(userId);
        editedUser.setApprovalStatus(approvalStatus);
        editedUser.setRoles(roles);
        editedUser.setEmailAddress(adminEmailAddress);
        User persistentUser = mock(User.class);
        given(persistentUser.getEmailAddress()).willReturn(adminEmailAddress);
        given(persistentUser.getUserState()).willReturn(User.USER_STATE_ACTIVE);
        given(persistentUser.isAdmin()).willReturn(true);
        given(persistentUser.getApprovalStatus()).willReturn(approvalStatus);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(anyLong())).willReturn(persistentUser);
        given(actor.isAdmin()).willReturn(true);
        given(roleRepository.findByCode(ROLE_ADMIN)).willReturn(Optional.of(ADMIN_ROLE));

        // When
        User returnedUser = userService.update(editedUser, actor);

        // Then
        verify(persistentUser).setRoles(rolesCaptor.capture());
        SortedSet<Role> setRoles = rolesCaptor.getValue();
        assertThat(setRoles, hasItem(ADMIN_ROLE));
        assertThat(returnedUser, is(persistentUser));
        verify(userRepository).getOne(userId);
        verify(persistentUser).updateDetailsFromUser(editedUser, actor);
        verify(synchronisationService).synchroniseRoles(persistentUser);
    }

    @Test(expected = InvalidApprovalStatusChangeException.class)
    public void updateShouldNotUpdateIfUserIsRejectedAndUpdateIsStateActive() {
        // Given
        String approvalStatus = User.APPROVAL_STATUS_REJECTED;
        User editedUser = new UserBuilder().createUser();
        SortedSet<Role> roles = new TreeSet<>();
        roles.add(SEARCH_ROLE);
        editedUser.setApprovalStatus(approvalStatus);
        editedUser.setId(userId);
        editedUser.setRoles(roles);
        editedUser.setEmailAddress(adminEmailAddress);
        editedUser.setUserState(User.USER_STATE_ACTIVE);
        User persistentUser = mock(User.class);
        given(persistentUser.getUserState()).willReturn(User.USER_STATE_INACTIVE);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(anyLong())).willReturn(persistentUser);

        // When
        userService.update(editedUser, actor);

        // Then
        exceptionRule.expect(InvalidApprovalStatusChangeException.class);
        exceptionRule.expectMessage("Cannot change user state to INACTIVE while user is REJECTED");
    }

    @Test
    public void updateShouldUpdateIfUserIsPendingApprovedAndUpdatedToRejected() {
        // Given
        User editedUser = new UserBuilder().createUser();
        SortedSet<Role> roles = new TreeSet<>();
        roles.add(SEARCH_ROLE);
        editedUser.setApprovalStatus(User.APPROVAL_STATUS_REJECTED);
        editedUser.setRejectionReason("rejectionReason");
        editedUser.setEmailAddress(adminEmailAddress);
        editedUser.setId(userId);
        editedUser.setRoles(roles);
        editedUser.setUserState(User.USER_STATE_ACTIVE);
        editedUser.setEmailAddressVerified(true);
        User persistentUser = mock(User.class);
        given(persistentUser.getApprovalStatus()).willReturn(User.APPROVAL_STATUS_PENDING);
        given(persistentUser.getEmailAddress()).willReturn(adminEmailAddress);
        given(persistentUser.getUserState()).willReturn(User.USER_STATE_ACTIVE);
        given(persistentUser.isAdmin()).willReturn(true);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(anyLong())).willReturn(persistentUser);
        given(actor.isAdmin()).willReturn(true);
        given(roleRepository.findByCode(ROLE_ADMIN)).willReturn(Optional.of(ADMIN_ROLE));

        // When
        User returnedUser = userService.update(editedUser, actor);

        assertThat(returnedUser, is(persistentUser));
        verify(userRepository).getOne(userId);
        verify(persistentUser).updateDetailsFromUser(editedUser, actor);
        verify(synchronisationService).synchroniseRoles(persistentUser);
    }

    @Test
    public void shouldResendUnapprovedConfirmationMessage() {

        // given
        User persistentUser = mock(User.class);
        given(persistentUser.getEmailAddress()).willReturn("test@example.com");
        given(userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc("test@example.com"))
                .willReturn(Optional.of(persistentUser));

        // When
        boolean approved = userService.resendConfirmationMessage("test@example.com");

        // Then
        then(cognitoService)
                .should()
                .resendConfirmationMessage("test@example.com");
        assertThat(approved, is(false));
    }

    @Test
    public void shouldErrorResendUnapprovedConfirmationMessageIfEmailIsNotPresent() {
        // given
        given(userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc("test@example.com"))
                .willReturn(Optional.empty());
        exceptionRule.expect(EmailAddressNotRegisteredException.class);

        // When
        userService.resendConfirmationMessage("test@example.com");

    }

    @Test
    public void shouldResendApprovedConfirmationMessage() {

        // given
        User persistentUser = mock(User.class);
        given(persistentUser.getEmailAddress()).willReturn("test@approved.com");
        given(userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc("test@approved.com"))
                .willReturn(Optional.of(persistentUser));

        // When
        boolean approved = userService.resendConfirmationMessage("test@approved.com");

        // Then
        then(cognitoService)
                .should()
                .resendConfirmationMessage("test@approved.com");
        assertThat(approved, is(true));
    }

    @Test
    public void shouldFailToResendConfirmationMessageGivenNullUser() {

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("emailAddress must not be null");

        // When
        userService.resendConfirmationMessage(null);
    }

    @Test
    public void shouldResendMessageToVerifiedAndUnapproveUser() {

        // given
        User persistentUser = mock(User.class);
        given(persistentUser.isEmailAddressVerified()).willReturn(true);
        given(persistentUser.isApproved()).willReturn(false);
        given(userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc("test@example.com"))
                .willReturn(Optional.of(persistentUser));

        // When
        boolean approved = userService.resendConfirmationMessage("test@example.com");

        // Then
        then(notificationService)
                .should()
                .sendMessageForRegistrationPartII(persistentUser);
        assertThat(approved, is(false));
    }

    @Test
    public void shouldDeleteIfIsNotApproved() {
        // Given
        long userForDeletionId = 1234L;
        User userForDeletion = mock(User.class);
        given(userForDeletion.getId()).willReturn(userForDeletionId);
        User persistentUser = mock(User.class);
        given(userRepository.getOne(userForDeletionId)).willReturn(persistentUser);

        // When
        userService.delete(userForDeletion, actor);

        // Then
        then(userRepository).should().delete(persistentUser);
        then(auditService).should().recordDeletion(actor, persistentUser);
        then(cognitoService).should().deleteUser(persistentUser);
    }

    @Test
    public void shouldDeleteIfIsApprovedAndUnVerified() {
        // Given
        long userForDeletionId = 1234L;
        User userForDeletion = mock(User.class);
        given(userForDeletion.getId()).willReturn(userForDeletionId);
        User persistentUser = mock(User.class);
        given(persistentUser.isEmailAddressVerified()).willReturn(false);
        given(userRepository.getOne(userForDeletionId)).willReturn(persistentUser);

        // When
        userService.delete(userForDeletion, actor);

        // Then
        then(userRepository).should().delete(persistentUser);
        then(auditService).should().recordDeletion(actor, persistentUser);
        then(cognitoService).should().deleteUser(persistentUser);
    }

    @Test
    public void shouldDeleteIfIsNotInCognito() {
        // Given
        long userForDeletionId = 1234L;
        User userForDeletion = mock(User.class);
        given(userForDeletion.getId()).willReturn(userForDeletionId);
        User persistentUser = mock(User.class);
        given(persistentUser.isEmailAddressVerified()).willReturn(false);
        given(userRepository.getOne(userForDeletionId)).willReturn(persistentUser);
        doThrow(com.amazonaws.services.cognitoidp.model.UserNotFoundException.class).when(cognitoService)
                .deleteUser(any(User.class));

        // When
        userService.delete(userForDeletion, actor);

        // Then
        then(userRepository).should().delete(persistentUser);
        then(auditService).should().recordDeletion(actor, persistentUser);
        then(cognitoService).should().deleteUser(persistentUser);
    }

    @Test
    public void shouldDeleteIfIsNotApprovedAndVerified() {
        // Given
        long userForDeletionId = 1234L;
        User userForDeletion = mock(User.class);
        UserChange userChangeEntry = mock(UserChange.class);
        ArrayList<UserChange> userChanges = new ArrayList<UserChange>();
        userChanges.add(userChangeEntry);
        given(userForDeletion.getId()).willReturn(userForDeletionId);
        User persistentUser = mock(User.class);
        given(persistentUser.isApproved()).willReturn(false);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(userForDeletionId)).willReturn(persistentUser);
        given(userChangeService.getAllRecordsByUser(userForDeletionId)).willReturn(userChanges);

        // When
        userService.delete(userForDeletion, actor);

        // Then
        then(userChangeService).should().deleteAllRecordsByUser(userId);
        then(userRepository).should().delete(persistentUser);
        then(auditService).should().recordDeletion(actor, persistentUser);
        then(cognitoService).should().deleteUser(persistentUser);
    }

    @Test
    public void deleteShouldFailGivenUserForDeletionIsApprovedAndVerified() {
        // Given
        long userForDeletionId = 1234L;
        User userForDeletion = mock(User.class);
        given(userForDeletion.getId()).willReturn(userForDeletionId);
        User persistentUser = mock(User.class);
        given(persistentUser.isApproved()).willReturn(true);
        given(persistentUser.isEmailAddressVerified()).willReturn(Boolean.TRUE);
        given(userRepository.getOne(userForDeletionId)).willReturn(persistentUser);

        // Expectations
        exceptionRule.expect(ApprovedUserDeletionException.class);

        // When
        userService.delete(userForDeletion, actor);
    }

    @Test
    public void deleteShouldThrowIllegalArgumentExceptionGivenUserForDeletionIsNull() {
        // Given
        User userForDeletion = null;
        User actor = mock(User.class);

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("userForDeletion must not be null");

        // When
        userService.delete(userForDeletion, actor);
    }

    @Test
    public void deleteShouldThrowIllegalArgumentExceptionGivenActorIsNull() {
        // Given
        User userForDeletion = mock(User.class);
        User actor = null;

        // Expectations
        exceptionRule.expect(IllegalArgumentException.class);
        exceptionRule.expectMessage("actor must not be null");

        // When
        userService.delete(userForDeletion, actor);
    }

    @Test
    public void shouldFindUnregisteredUsers() {
        // Given
        PagedQuery query = PagedQueryTestFactory.apagedQuery(5);
        PageRequest pageRequest = userQueryPageRequestFactory.createPageRequest(query);
        Page<UserAccount> userAccount = new PageImpl<>(Collections.singletonList(new UserAccountBuilder().build()));
        given(userRepository.findUnregisteredUserAccounts(eq(pageRequest), anyMap())).willReturn(userAccount);

        // When
        Page<UserAccount> pagedResult = userService.findUnregisteredUsers(query);

        // Then
        assertThat(pagedResult.getTotalElements(), is(1L));
        assertThat(pagedResult.getNumberOfElements(), is(1));
        assertThat(pagedResult.getTotalPages(), is(1));
        List<UserAccount> content = pagedResult.getContent();
        assertThat(content.size(), is(1));
        UserAccount userAccountResponse = content.get(0);
        assertThat(userAccountResponse.isEmailAddressVerified(), is(false));
    }

}
