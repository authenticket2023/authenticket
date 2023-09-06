
package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.VenueServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
//    @CrossOrigin
@RequestMapping(path = "/api/venue")

public class VenueController extends Utility {
    @Autowired
    private VenueServiceImpl venueService;

    @Autowired
    private AmazonS3ServiceImpl amazonS3Service;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping
    public ResponseEntity<GeneralApiResponse<Object>> findAllVenue() {
        List<VenueDisplayDto> venueList = venueService.findAllVenue();
        if (venueList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(venueList, "No venue found."));
        } else {
            return ResponseEntity.ok(generateApiResponse(venueList, "Venue successfully returned."));
        }
    }

    @GetMapping("/{venue_id}")
    public Optional<VenueDisplayDto> findVenueById(@PathVariable("venue_id") Integer venueId) {
        return venueService.findById(venueId);
    }

    @PostMapping
    public ResponseEntity<?> saveVenue(@RequestParam(value = "venueName") String venueName,
                                        @RequestParam(value = "venueLocation") String venueLocation,
                                        @RequestParam(value = "venueImage") String venueImage) {
        //Venue image change to file
        Venue savedVenue = venueService.saveVenue(venueName, venueLocation, venueImage);
        return ResponseEntity.ok(savedVenue);
    }

    @PutMapping("/update")
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
