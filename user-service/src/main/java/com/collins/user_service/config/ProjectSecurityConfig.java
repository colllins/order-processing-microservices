package com.collins.user_service.config;

import com.collins.user_service.filter.JWTTokenValidationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class ProjectSecurityConfig {

    private final Environment env;

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception{
//        CsrfTokenRequestAttributeHandler csrfTokenRequestAttributeHandler = new CsrfTokenRequestAttributeHandler();
        http.sessionManagement(sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                                .cors(corsConfig-> corsConfig.configurationSource(new CorsConfigurationSource() {
//                                    @Override
//                                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) { //what cors rule should apply to incoming request
//                                        CorsConfiguration config = new CorsConfiguration();
//                                        config.setAllowedOrigins(Collections.singletonList("http://localhost:8080")); //only requests coming from 8080 are allowed
//                                        config.setAllowedMethods(Collections.singletonList("*")); //allow every http method, GTE POST PATCH PUT DELETE OPTION
//                                        config.setAllowCredentials(true); //cross-origin request to include credentials such as cookie session identifier authorization credentials
//                                        config.setAllowedHeaders(Collections.singletonList("*")); //front end may send any request header
//                                        config.setExposedHeaders(Arrays.asList("Authorization")); //allow front end to read authorization response header
//                                        config.setMaxAge(3600L); //cache results for an hour
//                                        return config;
//                                    }
//                                }))
                /*
                turns on csrf protection and configure how it works
                 */
                .csrf(csrfConfig -> csrfConfig.disable())
                /*
                Before normal authentication, run your validation filter to:
                read Bearer token
                validate signature/expiry
                extract user details
                set Authentication in SecurityContext
                 */
                .addFilterBefore(new JWTTokenValidationFilter(env), BasicAuthenticationFilter.class)
//                        .csrfTokenRequestHandler(csrfTokenRequestAttributeHandler)  //tells spring how to read/process csrf token from incoming request
//                        .ignoringRequestMatchers( "/contact","/register") //ignore certain end points. csrf will not be checked for these end points
//                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())) //store token in a cookie, with withHttpOnlyFalse, front end is allowed to read that cookie and can copy token into a request header
//                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class) //Run CsrfCookieFilter after BasicAuthenticationFilter.
//                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class) //Run RequestValidationBeforeFilter before authentication happens.
//                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class) //Runs after authentication and probably logs the user’s authorities/roles.
//                .addFilterAt(new AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter.class)  //Places that custom filter at the same position as BasicAuthenticationFilter
//                .redirectToHttps((https) -> https.disable()) // Only HTTP

                /*
                Which endpoints are public?
                Which require login?
                Which require specific roles?
                 */
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(
                                "/api/users/register",
                                "/api/users/login",
                                "/api/users/getUser",
                                "/error"
                        ).permitAll()
                        .anyRequest()
                        .authenticated());
//        http.csrf(csrfConfig->csrfConfig.disable())
//                .authorizeHttpRequests((requests)->requests
//                        .requestMatchers("/api/users/getUser").authenticated()
//                        .requestMatchers("/api/users/register", "/api/users/login").permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public CompromisedPasswordChecker compromisedPasswordChecker(){
        return new HaveIBeenPwnedRestApiPasswordChecker();
    }
}
