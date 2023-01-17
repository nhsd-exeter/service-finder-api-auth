package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents the data required to reset a user's password
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordModel {

    @NotNull(message = "emailAddress must not be null")
    @Email(message = "emailAddress must be valid")
    private String emailAddress;

    @NotBlank(message = "code must not be blank")
    private String code;

    @NotNull(message = "password must not be null")
    @Size(min = 8, message = "password must be at least 8 characters in length")
    public String password;

}
