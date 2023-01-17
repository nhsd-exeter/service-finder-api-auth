package uk.nhs.digital.uec.api.common.request;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.nhs.digital.uec.api.common.model.AuthenticationToken;

import org.springframework.context.annotation.ScopedProxyMode;

/**
 * Scoped class that is generated for each request that is send in to an endpoint.
 * Here we store our internal authentication details so that we do not have to pass this
 * information through methods. To obtain the information, we simply set the information
 * in this object, and it can be retrieved from anywhere within the request flow by
 * autowiring in this class.
 */
@Service

@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceFinderRequest {

    /**
     * Initilise with an instantiated object so this is never null.
     */
    private AuthenticationToken authenticationToken =
        new AuthenticationToken();

}
