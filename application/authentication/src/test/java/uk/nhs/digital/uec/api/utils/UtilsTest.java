package uk.nhs.digital.uec.api.utils;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.nhs.digital.uec.api.util.Utils.calculateSecretHash;

import org.junit.jupiter.api.Test;

public class UtilsTest {

  @Test
  public void CalculateSecretHashTest() {
    String secretHash = calculateSecretHash("testUserPoolClientId", "testUserPoolId", "testUserPoolClientSecret");
    assertTrue(!secretHash.isEmpty());
  }

}
