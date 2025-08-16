package com.ejada.oms.auth.service;

import com.ejada.oms.auth.dto.req.LoginRequest;
import com.ejada.oms.auth.dto.res.JwtResponse;
import com.ejada.oms.auth.entity.User;
import com.ejada.oms.core.config.properties.ApplicationProperties;
import com.ejada.oms.core.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service for user authentication and token management.
 * 
 * @author Ali Hussein
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final ApplicationProperties applicationProperties;

    public JwtResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for username: {}", loginRequest.getUsername());

        // Authenticate user
        User user = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());

        // Generate JWT token
        String token = jwtUtil.generateToken(user);

        // Create response
        JwtResponse jwtResponse = JwtResponse.builder()
                .token(token)
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .expiresIn(applicationProperties.getSecurity().getJwt().getExpiration())
                .build();

        log.info("User logged in successfully: {}", user.getUsername());
        return jwtResponse;
    }

}