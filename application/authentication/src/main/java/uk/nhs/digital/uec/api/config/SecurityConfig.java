package uk.nhs.digital.uec.api.config;

import static uk.nhs.digital.uec.api.utils.Constants.HEALTH_CHECK_READINESS_URL;
import static uk.nhs.digital.uec.api.utils.Constants.LOGIN_URL;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

  @Override
  protected void configure(HttpSecurity http) throws Exception {

    List<String> permitAllEndpointList = Arrays.asList(LOGIN_URL, HEALTH_CHECK_READINESS_URL);

    http.csrf()
        .disable()
        .authorizeRequests()
        .antMatchers(permitAllEndpointList.toArray(new String[permitAllEndpointList.size()]))
        .permitAll();
  }
}
