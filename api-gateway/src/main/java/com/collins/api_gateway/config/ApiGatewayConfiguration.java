package com.collins.api_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class ApiGatewayConfiguration {
     @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder){
         return builder.routes()
                 .route(p->p.path("/api/users/**")
                         .uri("lb://user-service"))
                 .route(p->p.path("/api/orders/**")
                         .uri("lb://order-service"))
                 .route(p->p.path("/api/payments/**")
                         .uri("lb://payment-service"))
                 .route(p->p.path("/api/notifications/**")
                         .uri("lb://notification-service"))
                 .build();
     }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll()
                )
                .build();
    }
}
