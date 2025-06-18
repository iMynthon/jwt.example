package mynthon.jwt.example.jwt.example.web.controller;

import lombok.RequiredArgsConstructor;
import mynthon.jwt.example.jwt.example.security.AppUserPrincipal;
import mynthon.jwt.example.jwt.example.web.dto.request.UserRequest;
import mynthon.jwt.example.jwt.example.web.dto.response.UserResponse;
import mynthon.jwt.example.jwt.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public List<UserResponse> findAll(){
        return userService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public UserResponse findById(@AuthenticationPrincipal AppUserPrincipal userPrincipal){
        return userService.findUserById(UUID.fromString(userPrincipal.getId()));
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("/registered")
    public UserResponse save(@RequestBody UserRequest request){
        return userService.save(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public UserResponse update(@RequestBody UserRequest request){
        return userService.update(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/delete")
    @PreAuthorize("hasAnyRole('MODERATOR','ADMIN')")
    public void delete(@PathVariable UUID id){
        userService.delete(id);
    }
}
