package mynthon.jwt.example.jwt.example.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import mynthon.jwt.example.jwt.example.security.jwt.JwtTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.token.TokenService;
import org.springframework.stereotype.Component;



@Component
@RequiredArgsConstructor
public class JwtAuthConverter {

    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenService jwtTokenService;

    public Authentication convert(HttpServletRequest request){
        String token = extractBearerToken(request);
        return (token != null && jwtTokenService.validate(token)) ?
                jwtTokenService.toAuthentication(token) : null;
    }

    private String extractBearerToken(HttpServletRequest request){
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        return (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) ?
                authorizationHeader.substring(BEARER_PREFIX.length()) : null;
    }
}
