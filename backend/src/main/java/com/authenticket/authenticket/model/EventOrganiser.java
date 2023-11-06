package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


/**
 * The `EventOrganiser` class represents an event organizer in the system.
 * It implements the `UserDetails` interface to provide user-related information for authentication and authorization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_organiser")
@EqualsAndHashCode(callSuper = true)
public class EventOrganiser extends BaseEntity implements UserDetails {
    /**
     * The unique identifier for the event organizer, generated using an auto-increment strategy.
     */
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name = "organiser_id", nullable = false)
    private Integer organiserId;

    /**
     * The name of the event organizer.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The email of the event organizer.
     */
    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    /**
     * The password of the event organizer.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The description of the event organizer.
     */
    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    /**
     * The logo image of the event organizer.
     */
    @Column(name = "logo_image")
    private String logoImage;

    /**
     * The enabled status of the event organizer's account.
     */
    @Column(name = "enabled")
    private Boolean enabled = false;

    /**
     * The administrator who reviewed and approved/rejected this event organizer.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "reviewed_by")
    private Admin admin;

    /**
     * The review status of the event organizer.
     */
    @JsonIgnore
    @Column(name = "review_status")
    private String reviewStatus;

    /**
     * The review remarks for the event organizer.
     */
    @JsonIgnore
    @Column(name = "review_remarks")
    private String reviewRemarks;

    /**
     * The role assigned to event organizers. It is used for authentication and authorization.
     */
    @Getter
    private static String role = "ORGANISER";

    /**
     * The list of events organized by this event organizer.
     */
    @OneToMany( mappedBy = "organiser")
    @JsonIgnore
    private List<Event> events = new ArrayList<>();

    /**
     * Get the authorities (roles) of the event organizer.
     *
     * @return A collection of granted authorities, with the role of "ORGANISER."
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(role);
        return Collections.singletonList(authority);
    }

    /**
     * Get the password of the event organizer.
     *
     * @return The password.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Get the username (email) of the event organizer.
     *
     * @return The email.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Check if the event organizer's account is not expired.
     *
     * @return `true` if the account is not expired, `false` otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return this.enabled;
    }

    /**
     * Check if the event organizer's account is not locked.
     *
     * @return `true` if the account is not locked, `false` otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
    }

    /**
     * Check if the event organizer's credentials are not expired.
     *
     * @return `true` if the credentials are not expired, `false` otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    /**
     * Check if the event organizer's account is enabled.
     *
     * @return `true` if the account is enabled, `false` otherwise.
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Calculate the hash code for the event organizer based on their name.
     *
     * @return The calculated hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}