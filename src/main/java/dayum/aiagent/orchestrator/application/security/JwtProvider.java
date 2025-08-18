package dayum.aiagent.orchestrator.application.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
  @Value("${jwt.secret}")
  private String secret;
  private Key key;

  @PostConstruct
  void init() {
    key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  public boolean validate(String token) {
    Jwts.parserBuilder()
        .setSigningKey(key)
        .setAllowedClockSkewSeconds(60)
        .build()
        .parseClaimsJws(token);
    return true;
  }

  public Long getMemberId(String token) {
    var claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return Long.parseLong(claims.getSubject());
  }
}

