package uk.nhs.digital.uec.api.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.MalformedURLException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HealthCheckControllerTest {

  @InjectMocks
  private HealthCheckController controller;

  @Test
  public void testGetVersion() throws MalformedURLException {
    String response = controller.getVersion();
    assertNotNull(response);
  }
}
