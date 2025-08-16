package com.ejada.oms.core.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Base auditable entity with audit trail functionality.
 * 
 * @param <ID> the type of the entity identifier
 * @author Ali Hussein
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AuditableEntity<ID extends Serializable> extends BaseEntity<ID> {

    private static final long serialVersionUID = 1L;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime modifiedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false, length = 100)
    private String createdBy;

    @LastModifiedBy
    @Column(name = "modified_by", length = 100)
    private String modifiedBy;

    public String getAuditInfo() {
        return String.format("Created by %s at %s, Last modified by %s at %s",
                createdBy != null ? createdBy : "System",
                createdAt != null ? createdAt.toString() : "Unknown",
                modifiedBy != null ? modifiedBy : "System",
                modifiedAt != null ? modifiedAt.toString() : "Unknown");
    }

    public boolean isRecentlyCreated() {
        if (createdAt == null) return false;
        return createdAt.isAfter(LocalDateTime.now().minusHours(1));
    }

    public boolean isRecentlyModified() {
        if (modifiedAt == null) return false;
        return modifiedAt.isAfter(LocalDateTime.now().minusHours(1));
    }

    public boolean wasLastModifiedBy(String username) {
        return modifiedBy != null && modifiedBy.equals(username);
    }

    public boolean wasCreatedBy(String username) {
        return createdBy != null && createdBy.equals(username);
    }

    @Override
    public String toString() {
        return String.format("%s{id=%d, version=%d, createdBy='%s', createdAt=%s, modifiedBy='%s', modifiedAt=%s}",
                this.getClass().getSimpleName(), getId(), getVersion(), createdBy, createdAt, modifiedBy, modifiedAt);
    }
}