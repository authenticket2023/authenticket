package com.authenticket.authenticket.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ticket_categories")
@JsonIgnoreProperties(value = { "createdAt", "deletedAt", "updatedAt" })
public class TicketCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Override
    public int hashCode() {
        return Objects.hash(categoryId);
    }
//    @OneToMany
//    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
//    Set<EventTicketCategory> eventTicketCategorySet = new HashSet<>();
}
