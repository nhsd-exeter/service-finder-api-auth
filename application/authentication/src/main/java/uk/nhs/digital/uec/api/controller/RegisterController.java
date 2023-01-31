package uk.nhs.digital.uec.api.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import uk.nhs.digital.uec.api.model.ApprovalStatus;
import uk.nhs.digital.uec.api.model.EmailVerification;
import uk.nhs.digital.uec.api.model.RegistrationCompleteAccount;
import uk.nhs.digital.uec.api.model.RegistrationRequestAccount;
import uk.nhs.digital.uec.api.model.UnconfirmedUserVerification;
import uk.nhs.digital.uec.api.service.RegistrationService;
import uk.nhs.digital.uec.api.service.UserService;

/**
 * Controller responsible for registering a new user.
 */
@RestController
@RequestMapping("/api/register")
@AllArgsConstructor
public class RegisterController {

    @Autowired
    private RegistrationService registrationService;

    private final UserService userService;

    @PostMapping("/requestAccount")
    public ResponseEntity requestAccount(@Valid @RequestBody RegistrationRequestAccount requestAccount) {

        registrationService.requestAccount(requestAccount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/completeRegistration")
    public ResponseEntity completeRegistration(@Valid @RequestBody RegistrationCompleteAccount completeAccount) {

        registrationService.completeRegistration(completeAccount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verifyAccount")
    public ResponseEntity<ApprovalStatus> verifyAccount(@Valid @RequestBody EmailVerification emailVerification) {

        ApprovalStatus approvalStatus = registrationService.verifyAccount(emailVerification);

        return ResponseEntity.ok(approvalStatus);
    }

    @PostMapping("/resendLink")
    public ResponseEntity<ApprovalStatus> resendConfirmationMessage(@Valid @RequestBody UnconfirmedUserVerification unconfirmedUserVerification) {
        ApprovalStatus approvalStatus =
            new ApprovalStatus(userService.
                resendConfirmationMessage(unconfirmedUserVerification.getEmailAddress()));
        return ResponseEntity.ok(approvalStatus);
    }

}
