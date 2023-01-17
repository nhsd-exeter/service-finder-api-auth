package uk.nhs.digital.uec.api.config;

import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Arrays;

/**
 * The Amazon Cognito configuration
 */
@Configuration
@Slf4j
public class CognitoConfiguration {

  private static final Regions DEFAULT_COGNITO_REGION = Regions.EU_WEST_2;
  @Autowired private Environment environment;


  @Bean
  public AWSCognitoIdentityProvider amazonCognitoIdentityClient() {
    if (isInLocalEnvironment()) {
      log.info("Running local AWS STS Authentication");
      return AWSCognitoIdentityProviderClientBuilder.standard()
        .withRegion(DEFAULT_COGNITO_REGION)
        .build();
    }

    return AWSCognitoIdentityProviderClientBuilder.standard()
      .withRegion(Regions.EU_WEST_2)
      .withCredentials(new InstanceProfileCredentialsProvider(false))
      .build();
  }

  private boolean isInLocalEnvironment() {
    return Arrays.stream(environment.getActiveProfiles())
      .anyMatch(env -> env.equals("local") || env.equals("test"));
  }
}
