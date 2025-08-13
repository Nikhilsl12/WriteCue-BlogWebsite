package org.coderscrib.blogapp.config;

import org.coderscrib.blogapp.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            // Try to find user by username
            var userOptional = userRepository.findByUsername(username);

            // If not found by username, try by email
            if (userOptional.isEmpty()) {
                userOptional = userRepository.findByEmail(username);
            }

            // If user is found, convert to UserDetails
            if (userOptional.isPresent()) {
                org.coderscrib.blogapp.entity.User appUser = userOptional.get();
                return User.builder()
                        .username(appUser.getUsername())
                        .password(appUser.getPassword())
                        .roles("USER")
                        .build();
            }

            throw new UsernameNotFoundException("User not found: " + username);
        };
    }
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/register/**", "/api/users/login/**").permitAll()
                .requestMatchers("/error/**", "/favicon.ico", "/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/test-exceptions/**").permitAll() // Allow test endpoints for exception testing
                .anyRequest().authenticated()
        )
        .csrf(csrf -> csrf.disable()) // Disable CSRF
        .cors(cors -> cors.disable()) // Disable CORS
        .formLogin(form -> form.disable()) // Disable form login
        .logout(logout -> logout.disable()) // Disable logout
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Stateless session
        .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
        )
        .httpBasic(httpBasic -> {}); // Enable HTTP Basic Authentication for Postman testing

    return http.build();
}


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
