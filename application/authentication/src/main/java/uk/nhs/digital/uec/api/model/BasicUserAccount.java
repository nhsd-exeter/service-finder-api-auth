package uk.nhs.digital.uec.api.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicUserAccount {

    @NotBlank(message = "email address must not be blank")
    @Email(message = "email address must be valid")
    private String emailAddress;
}
