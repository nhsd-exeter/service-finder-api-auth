package uk.nhs.digital.uec.api.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Represents the data required to login a user
 */
@Data
public class UserLogin {

    @Email(message = "email must be valid")
    private String emailAddress;

    @NotBlank(message = "password must not be blank")
    private String password;

}
