package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVerification {

    @NotBlank(message = "email address must not be blank")
    @Email(message = "email address must be valid")
    private String emailAddress;

    @NotNull(message = "code must not be null")
    @Size(min = 6, max = 6, message = "code must be 6 characters in length")
    private String code;

}
