package uk.nhs.digital.uec.api.service.factory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.nhs.digital.uec.api.config.CognitoUserPoolProperties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;

/**
 * Tests for {@link CognitoClientRequestSecretHashFactory}
 */
@RunWith(MockitoJUnitRunner.class)
public class CognitoClientRequestSecretHashFactoryTest {

    private static final String CLIENT_ID = "client_id";

    private static final String CLIENT_SECRET = "client_secret";

    @Mock
    private CognitoUserPoolProperties cognitoUserPoolProperties;

    private CognitoClientRequestSecretHashFactory factory;

    @Before
    public void setUp() {
        given(cognitoUserPoolProperties.getClientId()).willReturn(CLIENT_ID);
        given(cognitoUserPoolProperties.getClientSecret()).willReturn(CLIENT_SECRET);
        factory = new CognitoClientRequestSecretHashFactory(cognitoUserPoolProperties);
    }

    @Test
    public void shouldCreate() {
        // Given
        String emailAddress = "test@example.com";

        // When
        String secretHash = factory.create(emailAddress);

        // Then
        assertThat(secretHash, is("zHxLpKr9YOhp8+3s/KImL8Z2cklFCKUd7nYl3mNJOBc="));
    }

}
