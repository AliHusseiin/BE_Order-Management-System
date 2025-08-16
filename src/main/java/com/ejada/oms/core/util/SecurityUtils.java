package com.ejada.oms.core.util;

import com.ejada.oms.auth.entity.User;
import com.ejada.oms.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public final class SecurityUtils {

    private SecurityUtils() {
        // Utility class
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw BusinessException.userNotAuthenticated();
        }

        if (authentication.getPrincipal() instanceof User user) {
            return user;
        }

        throw BusinessException.userNotAuthenticated();
    }

    public static String getCurrentUsername() {
        return getCurrentUser().getUsername();
    }

}