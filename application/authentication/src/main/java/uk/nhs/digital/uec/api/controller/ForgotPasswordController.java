package uk.nhs.digital.uec.api.controller;

import lombok.AllArgsConstructor;
import uk.nhs.digital.uec.api.adapter.UserAdapter;
import uk.nhs.digital.uec.api.domain.User;
import uk.nhs.digital.uec.api.exception.ForgotPasswordBadEmailAddressException;
import uk.nhs.digital.uec.api.model.ForgotPasswordModel;
import uk.nhs.digital.uec.api.model.ResetPasswordModel;
import uk.nhs.digital.uec.api.service.LoginAttemptService;
import uk.nhs.digital.uec.api.service.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.cognitoidp.model.ExpiredCodeException;

import javax.validation.Valid;

/**
 * Controller responsible for handling forgot password/reset password requests.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/forgotPassword")
public class ForgotPasswordController {

    private final UserService userService;

    private final LoginAttemptService loginAttemptService;

    private final UserAdapter userAdapter;

    @PostMapping
    public ResponseEntity forgotPassword(@Valid @RequestBody ForgotPasswordModel forgotPasswordModel) {
        User user = userAdapter.toUser(forgotPasswordModel.getEmailAddress())
                                .orElseThrow(() -> new ForgotPasswordBadEmailAddressException("email address not found"));
        userService.forgotPassword(forgotPasswordModel, user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset")
    public ResponseEntity resetPassword(@Valid @RequestBody ResetPasswordModel resetPasswordModel) {
        String emailAddress = resetPasswordModel.getEmailAddress();
        User user = userAdapter.toUser(emailAddress).orElseThrow(() -> new ExpiredCodeException("code expired"));
        userService.resetPassword(resetPasswordModel, user);
        loginAttemptService.remove(resetPasswordModel.getEmailAddress());
        return ResponseEntity.ok().build();
    }

}
