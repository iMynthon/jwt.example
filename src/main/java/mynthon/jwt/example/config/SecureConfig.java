package mynthon.jwt.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import mynthon.jwt.example.filter.JwtAuthenticationFilter;
import mynthon.jwt.example.security.JwtAuthConverter;
import mynthon.jwt.example.web.dto.response.ErrorResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecureConfig {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> tokenFilter(JwtAuthConverter jwtAuthConverter) {
        JwtAuthenticationFilter bearerAuthFilter =
                new JwtAuthenticationFilter(jwtAuthConverter);
        FilterRegistrationBean<JwtAuthenticationFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(bearerAuthFilter);
        registration.addUrlPatterns("/api/v1/user","/api/v1/user/**");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        return registration;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity security, AuthenticationManager authenticationManager,
                                           JwtAuthenticationFilter tokenFilter) throws Exception {
        security.authorizeHttpRequests((auth) -> auth.requestMatchers(HttpMethod.POST, "/api/v1/user/**","/api/v1/login/**").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(handling -> handling.accessDeniedHandler((request, response, accessDeniedException) -> {
                    createResponse(response,"Вы не являетесь модератором или администратором системы - ", accessDeniedException);
                }).authenticationEntryPoint((request, response, authException) -> {
                    createResponse(response,"Ошибка аутентификации - ",authException);
                })).csrf(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer<HttpSecurity>::disable)
                .sessionManagement(httpSecuritySessionManagementConfigurer ->
                        httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authenticationManager(authenticationManager)
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }

    private void createResponse(HttpServletResponse response, String message, Exception e) throws IOException {
        response.setStatus(e instanceof AccessDeniedException ? HttpStatus.FORBIDDEN.value() : HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(new ObjectMapper().writeValueAsString(
                new ErrorResponse(response.getStatus(), message + e.getMessage())));
    }

}
