package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Represents the data required to request a password reset link for a user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordModel {

    @NotNull(message = "emailAddress must not be null")
    @Email(message = "emailAddress must be valid")
    private String emailAddress;

}
