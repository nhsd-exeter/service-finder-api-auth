package uk.nhs.digital.uec.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

/**
 * Remote configuration for the User Management Service
 */
@Configuration
@ConfigurationProperties
//@PropertySource(factory = AwsSsmPropertySourceFactory.class, value = "", ignoreResourceNotFound = true)
@Profile("remote-config")
public class UserManagementRemoteConfiguration {

}
