package com.authenticket.authenticket.dto.user;

import com.authenticket.authenticket.model.User;
import org.springframework.stereotype.Service;
import java.util.function.Function;

@Service
public class UserDtoMapper implements Function<User, UserDto> {
    public UserDto apply(User user){
        return new UserDto(
                user.getName(),
                user.getEmail(),
                user.getDateOfBirth(),
                user.getProfileImage()
        );
    }
}
