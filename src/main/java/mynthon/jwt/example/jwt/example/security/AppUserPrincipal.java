package mynthon.jwt.example.jwt.example.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.security.Principal;
import java.util.List;

@RequiredArgsConstructor
@Getter @Setter
public class AppUserPrincipal implements Principal {

    private final String name;

    private final String id;

    private final List<String> roles;

    @Override
    public String getName() {
        return name;
    }
}
