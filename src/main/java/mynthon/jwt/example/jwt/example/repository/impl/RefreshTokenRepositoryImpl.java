package mynthon.jwt.example.jwt.example.repository.impl;

import mynthon.jwt.example.jwt.example.exception.RefreshTokenException;
import mynthon.jwt.example.jwt.example.model.jwt.RefreshToken;
import mynthon.jwt.example.jwt.example.repository.RefreshTokenRepository;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;
import java.util.UUID;

@Repository
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private static final String REFRESH_TOKEN_INDEX = "refreshTokenIndex";

    private final ValueOperations<String, RefreshToken> operationsForValue;
    private final HashOperations<String,String,String> operationsForHash;

    public RefreshTokenRepositoryImpl(RedisTemplate<String,RefreshToken> refreshTokenRedisTemplate){
        this.operationsForValue = refreshTokenRedisTemplate.opsForValue();
        operationsForHash = refreshTokenRedisTemplate.opsForHash();
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken, Duration duration) {
        operationsForValue.set(REFRESH_TOKEN_INDEX,refreshToken);
        operationsForHash.put(REFRESH_TOKEN_INDEX,refreshToken.getValue(),refreshToken.getId());
        return operationsForValue.get(refreshToken.getId());
    }

    @Override
    public RefreshToken getByValue(String refreshToken) {
        // 1. Поиск ID токена
        String tokenId = operationsForHash.get(REFRESH_TOKEN_INDEX, refreshToken);
        if (tokenId == null) {
            throw new RefreshTokenException("Не найден");
        }
        // 2. Получение старого токена
        RefreshToken oldToken = operationsForValue.get(tokenId);
        if (oldToken == null) {
            throw new RefreshTokenException("Данные не найдены");
        }
        // 3. Удаление старого токена
        operationsForHash.delete(REFRESH_TOKEN_INDEX, tokenId); // Удаляем по ID, а надо по значению
        operationsForValue.getOperations().delete(tokenId);
        // 4. Генерация и сохранение нового
        RefreshToken newToken = generateNewToken(oldToken.getUserId());
        save(newToken, Duration.ofMinutes(30));
        return newToken;
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
}
