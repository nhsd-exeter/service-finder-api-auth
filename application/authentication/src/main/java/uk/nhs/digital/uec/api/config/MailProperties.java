package uk.nhs.digital.uec.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties("servicefinder.mail")
@Getter
@Setter
@Validated
public class MailProperties {

    @NotBlank
    private String senderName;

    @NotBlank
    private String senderEmail;

}
