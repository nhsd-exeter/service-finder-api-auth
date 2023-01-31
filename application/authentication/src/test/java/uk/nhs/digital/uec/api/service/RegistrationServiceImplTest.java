package uk.nhs.digital.uec.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.nhs.digital.uec.api.testsupport.UserRegistrationTestFactory.atestUserRegistrationCompleteAccount;

import java.util.Optional;

import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;

import uk.nhs.digital.uec.api.adapter.UserRegistrationAdapter;
import uk.nhs.digital.uec.api.domain.Credentials;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserAccount;
import uk.nhs.digital.uec.api.domain.UserBuilder;
import uk.nhs.digital.uec.api.domain.UserDetails;
import uk.nhs.digital.uec.api.exception.AccountAlreadyRegisteredException;
import uk.nhs.digital.uec.api.exception.ApprovedAccountAlreadyRegisteredException;
import uk.nhs.digital.uec.api.exception.InvalidRegistrationDetailsException;
import uk.nhs.digital.uec.api.exception.RegistrationEmailAddressNotRegisteredException;
import uk.nhs.digital.uec.api.exception.UserManagementExceptionCode;
import uk.nhs.digital.uec.api.model.ApprovalStatus;
import uk.nhs.digital.uec.api.model.EmailVerification;
import uk.nhs.digital.uec.api.model.RegistrationCompleteAccount;
import uk.nhs.digital.uec.api.model.RegistrationRequestAccount;
import uk.nhs.digital.uec.api.model.RegistrationResult;
import uk.nhs.digital.uec.api.repository.UserAccountRepository;
import uk.nhs.digital.uec.api.repository.UserDetailsRepository;
import uk.nhs.digital.uec.api.repository.UserRepository;
import uk.nhs.digital.uec.api.service.impl.RegistrationServiceImpl;
import uk.nhs.digital.uec.api.service.impl.UserServiceImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


/**
 * Tests for {@link UserServiceImpl}
 */
