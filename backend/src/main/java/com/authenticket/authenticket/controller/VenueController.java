
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * The `VenueController` class handles HTTP requests related to venue management.
 */
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
@RequestMapping(path = "/api/v2/venue")
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

    /**
     * A test endpoint to check if the controller is operational.
     *
     * @return A simple test message indicating the operation was successful.
     */
    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    /**
     * Retrieve a list of all venues in the system.
     *
     * @return A response containing a list of venue information.
     */
    @GetMapping
    public ResponseEntity<GeneralApiResponse<Object>> findAllVenue() {
        List<Venue> venueList = venueService.findAllVenue();
        if (venueList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(venueList, "No venue found."));
        } else {
            return ResponseEntity.ok(generateApiResponse(venueList, "Venue successfully returned."));
        }
    }

    /**
     * Find a venue by its ID.
     *
     * @param venueId The ID of the venue to retrieve.
     * @return An optional containing the venue information, if found.
     */
    @GetMapping("/{venue_id}")
    public Optional<Venue> findVenueById(@PathVariable("venue_id") Integer venueId) {
        return venueService.findById(venueId);
    }

    /**
     * Save a new venue, including venue image.
     *
     * @param venueName        The name of the venue.
     * @param venueLocation    The location of the venue.
     * @param venueDescription The description of the venue.
     * @param venueImageFile   The image file for the venue.
     * @return A response indicating the successful creation of the venue.
     */
    @PostMapping
    public ResponseEntity<GeneralApiResponse<Object>> saveVenue(@RequestParam(value = "venueName") String venueName,
                                        @RequestParam(value = "venueLocation") String venueLocation,
                                       @RequestParam(value = "venueDescription") String venueDescription,
                                        @RequestParam(value = "venueImage") MultipartFile venueImageFile) {
        Optional<Venue> venueOptional = venueRepository.findByVenueName(venueName);
        if (venueOptional.isPresent()) {
            throw new AlreadyExistsException("Venue already exists");
        }

        if (venueImageFile.isEmpty()) {
            throw new NonExistentException("Venue Image File is null");
        }

        Venue savedVenue = venueService.saveVenue(new Venue(null, venueName, venueLocation,venueDescription, null,null));
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

        return ResponseEntity.ok(generateApiResponse(savedVenue, "Venue created successfully"));
    }

    /**
     * Update an existing venue, including the venue image.
     *
     * @param venueId        The ID of the venue to update.
     * @param venueName      The updated name of the venue.
     * @param venueLocation  The updated location of the venue.
     * @param venueImageFile The updated image file for the venue.
     * @return A response indicating the successful update of the venue.
     */
    @PutMapping
    public ResponseEntity<GeneralApiResponse<Object>> updateVenue(@RequestParam(value = "venueId") Integer venueId,
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
}
