package uk.nhs.digital.uec.api.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.nhs.digital.uec.api.utils.Utils.calculateSecretHash;

import org.junit.jupiter.api.Test;

public class UtilsTest {

  @Test
  public void testCalculateSecretHash() {
    String secretHash =
        calculateSecretHash("testUserPoolClientId", "testUserPoolId", "testUserPoolClientSecret");
    assertTrue(!secretHash.isEmpty());
  }
}
