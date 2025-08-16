package com.ejada.oms.auth.service;

import com.ejada.oms.auth.entity.User;
import com.ejada.oms.auth.repository.UserRepository;
import com.ejada.oms.core.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user management operations.
 * 
 * @author Ali Hussein
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User authenticate(String username, String password) {
        log.debug("Authenticating user: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(BusinessException::invalidCredentials);

        if (!user.getIsActive()) {
            throw BusinessException.userInactive();
        }

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw BusinessException.invalidCredentials();
        }

        log.info("User authenticated successfully: {}", username);
        return user;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.userNotFound(username));
    }


    public User createCustomerUser(String username, String email, String password) {
        log.info("Creating customer user: {}", username);

        if (userRepository.existsByUsername(username)) {
            throw BusinessException.usernameAlreadyExists(username);
        }

        if (userRepository.existsByEmail(email)) {
            throw BusinessException.emailAlreadyExists(email);
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .role(User.UserRole.CUSTOMER)
                .isActive(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Customer user created with ID: {}", saved.getId());
        return saved;
    }

}