package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
//    @CrossOrigin
@RequestMapping(path = "/api/user")

public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private AmazonS3ServiceImpl amazonS3Service;
    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping("/{user_id}")
    public Optional<UserDisplayDto> findUserById(@PathVariable("user_id") Integer user_id) {
        return userService.findById(user_id);
    }

    @PutMapping("/updateUserProfile")
    public UserDisplayDto updateUser(@RequestBody User newUser) {
        return userService.updateUser(newUser);
    }

    @PutMapping("/{userId}")
    public String removeUser(@PathVariable("userId") Integer userId) {
        return userService.removeUser(userId);
    }

    @PutMapping("/updateUserImage")
    public UserDisplayDto updateProfileImage(@RequestParam("profileImage")MultipartFile profileImage,
                                             @RequestParam("imageName") String imageName,
                                             @RequestParam("userId") Integer userId) {
        amazonS3Service.uploadFile(profileImage, imageName, "user_profile");
        return userService.updateProfileImage(imageName, userId);
    }

}
