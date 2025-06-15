package mynthon.jwt.example.jwt.example.security;

import java.security.Principal;
import java.util.List;


public record AppUserPrincipal(
        String email,
        String id,
        List<String> roles) implements Principal {

    @Override
    public String getName() {
        return email;
    }
}
