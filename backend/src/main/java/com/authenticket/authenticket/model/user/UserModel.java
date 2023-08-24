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
@Table(name = "app_user")
public class UserModel implements UserDetails {


    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    private Long user_id;
    private String name;
    private String email;
    private String password;

    private LocalDate date_of_birth;
    private LocalDateTime user_created_date;
    private LocalDateTime deleted_date;
    private String profile_image;
    private Boolean enabled = false;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority("user");
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

