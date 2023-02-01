package uk.nhs.digital.uec.api.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import uk.nhs.digital.uec.api.model.PostcodeMapping;
import uk.nhs.digital.uec.api.service.impl.PostcodeAPIServiceImpl;

@ExtendWith(MockitoExtension.class)
public class PostcodeAPIServiceImplTest {

  MultiValueMap<String, String> headers;
  String postcode;
  String psmuri;
  PostcodeMapping mapping;
  @Mock private WebClient mockWebclient;

  @Mock private ExternalAPIAuthenticationService mockAuthenticationService;
  @InjectMocks private PostcodeAPIServiceImpl classUnderTest;

  @Before
  public void setup() {
    headers = new LinkedMultiValueMap<>();
    postcode = "EX8 8XE";
    psmuri = "/api/regions";
    mapping = new PostcodeMapping();
    MockitoAnnotations.initMocks(this);
    ReflectionTestUtils.setField(classUnderTest, "psmUri", "/api/regions");
  }

  // This needs to uncomment when RegionController available


  // @Test
  // public void getRegionDetails() {
  //   // given
  //   when(mockAuthenticationService.getAccessTokenHeader()).thenReturn(headers);
  //   WebClient.RequestHeadersUriSpec requestHeadersUriSpec =
  //       Mockito.mock(WebClient.RequestHeadersUriSpec.class);
  //   WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);

  //   when(mockWebclient.get()).thenReturn(requestHeadersUriSpec);
  //   when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersUriSpec);
  //   when(requestHeadersUriSpec.headers(any(Consumer.class))).thenReturn(requestHeadersUriSpec);
  //   when(requestHeadersUriSpec.accept(any(MediaType.class))).thenReturn(requestHeadersUriSpec);
  //   when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
  //   when(responseSpec.bodyToMono(PostcodeMapping.class)).thenReturn(Mono.just(mapping));

  //   // when
  //   PostcodeMapping result = classUnderTest.getRegionDetails(postcode);

  //   // then
  //   assertEquals(mapping.getPostcode(), result.getPostcode());
  //   assertEquals(mapping.getRegion(), result.getRegion());
  // }
}
