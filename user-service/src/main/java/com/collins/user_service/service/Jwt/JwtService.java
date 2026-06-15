package com.collins.user_service.service.Jwt;

import com.collins.user_service.constants.ApplicationConstants;
import com.collins.user_service.dto.UserResponseDto;
import com.collins.user_service.service.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final UserService userService;
    private final Environment env;

    public String generateToken(Authentication authentication){

            UserResponseDto user = userService.getUserByEmail(authentication.getName());

                String secret = env
                        .getProperty(
                                ApplicationConstants.JWT_SECRET_KEY,
                                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE);

                SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                String jwt = Jwts.builder()
                        .setIssuer("user-service")
                        .setSubject("JWT Token")
                        .claim("userId", user.getId())
                        .claim("username", authentication.getName())
                        .claim("authorities", authentication.getAuthorities().stream().map(
                                        GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(","))

                        )
                        .setIssuedAt(new Date())
                        .setExpiration(
                                new Date(System.currentTimeMillis()+3_000_0000)
                        )
                        .signWith(secretKey)
                        .compact();

                return jwt;
            }
}
