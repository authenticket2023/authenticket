package com.authenticket.authenticket.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * The `BaseEntity` class represents a common superclass for entities in the system.
 *
 * This abstract class contains fields related to timestamp information, including the creation time, last update time, and deletion time.
 */
@Data
@MappedSuperclass
abstract class BaseEntity {

    /**
     * The timestamp indicating when the entity was created.
     */
    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;

    /**
     * The timestamp indicating when the entity was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * The timestamp indicating when the entity was deleted, if applicable.
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}
