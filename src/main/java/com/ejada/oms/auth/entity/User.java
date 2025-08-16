package com.ejada.oms.auth.entity;

import com.ejada.oms.core.entity.AuditableEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * User entity representing system users.
 * 
 * @author Ali Hussein
 */
@Entity
@Table(name = "user_table")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@AttributeOverride(name = "id", column = @Column(name = "user_id"))
public class User extends AuditableEntity<Long> {

    @NotBlank(message = "Username is required")
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    @NotNull
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    public boolean isAdmin() {
        return role != null && role.isAdmin();
    }

    public boolean isCustomer() {
        return role != null && role.isCustomer();
    }


    @Getter
    public enum UserRole {
        ADMIN("Admin", "Administrator with full system access"),
        CUSTOMER("Customer", "Customer with limited access to personal data");

        private final String displayName;
        private final String description;

        UserRole(String displayName, String description) {
            this.displayName = displayName;
            this.description = description;
        }

        public boolean isAdmin() {
            return this == ADMIN;
        }

        public boolean isCustomer() {
            return this == CUSTOMER;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
