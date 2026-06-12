package com.collins.user_service.service;

import com.collins.user_service.dto.CreateUserRequestDto;
import com.collins.user_service.dto.UserResponseDto;
import com.collins.user_service.entity.User;
import com.collins.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDto createUser(CreateUserRequestDto curd){
        String email = curd.getEmail().trim().toLowerCase();
        if(!userRepository.existByEmail(email)){
            User user = new User();
            user.setEmail(curd.getEmail());
            user.setPassword(curd.getPassword());
            user.setRole(curd.getRole());

            User userResponse  =  userRepository.save(user);

            return new UserResponseDto(userResponse.getId(), userResponse.getEmail(), userResponse.getRole(), userResponse.getCreatedAt());
        }else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exist, Try again");
        }
    }
}
