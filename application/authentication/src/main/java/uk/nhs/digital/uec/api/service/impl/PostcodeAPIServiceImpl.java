package uk.nhs.digital.uec.api.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.nhs.digital.uec.api.exception.InvalidParameterException;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.service.ExternalAPIAuthenticationService;
import uk.nhs.digital.uec.api.service.PostcodeAPIService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@Slf4j(topic = "User Management API - PostcodeAPIService")
public class PostcodeAPIServiceImpl implements PostcodeAPIService {

  @Autowired private ExternalAPIAuthenticationService authenticationService;

  @Autowired private WebClient postcodeAPI;

  @Value("${postcode.mapping.uri}")
  private String psmUri;

  @Override
  public PostcodeMapping getRegionDetails(String postcode) throws InvalidParameterException {
    PostcodeMapping mapping = new PostcodeMapping();
    mapping.setPostcode(postcode);
    log.info("Retrieving Region for postcode: {}", postcode);
    try {
      mapping =
          postcodeAPI
              .get()
              .uri(uriBuilder -> uriBuilder.path(psmUri).queryParam("postcode", postcode).build())
              .headers(
                  httpHeaders -> httpHeaders.putAll(authenticationService.getAccessTokenHeader()))
              .accept(MediaType.APPLICATION_JSON)
              .retrieve()
              .bodyToMono(PostcodeMapping.class)
              .block();

    } catch (WebClientResponseException e) {
      HttpStatus statusCode = e.getStatusCode();
      String errorResponse = e.getResponseBodyAsString();
      if (statusCode.value() == 400) {
        log.error("Error from Postcode Mapping API: {} . Param {}", errorResponse, postcode);
        throw new InvalidParameterException(errorResponse);
      }
    }
    log.info("Returning postcode mapping for {}", postcode);
    return mapping;
  }
}
