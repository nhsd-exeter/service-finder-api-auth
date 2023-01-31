package uk.nhs.digital.uec.api.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import javax.net.ssl.SSLException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
@Slf4j
public class WebClientConfig {

  @Bean
  public WebClient authWebClient(@Value("${auth.login.url}") String loginUrl) {
    return WebClient.builder()
        .baseUrl(loginUrl)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .clientConnector(new ReactorClientHttpConnector(getSecureHttpClient()))
        .build();
  }

  @Bean
  public WebClient postcodeAPI(@Value("${postcode.mapping.url}") String psmUrl) {
    return WebClient.builder()
        .baseUrl(psmUrl)
        .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
        .clientConnector(new ReactorClientHttpConnector(getSecureHttpClient()))
        .build();
  }

  private HttpClient getSecureHttpClient() {
    SslContext context;
    try {
      context =
          SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
    } catch (SSLException e) {
      log.info(
          "SSL Error while handshake between reporting and external service: " + e.getMessage());
      return HttpClient.create().wiretap(true);
    }
    return HttpClient.create().secure(t -> t.sslContext(context));
  }
}
