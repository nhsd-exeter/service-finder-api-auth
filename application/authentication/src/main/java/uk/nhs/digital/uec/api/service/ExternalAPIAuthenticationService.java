package uk.nhs.digital.uec.api.service;

import org.springframework.util.MultiValueMap;

public interface ExternalAPIAuthenticationService {

  MultiValueMap<String, String> getAccessTokenHeader();
}
