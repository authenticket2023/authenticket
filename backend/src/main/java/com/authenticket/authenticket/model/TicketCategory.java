package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

/**
 * Represents a category for tickets, providing information about the category's name and identification.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_categories")
@JsonIgnoreProperties(value = { "createdAt", "deletedAt", "updatedAt" })
public class TicketCategory extends BaseEntity {
    /**
     * The unique identifier for the ticket category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    /**
     * The name of the ticket category.
     */
    @Column(name = "category_name", nullable = false)
    private String categoryName;

    /**
     * Computes the hash code for the ticket category based on its unique identifier.
     *
     * @return The hash code for the ticket category.
     */
    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }
}
