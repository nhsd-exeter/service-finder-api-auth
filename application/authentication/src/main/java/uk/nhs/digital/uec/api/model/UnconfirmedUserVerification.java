package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnconfirmedUserVerification {

    @NotBlank(message = "email address must not be blank")
    @Email(message = "email address must be valid")
    private String emailAddress;

}
