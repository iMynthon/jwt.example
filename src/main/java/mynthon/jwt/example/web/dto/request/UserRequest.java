package mynthon.jwt.example.web.dto.request;

import lombok.Builder;
import mynthon.jwt.example.model.RoleType;

import java.util.List;

@Builder
public record UserRequest(
        String username,
        String email,
        String password,
        List<RoleType> roles
) {
}
