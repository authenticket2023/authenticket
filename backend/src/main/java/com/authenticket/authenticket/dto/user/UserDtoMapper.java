package com.authenticket.authenticket.dto.user;

import com.authenticket.authenticket.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.function.Function;

/**
 * This class is responsible for mapping between User and UserDisplayDto/UserFullDisplayDto.
 */

@Service
public class UserDtoMapper implements Function<User, UserDisplayDto> {
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserDtoMapper(PasswordEncoder passwordEncoder){
        this.passwordEncoder=passwordEncoder;
    }

    /**
     * Maps a User object to a UserDisplayDto object.
     *
     * @param user The User object to map.
     * @return A UserDisplayDto object containing user information.
     */
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

    /**
     * Maps a User object to a UserFullDisplayDto object.
     *
     * @param user The User object to map.
     * @return A UserFullDisplayDto object containing comprehensive user information.
     */
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

    /**
     * Updates the properties of an existing user with new values.
     *
     * @param newUser The User object containing updated values.
     * @param oldUser The existing User object to be updated.
     */
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