@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceImplTest {

    private static final String nhsEmailAddress = "actor@nhs.net";

    private static final String verificationCode = "12345";

    private static final String password = "password1234";

    private static final String uuid = "12345";

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private UserDetailsRepository userDetailsRepository;

    @Mock
    private CognitoService cognitoService;

    @Mock
    private UserRegistrationAdapter userRegistrationAdapter;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RegistrationServiceImpl registrationService;

    @Captor
    private ArgumentCaptor<Credentials> credentialsCaptor;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    @Captor
    private ArgumentCaptor<UserAccount> userAccountCaptor;

    @Captor
    private ArgumentCaptor<UserDetails> userDetailsCaptor;

    @Test
    public void registerNHSAccountSuccess(){

        RegistrationRequestAccount requestAccount =
            new RegistrationRequestAccount(nhsEmailAddress, password);

        when(cognitoService.register(any())).thenReturn(new RegistrationResult(uuid));
        when(userAccountRepository.saveAndFlush(any())).thenReturn(new UserAccount());
        when(userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(any())).thenReturn(Optional.empty());

        registrationService.requestAccount(requestAccount);

        verify(cognitoService, times(1)).register(credentialsCaptor.capture());
        verify(userAccountRepository, times(1)).saveAndFlush(userAccountCaptor.capture());
        assertEquals(credentialsCaptor.getValue().getEmailAddress(), nhsEmailAddress);
        assertEquals(credentialsCaptor.getValue().getPassword(), password);
    }

    @Test(expected = InvalidRegistrationDetailsException.class)
    public void registerExistingAccount(){

        RegistrationRequestAccount requestAccount =
            new RegistrationRequestAccount(nhsEmailAddress, password);

        User user = new UserBuilder().withEmailAddress(nhsEmailAddress).createUser();

        when(userRepository.findFirstByEmailAddressIgnoreCaseOrderByIdAsc(any())).thenReturn(Optional.of(user));

        registrationService.requestAccount(requestAccount);

        verify(cognitoService, times(1)).register(credentialsCaptor.capture());
        verify(userAccountRepository, times(1)).saveAndFlush(userAccountCaptor.capture());
        assertEquals(credentialsCaptor.getValue().getEmailAddress(), nhsEmailAddress);
        assertEquals(credentialsCaptor.getValue().getPassword(), password);
    }

    @Test
    public void completeRegistrationForApprovedDomain(){

        RegistrationCompleteAccount completeAccount = atestUserRegistrationCompleteAccount();
        User user = createMockedUser(completeAccount);

        when(userRegistrationAdapter.toUser(completeAccount))
            .thenReturn(user);
        when(userService.isEmailInApprovedDomain(eq(completeAccount.getEmailAddress())))
            .thenReturn(true);

        registrationService.completeRegistration(completeAccount);

        verify(userRegistrationAdapter, times(1)).toUser(completeAccount);
        verify(userService, times(1)).updatePropertiesForApprovedUser(user);
        verify(userRepository, times(1)).saveAndFlush(userCaptor.capture());
        verify(notificationService, times(1)).sendApprovalMessage(user);

        assertEquals(completeAccount.getName(), userCaptor.getValue().getName());
        assertNull(userCaptor.getValue().getUpdatedBy());
        assertNull(userCaptor.getValue().getUpdated());
    }

    @Test
    public void completeRegistrationForUnapprovedDomain(){

        RegistrationCompleteAccount completeAccount = atestUserRegistrationCompleteAccount();
        User user = createMockedUser(completeAccount);

        when(userRegistrationAdapter.toUser(completeAccount))
            .thenReturn(user);
        when(userService.isEmailInApprovedDomain(completeAccount.getEmailAddress()))
            .thenReturn(false);

        registrationService.completeRegistration(completeAccount);

        verify(userRegistrationAdapter, times(1)).toUser(completeAccount);
        verify(userService, never()).updatePropertiesForApprovedUser(user);
        verify(userRepository, times(1)).saveAndFlush(userCaptor.capture());

        assertEquals(completeAccount.getName(), userCaptor.getValue().getUserDetails().getName());
        assertNull(userCaptor.getValue().getUpdatedBy());
        assertNull(userCaptor.getValue().getUpdated());
    }

    @Test
    public void verifyApprovedAccountSuccess(){

        EmailVerification emailVerification =
            new EmailVerification(nhsEmailAddress, verificationCode);

        AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
        adminGetUserResult.setUserStatus("UNCONFIRMED");

        User user = new User();
        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress(nhsEmailAddress);
        userAccount.setEmailAddressVerified(false);
        user.setUserAccount(userAccount);
        Optional<User> optionalUser = Optional.of(user);

        when(cognitoService.getUserByEmailAddress(nhsEmailAddress)).thenReturn(adminGetUserResult);
        when(userService.isEmailInApprovedDomain(nhsEmailAddress)).thenReturn(true);
        when(userService.getByEmailAddress(nhsEmailAddress)).thenReturn(optionalUser);

        ApprovalStatus approvalStatus = registrationService.verifyAccount(emailVerification);

        verify(cognitoService, times(1)).verify(eq(emailVerification));
        assertTrue(approvalStatus.isApproved());
    }

    @Test
    public void verifyUnApprovedAccountSuccess(){

        EmailVerification emailVerification =
            new EmailVerification(nhsEmailAddress, verificationCode);

        AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
        adminGetUserResult.setUserStatus("UNCONFIRMED");

        User user = new User();
        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress(nhsEmailAddress);
        userAccount.setEmailAddressVerified(false);
        user.setUserAccount(userAccount);
        Optional<User> optionalUser = Optional.of(user);

        when(cognitoService.getUserByEmailAddress(nhsEmailAddress)).thenReturn(adminGetUserResult);
        when(userService.isEmailInApprovedDomain(nhsEmailAddress)).thenReturn(false);
        when(userService.getByEmailAddress(nhsEmailAddress)).thenReturn(optionalUser);

        ApprovalStatus approvalStatus = registrationService.verifyAccount(emailVerification);

        verify(cognitoService, times(1)).verify(eq(emailVerification));
        assertFalse(approvalStatus.isApproved());
    }

    @Test
    public void verifyAccountNotFound(){

        EmailVerification emailVerification =
            new EmailVerification(nhsEmailAddress, verificationCode);


        User user = new User();
        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress(nhsEmailAddress);
        userAccount.setEmailAddressVerified(false);
        user.setUserAccount(userAccount);
        Optional<User> optionalUser = Optional.of(user);

        when(cognitoService.getUserByEmailAddress(nhsEmailAddress))
            .thenThrow(new RegistrationEmailAddressNotRegisteredException(nhsEmailAddress));
        when(userService.getByEmailAddress(nhsEmailAddress)).thenReturn(optionalUser);

        try
        {
            registrationService.verifyAccount(emailVerification);
            fail();
        }
        catch (RegistrationEmailAddressNotRegisteredException e)
        {
            assertEquals(UserManagementExceptionCode.EMAIL_ADDRESS_NOT_REGISTERED.getCode(), e.getCode());
            assertEquals(UserManagementExceptionCode.EMAIL_ADDRESS_NOT_REGISTERED.getMessage() + nhsEmailAddress, e.getMessage());
        }

        verify(cognitoService, times(0)).verify(eq(emailVerification));

    }

    @Test
    public void verifyConfirmedPresentAndApproved(){

        EmailVerification emailVerification =
            new EmailVerification(nhsEmailAddress, verificationCode);

        AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
        adminGetUserResult.setUserStatus("CONFIRMED");

        User user = new User();
        Optional<User> optionalUser = Optional.of(user);

        when(cognitoService.getUserByEmailAddress(nhsEmailAddress)).thenReturn(adminGetUserResult);
        when(userService.isEmailInApprovedDomain(nhsEmailAddress)).thenReturn(true);
        when(userService.getByEmailAddress(nhsEmailAddress)).thenReturn(optionalUser);

        try{
            registrationService.verifyAccount(emailVerification);
            fail();
        }
        catch (ApprovedAccountAlreadyRegisteredException e)
        {
            assertEquals(UserManagementExceptionCode.APPROVED_ACCOUNT_ALREADY_REGISTERED.getCode(), e.getCode());
            assertEquals(UserManagementExceptionCode.APPROVED_ACCOUNT_ALREADY_REGISTERED.getMessage() + nhsEmailAddress, e.getMessage());
        }

        verify(cognitoService, times(0)).verify(eq(emailVerification));
    }

    @Test
    public void verifyConfirmedPresentAndUnApproved(){

        EmailVerification emailVerification =
            new EmailVerification(nhsEmailAddress, verificationCode);

        AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
        adminGetUserResult.setUserStatus("CONFIRMED");

        User user = new User();
        Optional<User> optionalUser = Optional.of(user);

        when(cognitoService.getUserByEmailAddress(nhsEmailAddress)).thenReturn(adminGetUserResult);
        when(userService.isEmailInApprovedDomain(nhsEmailAddress)).thenReturn(false);
        when(userService.getByEmailAddress(nhsEmailAddress)).thenReturn(optionalUser);

        try{
            registrationService.verifyAccount(emailVerification);
            fail();
        }
        catch (AccountAlreadyRegisteredException e)
        {
            assertEquals(UserManagementExceptionCode.ACCOUNT_ALREADY_REGISTERED.getCode(), e.getCode());
            assertEquals(UserManagementExceptionCode.ACCOUNT_ALREADY_REGISTERED.getMessage() + nhsEmailAddress, e.getMessage());
        }

        verify(cognitoService, times(0)).verify(eq(emailVerification));
    }

    @Test
    public void verifyConfirmedNotPresentAndApproved(){

        EmailVerification emailVerification =
            new EmailVerification(nhsEmailAddress, verificationCode);

        AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
        adminGetUserResult.setUserStatus("CONFIRMED");

        User user = new User();
        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress(nhsEmailAddress);
        userAccount.setEmailAddressVerified(false);
        user.setUserAccount(userAccount);
        Optional<User> optionalUser = Optional.of(user);

        when(cognitoService.getUserByEmailAddress(emailVerification.getEmailAddress())).thenReturn(adminGetUserResult);
        when(userService.isEmailInApprovedDomain(emailVerification.getEmailAddress())).thenReturn(true);
        when(userService.getByEmailAddress(emailVerification.getEmailAddress())).thenReturn(optionalUser);


        ApprovalStatus approvalStatus = registrationService.verifyAccount(emailVerification);

        verify(cognitoService, times(0)).verify(eq(emailVerification));
        assertTrue(approvalStatus.isApproved());
    }

    @Test
    public void verifyConfirmedNotPresentAndNotApproved(){

        EmailVerification emailVerification =
            new EmailVerification(nhsEmailAddress, verificationCode);

        AdminGetUserResult adminGetUserResult = new AdminGetUserResult();
        adminGetUserResult.setUserStatus("CONFIRMED");

        User user = new User();
        UserAccount userAccount = new UserAccount();
        userAccount.setEmailAddress(nhsEmailAddress);
        userAccount.setEmailAddressVerified(false);
        user.setUserAccount(userAccount);
        Optional<User> optionalUser = Optional.of(user);

        when(cognitoService.getUserByEmailAddress(nhsEmailAddress)).thenReturn(adminGetUserResult);
        when(userService.isEmailInApprovedDomain(nhsEmailAddress)).thenReturn(false);
        when(userService.getByEmailAddress(nhsEmailAddress)).thenReturn(optionalUser);


        ApprovalStatus approvalStatus = registrationService.verifyAccount(emailVerification);

        verify(cognitoService, times(0)).verify(eq(emailVerification));
        assertFalse(approvalStatus.isApproved());
    }

    private User createMockedUser(RegistrationCompleteAccount completeAccount){

        User user = new User();
        UserAccount userAccount = new UserAccount();
        UserDetails userDetails = new UserDetails();
        userAccount.setUserDetails(userDetails);
        userDetails.setUserAccount(userAccount);
        user.setUserAccount(userAccount);
        user.setUserDetails(userDetails);
        user.setEmailAddress(completeAccount.getEmailAddress());
        user.setName(completeAccount.getName());
        return user;
    }

}
