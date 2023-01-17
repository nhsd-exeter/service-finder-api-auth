package uk.nhs.digital.uec.api.interceptor;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MDCInterceptor implements HandlerInterceptor {


  public static final String CORRELATION_ID_HEADER_NAME = "X-Correlation-Id";
  private static final String CORRELATION_ID_LOG_VAR_NAME = "correlationId";

  @Override
  public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
          throws Exception {
            final String correlationId = getCorrelationIdFromHeader(request);
            MDC.clear();
            MDC.put(CORRELATION_ID_LOG_VAR_NAME, correlationId);
            return true;
  }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            @Nullable Exception ex) throws Exception {
      log.debug("removing correlationId from MDC. {}", MDC.get(CORRELATION_ID_LOG_VAR_NAME));
      MDC.remove(CORRELATION_ID_LOG_VAR_NAME);
    }


    private String generateUniqueCorrelationId() {
      return UUID.randomUUID().toString();
    }


  private String getCorrelationIdFromHeader(final HttpServletRequest request) {
    String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);
    if (StringUtils.isBlank(correlationId)) {
      correlationId = generateUniqueCorrelationId();
      log.debug("using backend generated correlationId. {}", correlationId);
    }
    else{
      log.debug("using frontend generated correlationId. {}", correlationId);
    }
    return correlationId;
  }


}
