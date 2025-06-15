package mynthon.jwt.example.jwt.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynthon.jwt.example.jwt.example.exception.CheckPasswordException;
import mynthon.jwt.example.jwt.example.model.User;
import mynthon.jwt.example.jwt.example.model.jwt.RefreshToken;
import mynthon.jwt.example.jwt.example.model.jwt.TokenData;
import mynthon.jwt.example.jwt.example.security.jwt.JwtTokenService;
import mynthon.jwt.example.jwt.example.service.JwtRefreshTokenService;
import mynthon.jwt.example.jwt.example.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class SecurityService {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenService jwtTokenService;

    private final JwtRefreshTokenService jwtRefreshTokenService;

    public TokenData processPasswordToken(String email,String password){
        User user = userService.findByEmail(email);
        if(!passwordEncoder.matches(password,user.getPassword())){
            log.error("Exception trying to check password for email: {}", email);
            throw new CheckPasswordException();
        }
        return createTokenData(user);
    }

    public TokenData processRefreshToken(String refreshTokenValue){
        RefreshToken refreshToken = jwtRefreshTokenService.getByValue(refreshTokenValue);
        User user = userService.findById(UUID.fromString(refreshToken.getUserId()));
        return createTokenData(user);
    }

    private TokenData createTokenData(User user) {
        String token = jwtTokenService.generateToken(
                user.getUsername(),
                String.valueOf(user.getId()),
                user.getRoles().stream().map(Enum::toString).toList());
        RefreshToken refreshToken = jwtRefreshTokenService.save(String.valueOf(user.getId()));
        return new TokenData(token,refreshToken.getValue());
    }

}
