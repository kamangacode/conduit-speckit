package com.conduit.infrastructure.user.security;

import com.conduit.application.user.TokenService;
import com.conduit.application.user.UserException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService implements TokenService {
  private final SecretKey key;

  public JwtTokenService(@Value("${conduit.jwt.secret}") String secret) {
    if (secret == null || secret.length() < 32) {
      throw new IllegalStateException("conduit.jwt.secret must contain at least 32 characters");
    }
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public String issue(UUID userId) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(userId.toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(3600)))
        .signWith(key)
        .compact();
  }

  @Override
  public UUID subject(String token) {
    try {
      return UUID.fromString(
          Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject());
    } catch (RuntimeException exception) {
      throw new UserException("token", "is invalid", UserException.ErrorKind.UNAUTHORIZED);
    }
  }
}
