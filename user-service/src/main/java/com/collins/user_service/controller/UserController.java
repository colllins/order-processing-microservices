package com.collins.user_service.controller;

import com.collins.user_service.config.UsernamePasswordAuthenticationProvider;
import com.collins.user_service.dto.CreateUserRequestDto;
import com.collins.user_service.dto.LoginRequestDto;
import com.collins.user_service.dto.UserResponseDto;
import com.collins.user_service.service.Jwt.JwtService;
import com.collins.user_service.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UsernamePasswordAuthenticationProvider authenticationProvider;
    private final JwtService jwtService;

    @PostMapping("/register")
    public UserResponseDto createUser(@RequestBody @Valid CreateUserRequestDto curd){
        String hashPwd = passwordEncoder.encode(curd.getPassword());
        curd.setPassword(hashPwd);
        return userService.registerUser(curd);
    }

    @GetMapping("/getUser")
    public String retrieveUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return "Current Logged in user is "+authentication.getName();
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody @Valid LoginRequestDto dto){
        try{
            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            String jwt = jwtService.generateToken(authentication);

            return ResponseEntity.ok(jwt);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An exception occurred: " + e.getMessage());
        }
    }
}
