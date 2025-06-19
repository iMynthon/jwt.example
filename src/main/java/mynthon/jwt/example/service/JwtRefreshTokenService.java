package mynthon.jwt.example.service;

import lombok.RequiredArgsConstructor;
import mynthon.jwt.example.exception.RefreshTokenException;
import mynthon.jwt.example.model.jwt.RefreshToken;
import mynthon.jwt.example.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtRefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${user-service.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    public RefreshToken save(String userId){
        RefreshToken savedToken = refreshTokenRepository.save(generateNewToken(userId),refreshTokenExpiration);
        if (savedToken == null) {
            throw new RefreshTokenException("Failed to save refresh token for user: " + userId);
        }
        return savedToken;
    }

    private RefreshToken generateNewToken(String userId) {
        return RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .value(generateSecureToken()) // Например, SecureRandom
                .userId(userId)
                .build();
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[36]; // 288 бит
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public RefreshToken getByValue(String refreshTokenValue){
        return refreshTokenRepository.getByValue(refreshTokenValue);
    }
}
