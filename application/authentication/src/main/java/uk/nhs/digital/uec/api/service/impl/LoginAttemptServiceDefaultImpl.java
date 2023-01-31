package uk.nhs.digital.uec.api.service.impl;

import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.common.assertion.CheckArgument;
import uk.nhs.digital.uec.api.domain.LoginAttempt;
import uk.nhs.digital.uec.api.repository.LoginAttemptRepository;
import uk.nhs.digital.uec.api.service.LoginAttemptService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


/** The {@link LoginAttemptService} default implementation. */
@Service
@Slf4j
public class LoginAttemptServiceDefaultImpl implements LoginAttemptService {

  private static final int FIRST_LOGIN_ATTEMPT = 1;

  private final LoginAttemptRepository loginAttemptRepository;

  private final int maxLoginAttempts;

  @Autowired
  public LoginAttemptServiceDefaultImpl(
      LoginAttemptRepository loginAttemptRepository,
      @Value("${servicefinder.maxLoginAttempts}") int maxLoginAttempts) {
    this.loginAttemptRepository = loginAttemptRepository;
    this.maxLoginAttempts = maxLoginAttempts;
  }

  /** {@inheritDoc} */
  @Override
  public void remove(String emailAddress) {
    CheckArgument.hasText(emailAddress, "emailAddress must have text");
    Optional<LoginAttempt> loginAttempt =
        loginAttemptRepository.findByEmailAddressIgnoreCase(emailAddress);
    loginAttempt.ifPresent(loginAttemptRepository::delete);
    log.info("Zeroing state of login attempts");
  }

  /** {@inheritDoc} */
  @Override
  public void add(String emailAddress) {
    CheckArgument.hasText(emailAddress, "emailAddress must have text");
    Optional<LoginAttempt> loginAttempt =
        loginAttemptRepository.findByEmailAddressIgnoreCase(emailAddress);
    if (loginAttempt.isPresent()) {
      LoginAttempt updatedLoginAttempt = loginAttempt.get();
      int currentAttempts = updatedLoginAttempt.getAttempts();
      currentAttempts++;
      updatedLoginAttempt.setAttempts(currentAttempts);
      log.info("User: {} login attempts {} ", emailAddress,currentAttempts);
    } else {
      loginAttemptRepository.saveAndFlush(new LoginAttempt(emailAddress, FIRST_LOGIN_ATTEMPT));
      log.info("User: {} login attempts {} ", emailAddress,FIRST_LOGIN_ATTEMPT);
    }
  }

  /** {@inheritDoc} */
  @Override
  public boolean isBlocked(String emailAddress) {
    CheckArgument.hasText(emailAddress, "emailAddress must have text");
    Optional<LoginAttempt> loginAttempt =
        loginAttemptRepository.findByEmailAddressIgnoreCase(emailAddress);
    log.info("User {} has reached maximum login attempts",emailAddress);
    return loginAttempt.isPresent() && loginAttempt.get().getAttempts() >= maxLoginAttempts;
  }
}
