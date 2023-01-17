package uk.nhs.digital.uec.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * Cognito User Pool properties
 */
@Component
@ConfigurationProperties(prefix = "cognito-user-pool-properties")
@Validated
@Getter
public class CognitoUserPoolProperties {

    @NotBlank
    @Value("${userPool.clientId}")
    private String clientId;

    @NotBlank
    @Value("${userPool.clientSecret}")
    private String clientSecret;

    @NotBlank
    @Value("${userPool.id}")
    private String poolId;

}