package uk.nhs.digital.uec.api.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RefreshTokens {

    @NotBlank(message = "refreshToken must not be blank")
    private String refreshToken;

    @NotBlank(message = "identityProviderId must not be blank")
    private String identityProviderId;

}
