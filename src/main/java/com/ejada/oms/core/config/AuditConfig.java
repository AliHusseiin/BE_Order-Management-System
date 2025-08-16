package com.ejada.oms.core.config;

import com.ejada.oms.core.util.SecurityUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Configuration for JPA Auditing.
 * Enables automatic population of audit fields (createdBy, modifiedBy, createdAt, modifiedAt)
 * in entities that extend AuditableEntity.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class AuditConfig {

    /**
     * Provides the current auditor (user) for JPA auditing.
     * Uses Spring Security context to get the current authenticated user.
     *
     * @return AuditorAware bean that provides current user information
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return new SpringSecurityAuditorAware();
    }

    /**
     * Implementation of AuditorAware that uses Spring Security
     * to get the current authenticated user.
     */
    public static class SpringSecurityAuditorAware implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            try {
                // Try to get current username from Security Context
                String currentUsername = SecurityUtils.getCurrentUsername();
                return Optional.ofNullable(currentUsername);
            } catch (Exception e) {
                // Fallback to system user if no security context available
                return Optional.of("SYSTEM");
            }
        }
    }
}