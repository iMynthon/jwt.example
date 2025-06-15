package mynthon.jwt.example.jwt.example.web.controller;

import lombok.RequiredArgsConstructor;
import mynthon.jwt.example.jwt.example.web.dto.request.UserRequest;
import mynthon.jwt.example.jwt.example.web.dto.response.UserResponse;
import mynthon.jwt.example.jwt.example.service.UserService;
import org.springframework.http.HttpStatus;
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
    private List<UserResponse> findAll(){
        return userService.findAll();
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}/user")
    private UserResponse findById(@PathVariable UUID id){
        return userService.findUserById(id);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping
    private UserResponse save(@RequestBody UserRequest request){
        return userService.save(request);
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping
    private UserResponse update(@RequestBody UserRequest request){
        return userService.update(request);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}/delete")
    private void delete(@PathVariable UUID id){
        userService.delete(id);
    }
}
