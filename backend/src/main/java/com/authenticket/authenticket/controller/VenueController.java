
package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.VenueServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping(path = "/api/venue")

public class VenueController extends Utility {
    private final VenueServiceImpl venueService;

    private final VenueRepository venueRepository;

    private final AmazonS3ServiceImpl amazonS3Service;

    @Autowired
    public VenueController(VenueServiceImpl venueService, VenueRepository venueRepository, AmazonS3ServiceImpl amazonS3Service) {
        this.venueService = venueService;
        this.venueRepository = venueRepository;
        this.amazonS3Service = amazonS3Service;
    }

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
                                        @RequestParam(value = "venueImage") MultipartFile venueImageFile) {
        Optional<Venue> venueOptional = venueRepository.findByVenueName(venueName);
        if (venueOptional.isPresent()) {
            throw new AlreadyExistsException("Venue already exists");
        }

        if (venueImageFile.isEmpty()) {
            throw new NonExistentException("Venue Image File is null");
        }

        Venue savedVenue = venueService.saveVenue(new Venue(null, venueName, venueLocation, null,null));
        //generating the file name with the extension
        String fileExtension = getFileExtension(venueImageFile.getContentType());
        String imageName = savedVenue.getVenueId() + fileExtension;

        savedVenue.setVenueImage(imageName);
        venueService.saveVenue(savedVenue);

        try {
            amazonS3Service.uploadFile(venueImageFile, imageName, "venue_image");
            // delete event from db if got error saving image
        } catch (AmazonS3Exception e) {
            venueService.removeVenue(savedVenue.getVenueId());

            String errorCode = e.getErrorCode();
            if ("AccessDenied".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(generateApiResponse(null, "Access Denied to Amazon."));
            } else if ("NoSuchBucket".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "S3 bucket not found."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateApiResponse(null, "An error occurred during S3 interaction: " + e.getMessage()));
            }
        } catch (Exception e) {
            venueService.removeVenue(savedVenue.getVenueId());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateApiResponse(null, "An error occurred during S3 interaction: " + e.getMessage()));
        }

        return ResponseEntity.ok(savedVenue);
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateVenue(@RequestParam(value = "venueId") Integer venueId,
                                         @RequestParam(value = "venueName") String venueName,
                                         @RequestParam(value = "venueLocation") String venueLocation,
                                         @RequestParam(value = "venueImage") MultipartFile venueImageFile) {
        Venue updatedVenue = venueService.updateVenue(venueId, venueName, venueLocation);

        //update file image if not null
        if (!venueImageFile.isEmpty()){
            try {
                amazonS3Service.uploadFile(venueImageFile, updatedVenue.getVenueImage(), "venue_image");
                // delete event from db if got error saving image
            } catch (AmazonS3Exception e) {
                String errorCode = e.getErrorCode();
                if ("AccessDenied".equals(errorCode)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(generateApiResponse(null, "Access Denied to Amazon."));
                } else if ("NoSuchBucket".equals(errorCode)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "S3 bucket not found."));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateApiResponse(null, "An error occurred during S3 interaction: " + e.getMessage()));
                }
            }
        }
        return ResponseEntity.ok(generateApiResponse(updatedVenue, "Venue updated successfully."));
    }

    @DeleteMapping("/{venueId}")
    public String removeVenue(@PathVariable("venueId") Integer venueId) {
        venueService.removeVenue(venueId);
        return "Venue removed successfully.";
    }
}
