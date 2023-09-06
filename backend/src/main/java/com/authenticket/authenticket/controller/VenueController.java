
package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.VenueServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
//    @CrossOrigin
@RequestMapping(path = "/api/venue")

public class VenueController {
    @Autowired
    private VenueServiceImpl venueService;

    @Autowired
    private AmazonS3ServiceImpl amazonS3Service;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping("/{venue_id}")
    public Optional<VenueDisplayDto> findUserById(@PathVariable("venue_id") Integer venueId) {
        return venueService.findById(venueId);
    }

    @PutMapping("/updateUserProfile")
    public VenueDisplayDto updateVenue(@RequestBody Venue newVenue) {
        return venueService.updateVenue(newVenue);
    }

    @PutMapping("/{venueId}")
    public String removeVenue(@PathVariable("venueId") Integer venueId) {
        return venueService.removeVenue(venueId);
    }

    @PutMapping("/updateVenueImage")
    public VenueDisplayDto updateVenueImage(@RequestParam("venueImage")MultipartFile venueImage,
                                            @RequestParam("imageName") String imageName,
                                            @RequestParam("venueId") Integer venueId) {
        amazonS3Service.uploadFile(venueImage, imageName, "user_profile");
        return venueService.updateVenueImage(imageName, venueId);
    }

}
