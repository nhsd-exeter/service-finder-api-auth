package uk.nhs.digital.uec.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.nhs.digital.uec.api.common.filter.JwtDecoder;
import uk.nhs.digital.uec.api.service.factory.UserCsvFactory;
import uk.nhs.digital.uec.api.service.factory.UserQueryPageRequestFactory;

import uk.nhs.digital.uec.api.service.factory.SuperCsvFactory;

import java.time.Clock;

/**
 * The User Management Service configuration
 */
@Configuration
public class UserManagementConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public UserQueryPageRequestFactory userQueryPageRequestFactory() {
        return new UserQueryPageRequestFactory();
    }

    @Bean
    public UserCsvFactory userCsvFactory() {
        return new UserCsvFactory();
    }

    @Bean
    public SuperCsvFactory superCsvFactory() {
        return new SuperCsvFactory();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return new JwtDecoder();
    }

}
