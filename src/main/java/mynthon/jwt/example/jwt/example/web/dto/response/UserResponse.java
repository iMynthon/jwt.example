package mynthon.jwt.example.jwt.example.web.dto.response;

import lombok.Builder;

import java.util.UUID;

@Builder
public record UserResponse(
        UUID id,
        String username,
        String email,
        String password
){
}
