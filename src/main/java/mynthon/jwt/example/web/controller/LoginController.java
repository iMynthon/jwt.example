package mynthon.jwt.example.web.controller;

import lombok.RequiredArgsConstructor;
import mynthon.jwt.example.model.jwt.TokenData;
import mynthon.jwt.example.service.SecurityService;
import mynthon.jwt.example.web.dto.request.LogoutRequest;
import mynthon.jwt.example.web.dto.request.RefreshTokenRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private final SecurityService securityService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/access")
    public TokenData logout(@RequestBody LogoutRequest request){
        return securityService.processPasswordToken(request.email(),request.password());
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/refresh")
    public TokenData logoutRefresh(@RequestBody RefreshTokenRequest request){
        return securityService.processRefreshToken(request.refreshToken());
    }
}
