package uk.nhs.digital.uec.api.config;

import javax.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import uk.nhs.digital.uec.api.common.CookieTokenExtractor;
import uk.nhs.digital.uec.api.common.CorsConfig;
import uk.nhs.digital.uec.api.common.factory.CookieFactory;
import uk.nhs.digital.uec.api.common.factory.SslFactorySupplier;
import uk.nhs.digital.uec.api.common.filter.AccessTokenChecker;
import uk.nhs.digital.uec.api.common.filter.AccessTokenFilter;
import uk.nhs.digital.uec.api.common.filter.JwtDecoder;
import uk.nhs.digital.uec.api.common.filter.RefreshTokenFilter;
import uk.nhs.digital.uec.api.common.filter.RefreshTokenService;


/** Spring Security configuration for the User Management OAuth2 Resource Server */
@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

  @NotBlank
  @Value("${servicefinder.usermanagement.cookie.domain}")
  private String cookieDomain;

  @Value("${servicefinder.usermanagement.url}")
  private String userManagementUrl;

  @Value("${servicefinder.allowedorigins}")
  private String allowedOrigins;

  private final RestTemplateBuilder restTemplateBuilder;

  @Autowired
  public ResourceServerConfiguration(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplateBuilder = restTemplateBuilder;
  }

  @Bean
  public CookieFactory cookieFactory() {
    return new CookieFactory(cookieDomain);
  }

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new CorsConfig(allowedOrigins);
  }

  @Bean
  public RefreshTokenFilter refreshTokenFilter() {
    RestTemplate restTemplate =
        restTemplateBuilder.requestFactory(new SslFactorySupplier()).build();
    return new RefreshTokenFilter(
        new RefreshTokenService(restTemplate, userManagementUrl),
        new AccessTokenChecker(new JwtDecoder()),
        cookieFactory());
  }

  @Bean
  public AccessTokenFilter accessTokenFilter() {
    return new AccessTokenFilter();
  }

  @Override
  public void configure(ResourceServerSecurityConfigurer resources) {
    resources.tokenExtractor(new CookieTokenExtractor());
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    http.requestMatchers()
        .antMatchers(
            "/api/currentUser",
            "/api/users/*",
            "/api/users",
            "/api/sessionStatus",
            "/api/unregisteredUsers",
            "/api/users/csv",
            "/api/user/changes/*",
            "/api/createReferralReferenceCode",
            "/api/logContentDownload/*")
        .and()
        .addFilterBefore(refreshTokenFilter(), AbstractPreAuthenticatedProcessingFilter.class)
        .addFilterAfter(accessTokenFilter(), AbstractPreAuthenticatedProcessingFilter.class)
        .cors()
        .and()
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .csrf()
        .disable();
  }
}
