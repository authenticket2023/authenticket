package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_organiser", schema = "dev")
@EqualsAndHashCode(callSuper = true)
public class EventOrganiser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "organiser_id")
    private Integer organiserId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "description")
    private String description;

    @Column(name = "logo_image")
    private String logoImage;

    @OneToMany( mappedBy = "organiser")
//    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Event> events = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore

    @JoinColumn(name = "verified_by",nullable = false)
    private Admin admin;
}