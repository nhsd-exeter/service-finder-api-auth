package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Represents the data required to request a new account.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequestAccount {

    @NotBlank(message = "email address must not be blank")
    @Email(message = "email address must be valid")
    private String emailAddress;

    @NotNull(message = "password must not be null")
    @Size(min = 8, message = "password must be at least 8 characters in length")
    private String password;

}
