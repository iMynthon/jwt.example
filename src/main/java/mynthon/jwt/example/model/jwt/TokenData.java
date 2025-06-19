package mynthon.jwt.example.model.jwt;

public record TokenData(
        String token,
        String refreshToken) {
}
