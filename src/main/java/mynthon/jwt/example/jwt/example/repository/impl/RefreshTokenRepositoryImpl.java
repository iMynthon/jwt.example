package mynthon.jwt.example.jwt.example.repository.impl;

import lombok.extern.slf4j.Slf4j;
import mynthon.jwt.example.jwt.example.exception.RefreshTokenException;
import mynthon.jwt.example.jwt.example.model.jwt.RefreshToken;
import mynthon.jwt.example.jwt.example.repository.RefreshTokenRepository;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Repository
@Slf4j
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final String TOKEN_STORE_PREFIX = "token:";
    private static final String TOKEN_INDEX_KEY = "token_index";

    private final ValueOperations<String, RefreshToken> valueOperations;
    private final HashOperations<String, String, String> hashOperations;
    private final RedisTemplate<String, RefreshToken> redisTemplate;

    public RefreshTokenRepositoryImpl(RedisTemplate<String, RefreshToken> redisTemplate) {
        this.valueOperations = redisTemplate.opsForValue();
        this.hashOperations = redisTemplate.opsForHash();
        this.redisTemplate = redisTemplate;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken, Duration duration) {
        try {
            String tokenKey = TOKEN_STORE_PREFIX + refreshToken.getId();
            valueOperations.set(tokenKey, refreshToken, duration);
            hashOperations.put(TOKEN_INDEX_KEY, refreshToken.getValue(), refreshToken.getId());
            redisTemplate.expire(TOKEN_INDEX_KEY, duration);
            return refreshToken;
        } catch (Exception e) {
            log.error("Failed to save refresh token", e);
            throw new RefreshTokenException(String.format("Failed to save refresh token - %s", e.getMessage()));
        }
    }

    @Override
    public RefreshToken getByValue(String tokenValue) {
        String tokenId = hashOperations.get(TOKEN_INDEX_KEY, tokenValue);
        if (tokenId == null) {
            throw new RefreshTokenException("Токен не найден");
        }
        String tokenKey = TOKEN_STORE_PREFIX + tokenId;
        RefreshToken existingToken = valueOperations.get(tokenKey);
        if (existingToken == null) {
            hashOperations.delete(TOKEN_INDEX_KEY, tokenValue);
            throw new RefreshTokenException("Токен устарел или удалён");
        }
        RefreshToken newToken = generateNewToken(existingToken.getUserId());
        redisTemplate.delete(tokenKey);
        hashOperations.delete(TOKEN_INDEX_KEY, tokenValue);
        valueOperations.set(TOKEN_STORE_PREFIX + newToken.getId(), newToken,
                Duration.ofMinutes(30));
        hashOperations.put(TOKEN_INDEX_KEY, newToken.getValue(), newToken.getId());
        return newToken;
    }

    private RefreshToken generateNewToken(String userId) {
        return RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .value(generateSecureToken())
                .userId(userId)
                .build();
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[36];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
