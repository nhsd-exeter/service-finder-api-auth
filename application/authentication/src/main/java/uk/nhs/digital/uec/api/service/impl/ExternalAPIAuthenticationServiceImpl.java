package uk.nhs.digital.uec.api.service.impl;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.interceptor.MDCInterceptor;
import uk.nhs.digital.uec.api.model.AuthToken;
import uk.nhs.digital.uec.api.model.Credential;
import uk.nhs.digital.uec.api.service.ExternalAPIAuthenticationService;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@Slf4j(topic = "User Management API - External API Authentication Service")
public class ExternalAPIAuthenticationServiceImpl implements ExternalAPIAuthenticationService {

  @Autowired WebClient authWebClient;

  @Value("${auth.login.uri}")
  private String loginUri;

  @Value("${postcode.mapping.user}")
  private String postcodeMappingUser;

  @Value("${postcode.mapping.password}")
  private String postcodeMappingPassword;

  @Override
  public MultiValueMap<String, String> getAccessTokenHeader() {
    AuthToken authToken = this.getAuthenticationToken();
    String token = Objects.nonNull(authToken) ? authToken.getAccessToken() : null;
    MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
    headers.add("Content-Type", "application/json");
    headers.add("Authorization", "Bearer " + token);
    headers.add(MDCInterceptor.CORRELATION_ID_HEADER_NAME, MDC.get(MDCInterceptor.CORRELATION_ID_HEADER_NAME));
    return headers;
  }

  private AuthToken getAuthenticationToken() {
    AuthToken authToken = null;
    Credential credential =
        Credential.builder()
            .emailAddress(postcodeMappingUser)
            .password(postcodeMappingPassword)
            .build();
    try {
      authToken =
          authWebClient
              .post()
              .uri(builder -> builder.path(loginUri).build())
              .body(BodyInserters.fromValue(credential))
              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
              .retrieve()
              .bodyToMono(AuthToken.class)
              .block();
    } catch (Exception e) {
      log.error("Error while connecting Authentication service: " + e.getMessage());
    }
    return authToken;
  }
}
