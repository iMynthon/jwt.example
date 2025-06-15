package mynthon.jwt.example.jwt.example.web.dto.response;

public record ErrorResponse(
        int status,
        String message
) {
}
