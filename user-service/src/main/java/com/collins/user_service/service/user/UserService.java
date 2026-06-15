package com.collins.user_service.service.user;

import com.collins.user_service.dto.CreateUserRequestDto;
import com.collins.user_service.dto.UserResponseDto;
import com.collins.user_service.entity.User;
import com.collins.user_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserResponseDto registerUser(CreateUserRequestDto curd){
        String email = curd.getEmail().trim().toLowerCase();
        if(!userRepository.existsByEmail(email)){
            User user = new User();
            user.setEmail(email);
            user.setPassword(curd.getPassword());
            user.setRole(curd.getRole());

            User userResponse  =  userRepository.save(user);

            return new UserResponseDto(userResponse.getId(), userResponse.getEmail(), userResponse.getRole(), userResponse.getCreatedAt());
        }else{
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exist, Try again");
        }
    }

    public UserResponseDto getUserByEmail(String email){
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "User with that email Not Found!"));

        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(()->new UsernameNotFoundException("user details not found for user"));
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(), authorities);
    }
}
