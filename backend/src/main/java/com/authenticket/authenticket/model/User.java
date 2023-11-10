package com.authenticket.authenticket.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.*;

/**
 * Represents a user in the application, with attributes such as name, email, password, date of birth, and more.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "app_user")
public class User extends BaseEntity implements UserDetails {
    /**
     * The unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /**
     * The name of the user.
     */
    @Column(name = "name", nullable = false)
    private String name;

    /**
     * The email address of the user, which is unique.
     */
    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    /**
     * The password associated with the user.
     */
    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The date of birth of the user.
     */
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /**
     * The profile image of the user.
     */
    @Column(name = "profile_image")
    private String profileImage;

    /**
     * Flag indicating whether the user is enabled.
     */
    @Column(name = "enabled")
    private Boolean enabled = false;

    /**
     * The role assigned to the user.
     */
    @Getter
    private static String role = "USER";

    /**
     * Returns the user's authorities (in this case, their role as a granted authority).
     *
     * @return A list containing the user's role as a granted authority.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(role);
        return Collections.singletonList(authority);
    }

    /**
     * Gets the user's password.
     *
     * @return The user's password.
     */
    @Override
    public String getPassword() {
        return this.password;
    }

    /**
     * Gets the user's email address, which is used as their username.
     *
     * @return The user's email address.
     */
    @Override
    public String getUsername() {
        return this.email;
    }

    /**
     * Checks if the user's account is not expired (based on the 'enabled' flag).
     *
     * @return `true` if the account is not expired, `false` otherwise.
     */
    @Override
    public boolean isAccountNonExpired() {
        return this.enabled;
    }

    /**
     * Checks if the user's account is not locked (based on the 'enabled' flag).
     *
     * @return `true` if the account is not locked, `false` otherwise.
     */
    @Override
    public boolean isAccountNonLocked() {
        return this.enabled;
    }

    /**
     * Checks if the user's credentials are not expired (based on the 'enabled' flag).
     *
     * @return `true` if the credentials are not expired, `false` otherwise.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return this.enabled;
    }

    /**
     * Checks if the user is enabled (based on the 'enabled' flag).
     *
     * @return `true` if the user is enabled, `false` otherwise.
     */
    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * A list of orders associated with the user.
     */
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    /**
     * Returns the name of the user.
     *
     * @return The name of the user.
     */
    @Override
    public String toString() {
        return name;
    }
}


