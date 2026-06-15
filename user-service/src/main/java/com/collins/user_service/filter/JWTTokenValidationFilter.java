package com.collins.user_service.filter;

import com.collins.user_service.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class JWTTokenValidationFilter extends OncePerRequestFilter {

    private final Environment env;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(ApplicationConstants.JWT_HEADER);
        if(header!=null && header.startsWith("Bearer ")) {
            String jwt = header.substring(7);
            if (null != jwt) {
                try {
                    String secret = env.getProperty(
                            ApplicationConstants.JWT_SECRET_KEY,
                            ApplicationConstants.JWT_SECRET_DEFAULT_VALUE
                    );
                    SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                    if (null != secretKey) {
                        Claims claims = Jwts
                                .parserBuilder()
                                .setSigningKey(secretKey)
                                .build()
                                .parseClaimsJws(jwt)
                                .getBody();
                        String username = String.valueOf(claims.get("username"));
                        String authorities = String.valueOf(claims.get("authorities"));
                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                AuthorityUtils.commaSeparatedStringToAuthorityList(authorities));
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        context.setAuthentication(authentication);
                        SecurityContextHolder.setContext(context);
                    }
                } catch (Exception e) {
                    throw new BadCredentialsException("Invalid token");
                }
            }
        }
        filterChain.doFilter(request, response);
    }
        @Override
        protected boolean shouldNotFilter (HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();

            return path.equals("/api/users/login")
                    || path.equals("/api/users/register");
        }
}
