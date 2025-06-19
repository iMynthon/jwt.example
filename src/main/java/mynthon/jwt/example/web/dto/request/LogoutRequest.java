package mynthon.jwt.example.web.dto.request;

public record LogoutRequest(
        String email,
        String password
) {
}
