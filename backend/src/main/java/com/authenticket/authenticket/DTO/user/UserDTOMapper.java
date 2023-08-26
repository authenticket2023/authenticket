package com.authenticket.authenticket.DTO.user;

import com.authenticket.authenticket.model.user.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {
    public UserDTO apply(User user){
        return new UserDTO(
                user.getName(), user.getEmail(), user.getDate_of_birth(), user.getProfile_image()
        );
    }
}
