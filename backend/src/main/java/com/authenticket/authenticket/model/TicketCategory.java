package com.authenticket.authenticket.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "ticket_categories", schema = "dev")
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
        return Objects.hash(categoryName);
    }
//    @OneToMany
//    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
//    Set<EventTicketCategory> eventTicketCategorySet = new HashSet<>();
}
