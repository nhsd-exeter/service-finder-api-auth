package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("local")
public class HealthCheckControllerTest {
    @LocalServerPort
    private int port;

    @Value("${local.host}")
    private String host;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testGetVersion() throws MalformedURLException {
        ResponseEntity<String> response = restTemplate.getForEntity(new URL(host + ":" + port + "/home").toString(),
                String.class);
        String message = response.getBody();
        String expected = "This is the DoS Service Finder Authentication API. Version: testVersionv1.0";
        assertEquals(expected, message);
    }
}
