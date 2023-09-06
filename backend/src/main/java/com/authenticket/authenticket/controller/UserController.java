package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
//    @CrossOrigin
@RequestMapping(path = "/api/user")

public class UserController extends Utility {
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

    @GetMapping("/{userId}")
    public ResponseEntity<GeneralApiResponse> findUserById(@PathVariable("userId") Integer userId) {
        Optional<UserDisplayDto> userDisplayDto = userService.findById(userId);
        if(userDisplayDto.isPresent()){
            return ResponseEntity.status(200).body(generateApiResponse(userDisplayDto.get(), "User found"));
        }
        return ResponseEntity.status(400).body(generateApiResponse(null, "User does not exist"));
    }

    @PutMapping("/updateUserProfile")
    public ResponseEntity<GeneralApiResponse> updateUser(@RequestBody User newUser) {
        if(userRepository.findByEmail(newUser.getEmail()).isPresent()){
            UserDisplayDto updatedUser = userService.updateUser(newUser);
            return ResponseEntity.status(200).body(generateApiResponse(updatedUser, "User has been successfully updated"));
        }

        return ResponseEntity.status(404).body(generateApiResponse(null, "User does not exist"));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<GeneralApiResponse> removeUser(@PathVariable("userId") Integer userId) {
            userService.deleteUser(userId);
            return ResponseEntity.ok(generateApiResponse(null, String.format("User %d Deleted Successfully", userId)));

    }

    @PutMapping("/updateUserImage")
    public ResponseEntity<GeneralApiResponse<Object>> updateProfileImage(@RequestParam("profileImage")MultipartFile profileImage,
                                             @RequestParam("imageName") String imageName,
                                             @RequestParam("userId") Integer userId) {
        try{
            if(userRepository.findById(userId).isPresent()){
                amazonS3Service.uploadFile(profileImage, imageName, "user_profile");
                return ResponseEntity.ok(generateApiResponse(userService.updateProfileImage(imageName, userId),"Profile Image Uploaded Successfully."));
            } else {
                return ResponseEntity.status(400).body(generateApiResponse(userService.updateProfileImage(imageName, userId),"Profile Image Uploaded Failed."));
            }

        } catch (AmazonS3Exception e) {
            String errorCode = e.getErrorCode();
            if ("AccessDenied".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(generateApiResponse(null, "Access Denied to Amazon."));
            } else if ("NoSuchBucket".equals(errorCode)) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "S3 bucket not found."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateApiResponse(null, "An error occurred during S3 interaction."));
            }
        }

    }

}
