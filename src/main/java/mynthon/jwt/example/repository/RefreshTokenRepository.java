package mynthon.jwt.example.repository;

import mynthon.jwt.example.model.jwt.RefreshToken;

import java.time.Duration;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken, Duration duration);

    RefreshToken getByValue(String refreshToken);
}
