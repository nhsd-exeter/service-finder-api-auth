package uk.nhs.digital.uec.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {

  @Value("${configuration.version}")
  private String apiVersion;

  @GetMapping("/home")
  public String getVersion() {
    return "This is the DoS Service Finder Authentication API. Version: " + apiVersion;
  }
}
