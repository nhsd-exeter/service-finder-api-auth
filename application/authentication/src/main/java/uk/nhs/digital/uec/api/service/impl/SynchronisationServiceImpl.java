package uk.nhs.digital.uec.api.service.impl;

import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import uk.nhs.digital.uec.api.domain.Role;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.service.CognitoService;
import uk.nhs.digital.uec.api.service.SynchronisationService;

import org.springframework.stereotype.Service;


/** Implementation of {@link SynchronisationService} */
@Service
@AllArgsConstructor
public class SynchronisationServiceImpl implements SynchronisationService {

  private CognitoService cognitoService;

  /** {@inheritDoc} */
  @Override
  public void synchroniseRoles(User user) {
    String emailAddress = user.getEmailAddress();
    Set<String> databaseRoleCodes =
        user.getRoles().stream().map(Role::getCode).collect(Collectors.toSet());
    Set<String> cognitoRoleCodes = cognitoService.retrieveRoles(emailAddress);
    Set<String> rolesToAddToCognito =
        databaseRoleCodes.stream()
            .filter(val -> !cognitoRoleCodes.contains(val))
            .collect(Collectors.toSet());
    Set<String> rolesToDeleteFromCognito =
        cognitoRoleCodes.stream()
            .filter(val -> !databaseRoleCodes.contains(val))
            .collect(Collectors.toSet());
    rolesToAddToCognito.forEach(roleCode -> cognitoService.addRole(roleCode, emailAddress));
    rolesToDeleteFromCognito.forEach(roleCode -> cognitoService.deleteRole(roleCode, emailAddress));
  }
}
