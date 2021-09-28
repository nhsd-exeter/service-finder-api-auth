package uk.nhs.digital.uec.api.utils;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

import java.util.Date;
import java.util.Set;

import static uk.nhs.digital.uec.api.utils.Constants.COGNITO_GROUPS;

public class LocalJwtFactory {
  public String createToken(
    String id, String issuer, String subject, long timeToLiveMs, Set<String> groupNames) {

    long nowMs = System.currentTimeMillis();
    Date nowDate = new Date(nowMs);

    JwtBuilder builder =
      Jwts.builder()
        .setId(id)
        .setIssuedAt(nowDate)
        .setSubject(subject)
        .setIssuer(issuer);

    if (timeToLiveMs >= 0) {
      long expiredMs = nowMs + timeToLiveMs;
      Date expirationDate = new Date(expiredMs);
      builder.setExpiration(expirationDate);
    }

    builder.claim(COGNITO_GROUPS, groupNames.toArray());
    return builder.compact();
  }
}
