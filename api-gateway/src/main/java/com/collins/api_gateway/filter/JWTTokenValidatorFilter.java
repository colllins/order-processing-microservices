package com.collins.api_gateway.filter;

import com.collins.api_gateway.constants.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JWTTokenValidatorFilter implements GlobalFilter, Ordered {

    private final Environment env;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/users/register",
            "/api/users/login",
            "/api/users/getUser"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        //allow public end points without jwt
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        //protected end point has no Bearer token
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }

        String jwt = authorizationHeader.substring(7);

        try {
            String secret = env.getProperty(
                    ApplicationConstants.JWT_SECRET_KEY,
                    ApplicationConstants.JWT_SECRET_DEFAULT_VALUE
            );
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts
                    .parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

            Number userId = claims.get("userId", Number.class);
            String username = claims.get("username", String.class);
            String authorities = claims.get("authorities", String.class);

            if (userId == null || username == null || authorities == null) {
                return unauthorized(exchange);
            }

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            AuthorityUtils.commaSeparatedStringToAuthorityList(
                                    authorities
                            )
                    );

            /*
             * Add verified user information to the forwarded request.
             * Overwrite any values originally supplied by the client.
             */
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(request -> request.headers(headers -> {
                        headers.remove("X-User-Id");
                        headers.remove("X-Username");
                        headers.remove("X-Authorities");

                        headers.set("X-User-Id", String.valueOf(userId));
                        headers.set("X-Username", username);
                        headers.set("X-Authorities", authorities);
                    }))
                    .build();

            return chain.filter(modifiedExchange)
                    .contextWrite(
                            ReactiveSecurityContextHolder.withAuthentication(
                                    authentication
                            )
                    );
        } catch (Exception e) {
            System.out.println("JWT VALIDATION FAILED: "+e.getMessage());
            e.printStackTrace();
            return unauthorized(exchange);
        }
    }


    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream()
                .anyMatch(path::equals);
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse()
                .setStatusCode(HttpStatus.UNAUTHORIZED);

        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
