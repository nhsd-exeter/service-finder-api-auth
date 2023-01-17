package uk.nhs.digital.uec.api.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.domain.UserBuilder;
import uk.nhs.digital.uec.api.service.impl.SynchronisationServiceImpl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class SynchronisationServiceImplTest {

    private static final String ROLE_ADMIN = "ADMIN";

    private static final String ROLE_APPROVER = "APPROVER";

    private static final String ROLE_SEARCH = "SEARCH";

    @Mock
    private CognitoService cognitoService;

    @InjectMocks
    private SynchronisationServiceImpl synchronisationServiceImpl;

    @Test
    public void synchroniseRolesShouldAddAndDeleteRolesAppropriately() {

        // Given
        String emailAddress = "e@com";
        User user = new UserBuilder()
                .withEmailAddress(emailAddress)
                .withRoles(new TreeSet(
                        Arrays.asList(new Role(1, ROLE_SEARCH, ROLE_SEARCH), new Role(2, ROLE_ADMIN, ROLE_ADMIN))))
                .createUser();
        Set<String> origCognitoRoles = new HashSet<>(Arrays.asList(ROLE_APPROVER, ROLE_ADMIN));
        given(cognitoService.retrieveRoles(emailAddress)).willReturn(origCognitoRoles);

        // When
        synchronisationServiceImpl.synchroniseRoles(user);

        // Then
        verify(cognitoService, times(1)).addRole(any(), any());
        verify(cognitoService, times(1)).deleteRole(any(), any());
        verify(cognitoService).addRole(ROLE_SEARCH, emailAddress);
        verify(cognitoService).deleteRole(ROLE_APPROVER, emailAddress);
    }

}
