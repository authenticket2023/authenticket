package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface UserService {
    Optional<UserDisplayDto> findById(Integer userId);
    UserDisplayDto updateUser(User newUser);
    //    Admin updateAdmin (AdminDto adminDto);
    void deleteUser(Integer userId);
    UserDisplayDto updateProfileImage(String filename, Integer userId);
    // buy tickets
}
