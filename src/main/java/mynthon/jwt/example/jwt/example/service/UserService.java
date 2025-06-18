package mynthon.jwt.example.jwt.example.service;

import lombok.RequiredArgsConstructor;
import mynthon.jwt.example.jwt.example.web.dto.request.UserRequest;
import mynthon.jwt.example.jwt.example.web.dto.response.UserResponse;
import mynthon.jwt.example.jwt.example.exception.EntityNotFoundException;
import mynthon.jwt.example.jwt.example.mapper.UserMapper;
import mynthon.jwt.example.jwt.example.model.User;
import mynthon.jwt.example.jwt.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder encoder;

    public List<UserResponse> findAll(){
        return userRepository.findAll().stream()
                .map(userMapper::entityToResponse).toList();
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new EntityNotFoundException(String.format("User с email:{%s} - не найден",email)));
    }

    @Transactional
    public UserResponse save(UserRequest request){
        User user = userMapper.requestToEntity(request);
        user.setPassword(encoder.encode(request.password()));
        return userMapper.entityToResponse(userRepository.save(user));

    }

    public UserResponse findUserById(UUID id){
        return userMapper.entityToResponse(findById(id));
    }

    public UserResponse update(UserRequest request){
        User exists = findById(UUID.randomUUID());
        userMapper.updateEntity(exists,userMapper.requestToEntity(request));
        return userMapper.entityToResponse(userRepository.save(exists));
    }

    public void delete(UUID id){
        if(!userRepository.existsById(id)){
           throw new EntityNotFoundException(String.format("User с id:{%s} - не найден",id));
        }
        userRepository.deleteById(id);
    }

    public User findById(UUID id){
        return userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException(String.format("User с id:{%s} - не найден",id)));
    }
}
