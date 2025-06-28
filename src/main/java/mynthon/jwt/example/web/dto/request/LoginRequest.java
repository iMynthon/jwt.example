package mynthon.jwt.example.web.dto.request;

public record LoginRequest(
        String email,
        String password
) {
}
