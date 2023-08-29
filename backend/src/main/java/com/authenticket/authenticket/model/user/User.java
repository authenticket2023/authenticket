package com.authenticket.authenticket.model.user;

        import jakarta.persistence.*;
        import lombok.AllArgsConstructor;
        import lombok.Builder;
        import lombok.Data;
        import lombok.NoArgsConstructor;
        import org.springframework.security.core.GrantedAuthority;
        import org.springframework.security.core.authority.SimpleGrantedAuthority;
        import org.springframework.security.core.userdetails.UserDetails;

        import java.time.LocalDate;
        import java.time.LocalDateTime;
        import java.util.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user", schema = "dev")
public class User implements UserDetails {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    @Column(name = "user_id", nullable = false)
    private Long user_id;
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDate date_of_birth;
    @Column(name = "user_created_date")
    private LocalDate user_created_date = LocalDate.now();
    @Column(name = "deleted_date")
    private LocalDateTime deleted_date;
    @Column(name = "profile_image")
    private String profile_image;
    @Column(name = "enabled")
    private Boolean enabled = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("USER");
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return deleted_date == null;
    }

    @Override
    public boolean isAccountNonLocked() {
        return deleted_date == null;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return deleted_date == null;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

