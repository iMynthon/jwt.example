package mynthon.jwt.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mynthon.jwt.example.exception.DuplicateEmailException;
import mynthon.jwt.example.exception.EntityNotFoundException;
import mynthon.jwt.example.mapper.UserMapper;
import mynthon.jwt.example.model.User;
import mynthon.jwt.example.repository.UserRepository;
import mynthon.jwt.example.web.dto.request.UserRequest;
import mynthon.jwt.example.web.dto.response.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
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
        checkEmailDuplicate(request.email());
        User user = userMapper.requestToEntity(request);
        user.setPassword(encoder.encode(request.password()));
        return userMapper.entityToResponse(userRepository.save(user));

    }

    public UserResponse findUserById(UUID id){
        return userMapper.entityToResponse(findById(id));
    }

    public UserResponse update(UserRequest request){
        checkEmailDuplicate(request.email());
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

    private void checkEmailDuplicate(String email){
        if(userRepository.existsByEmailIgnoreCase(email)){
            log.info("Такой email уже зарегистрирован - {}",email);
            throw new DuplicateEmailException("Такой email уже зарегистрирован");
        }
    }
}
