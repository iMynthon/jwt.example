package mynthon.jwt.example.service;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynthon.jwt.example.exception.TokenParseException;
import mynthon.jwt.example.security.AppUserPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtTokenService {

    private static final String ROLES_CLAIM = "roles";
    private static final String ID_CLAIM = "id";

    @Value("${user-service.jwt.secret}")
    private String jwtSecret;
    @Value("${user-service.jwt.tokenExpiration}")
    private Duration tokenExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret.trim());
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA512");
    }

    public String generateToken(String email, String id, List<String> roles) {
        return Jwts.builder()
                .setSubject(email)
                .setExpiration(Date.from(Instant.now().plus(tokenExpiration)))
                .claim(ROLES_CLAIM, roles)
                .claim(ID_CLAIM, id)
                .signWith(getSigningKey())
                .compact();
    }
    @SuppressWarnings("unchecked")
    public Authentication toAuthentication(String token) {
        Claims claims = parseTokenClaims(token);
        String email = claims.getSubject();
        String id = claims.get(ID_CLAIM, String.class);
        List<String> roles = claims.get(ROLES_CLAIM, List.class);
        validateClaims(email, id, roles);
        AppUserPrincipal principal = new AppUserPrincipal(email,id,roles);
        return new UsernamePasswordAuthenticationToken(
                principal,
                null,
                roles.stream().map(SimpleGrantedAuthority::new).toList()
        );
    }
    public boolean validate(String authToken) {
        try {
            parseTokenClaims(authToken);
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("Token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("Token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Claims string is empty: {}", e.getMessage());
        }

        return false;
    }

    private Claims parseTokenClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void validateClaims(String email, String id, List<String> roles) {
        if (email == null || id == null || roles == null) {
            log.error("Missing required claims - Email: {}, ID: {}, Roles: {}", email, id, roles);
            throw new TokenParseException("Token claims cannot be null");
        }

        if (email.isBlank() || id.isBlank() || roles.isEmpty()) {
            log.error("Empty claims - Email: {}, ID: {}, Roles: {}", email, id, roles);
            throw new TokenParseException("Token claims cannot be empty");
        }
    }
}
