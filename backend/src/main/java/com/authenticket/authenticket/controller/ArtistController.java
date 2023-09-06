package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.user.UserDisplayDto;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.ArtistRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.ArtistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
@RestController
@RequestMapping(path = "/api/artist")
public class ArtistController extends Utility {
    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private ArtistServiceImpl artistService;

    @Autowired
    private AmazonS3ServiceImpl amazonS3Service;
    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping("/{artistId}")
    public ResponseEntity<GeneralApiResponse> findArtistById(@PathVariable("artistId") Integer artistId) {
        Optional<ArtistDisplayDto> artistDisplayDto = artistService.findById(artistId);
        if(artistDisplayDto.isPresent()){
            return ResponseEntity.status(200).body(generateApiResponse(artistDisplayDto.get(), "User found"));
        }
        return ResponseEntity.status(400).body(generateApiResponse(null, "User does not exist"));
    }

    @PostMapping
    public ResponseEntity<GeneralApiResponse> saveArtist(@RequestParam("name") String name) {
        Artist newArtist = new Artist(null, name, null, null);
        Artist saveArtist = artistService.saveArtist(newArtist);
        return ResponseEntity.ok(generateApiResponse(saveArtist,"Artist created successfully"));


    }

    @PutMapping("/{artistId}")
    public ResponseEntity<GeneralApiResponse> deleteUser(@PathVariable("artistId") Integer artistId) {
        artistService.deleteArtist(artistId);
        return ResponseEntity.ok(generateApiResponse(null, String.format("Artist %d Deleted Successfully", artistId)));

    }

//    @PutMapping("/updateUserImage")
//    public ResponseEntity<GeneralApiResponse<Object>> updateProfileImage(@RequestParam("profileImage") MultipartFile profileImage,
//                                                                         @RequestParam("imageName") String imageName,
//                                                                         @RequestParam("userId") Integer userId) {
//        try{
//            if(userRepository.findById(userId).isPresent()){
//                amazonS3Service.uploadFile(profileImage, imageName, "user_profile");
//                return ResponseEntity.ok(generateApiResponse(userService.updateProfileImage(imageName, userId),"Profile Image Uploaded Successfully."));
//            } else {
//                return ResponseEntity.status(400).body(generateApiResponse(userService.updateProfileImage(imageName, userId),"Profile Image Uploaded Failed."));
//            }
//
//        } catch (AmazonS3Exception e) {
//            String errorCode = e.getErrorCode();
//            if ("AccessDenied".equals(errorCode)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(generateApiResponse(null, "Access Denied to Amazon."));
//            } else if ("NoSuchBucket".equals(errorCode)) {
//
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "S3 bucket not found."));
//            } else {
//                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateApiResponse(null, "An error occurred during S3 interaction."));
//            }
//        }
//
//    }

}
