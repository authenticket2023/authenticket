package com.authenticket.authenticket.dto.user;

import com.authenticket.authenticket.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class UserDtoMapper implements Function<User, UserDisplayDto> {
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserDtoMapper(PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
    }
    public UserDisplayDto apply(User user){
        return new UserDisplayDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getProfileImage(),
                User.getRole()
        );
    }

    public UserFullDisplayDto fullApply(User user){
        return new UserFullDisplayDto(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getProfileImage(),
                User.getRole(),
                user.getEnabled(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getDeletedAt()
        );
    }

    public void update (User newUser, User oldUser){
        if(newUser.getName() != null){
            oldUser.setName(newUser.getName());
        }
        if(newUser.getPassword() != null){
            String passwordEncode = passwordEncoder.encode(newUser.getPassword());
            oldUser.setPassword(passwordEncode);
        }
    }
}
