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

/**
 * The `Admin` entity represents an administrative user.
 *
 * This entity is used for administrative users in the system and implements the `UserDetails` interface for Spring Security integration.
 */
@Entity
@Table(name = "Admin")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Admin extends BaseEntity implements UserDetails {
    /**
     * The unique identifier for the admin.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id", nullable = false)
    private Integer adminId;

    /**
     * The name of the admin.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The email address of the admin (unique).
     */
    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    /**
     * The hashed password of the admin.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The role assigned to the admin (e.g., "ADMIN").
     */
    @Getter
    private static String role = "ADMIN";

    /**
     * Returns the authorities (roles) granted to the admin.
     *
     * @return A collection of granted authorities (in this case, a single role authority).
     */
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        return Collections.singletonList(authority);
    }

    /**
     * Returns the admin's password.
     *
     * @return The hashed password of the admin.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Returns the admin's email (username).
     *
     * @return The email address of the admin.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Indicates whether the admin's account is not expired.
     *
     * @return `true` if the admin's account is not expired; `false` otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indicates whether the admin's account is not locked.
     *
     * @return `true` if the admin's account is not locked; `false` otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indicates whether the admin's credentials are not expired.
     *
     * @return `true` if the admin's credentials are not expired; `false` otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indicates whether the admin's account is enabled.
     *
     * @return `true` if the admin's account is enabled; `false` otherwise.
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * A list of event organizers associated with this admin.
     */
    @OneToMany(mappedBy = "admin")
    private List<EventOrganiser> eventOrganiser = new ArrayList<>();
}
