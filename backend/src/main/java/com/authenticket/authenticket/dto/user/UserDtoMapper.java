package com.authenticket.authenticket.dto.user;

import com.authenticket.authenticket.model.User;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class UserDtoMapper implements Function<User, UserDisplayDto> {
    public UserDisplayDto apply(User user){
        return new UserDisplayDto(
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getProfileImage()
        );
    }

    public void update (User newUser, User oldUser){
        if(newUser.getName() != null){
            oldUser.setName(newUser.getName());
        }
        if(newUser.getPassword() != null){
            oldUser.setPassword(newUser.getPassword());
        }
    }
}
