package uk.nhs.digital.uec.api.config.test;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;

import uk.nhs.digital.uec.api.repository.UserRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * The Dummy Amazon Cognito configuration for integration tests
 */
@Configuration
@Profile({"dev-compose"})
public class TestCognitoConfiguration {

    @Bean
    @Primary
    public AWSCognitoIdentityProvider integrationTestAmazonCognitoIdentityClient(UserRepository userRepository) {
        return new TestAmazonCognitoIdentityClient(userRepository);
    }

}
