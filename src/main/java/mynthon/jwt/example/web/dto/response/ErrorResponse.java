package mynthon.jwt.example.web.dto.response;

public record ErrorResponse(
        int status,
        String message
) {
}
