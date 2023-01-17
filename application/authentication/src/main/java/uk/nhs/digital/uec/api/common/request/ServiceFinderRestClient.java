package uk.nhs.digital.uec.api.common.request;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import uk.nhs.digital.uec.api.common.factory.SslFactorySupplier;
import uk.nhs.digital.uec.api.common.util.CookieUtil;


/**
 * Class to encapsulate logic to call endpoints of the service finder from within the service finder
 * application.
 *
 * Specially, the class deals with providing the request call with authentication components so the
 * code calling the endpoint does not have to worry about it.
 */
@Service
public class ServiceFinderRestClient {

    private RestTemplateBuilder restTemplateBuilder;

    /**
    * Service finder request scoped object to hold request related details such as authentication
    * tokens.
    */
    private ServiceFinderRequest serviceFinderRequest;

    @Autowired
    public ServiceFinderRestClient(final RestTemplateBuilder restTemplateBuilder,
        final ServiceFinderRequest serviceFinderRequest)
    {
        this.restTemplateBuilder = restTemplateBuilder;
        this.serviceFinderRequest = serviceFinderRequest;
    }

    /**
     * Sends the request to the specified endpoint with authentication tokens.
     *
     * @param endpointUrl the url to send the request to.
     * @param httpMethod the method in which to invoke the endpoint {@link HttpMethod}.
     * @param body the request body.
     */
    public void sendRequestWithAuthentication(final String endpointUrl,
        final HttpMethod httpMethod,
        final Map<String, String> body)
    {
        final HttpHeaders requestHeaders =
            CookieUtil.addAuthTokensToCookieHeader(serviceFinderRequest.getAuthenticationToken());
        final HttpEntity requestEntity = new HttpEntity(body, requestHeaders);

        restTemplateBuilder
            .requestFactory(new SslFactorySupplier())
            .build()
            .exchange(endpointUrl, httpMethod, requestEntity, ResponseEntity.class);
    }
}
