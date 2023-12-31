package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.service.impl.EventOrganiserServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**This is the event organiser controller class and the base path for this controller's endpoint is api/v2/event-organiser.*/

@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/v2/event-organiser")
public class EventOrganiserController extends Utility {
    private final EventOrganiserServiceImpl eventOrganiserService;

    private final AmazonS3Service amazonS3Service;

    @Autowired
    public EventOrganiserController(EventOrganiserServiceImpl eventOrganiserService,
                                    AmazonS3Service amazonS3Service) {
        this.eventOrganiserService = eventOrganiserService;
        this.amazonS3Service = amazonS3Service;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    /**
     * Retrieves a list of all event organizers.
     *
     * @return A ResponseEntity containing a GeneralApiResponse with a list of EventOrganiserDisplayDto objects.
     */
    @GetMapping
    public ResponseEntity<GeneralApiResponse<Object>> findAllEventOrganiser() {

        List<EventOrganiserDisplayDto> eventOrganiserList = eventOrganiserService.findAllEventOrganisers();
        if (eventOrganiserList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(eventOrganiserList, "No event organisers found."));

        } else {
            return ResponseEntity.ok(generateApiResponse(eventOrganiserList, "Event organisers successfully returned."));

        }
    }

    /**
     * Retrieves an event organizer by their ID.
     *
     * @param organiserId The ID of the event organizer to retrieve.
     * @return A ResponseEntity containing a GeneralApiResponse with the EventOrganiserDisplayDto or an error message.
     */
    @GetMapping("/{organiserId}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventOrganiserById(@PathVariable("organiserId") Integer organiserId) {
        Optional<EventOrganiserDisplayDto> organiserDisplayDtoOptional = eventOrganiserService.findOrganiserById(organiserId);
        return organiserDisplayDtoOptional.map(eventOrganiserDisplayDto ->
                        ResponseEntity.ok(generateApiResponse(eventOrganiserDisplayDto, String.format("Event organiser %d successfully returned.", organiserId))))
                .orElseGet(() -> ResponseEntity.ok(generateApiResponse(null, String.format("Event organiser with id %d not found", organiserId))));

    }

    /**
     * Retrieves a list of events associated with a specific event organizer.
     *
     * @param organiserId The ID of the event organizer whose events are to be retrieved.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of Event objects or an error message.
     */
    @GetMapping("/events/{organiserId}")
    public ResponseEntity<GeneralApiResponse<Object>> findAllEventsByOrganiser(@PathVariable("organiserId") Integer organiserId, @NonNull HttpServletRequest request) {
        if (!isAdminRequest(request) && retrieveOrganiserFromRequest(request).getOrganiserId() != organiserId) {
            throw new IllegalArgumentException("Unable to cancel other user's order");
        }

        List<Event> events = eventOrganiserService.findAllEventsByOrganiser(organiserId);
        if (!events.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(events, String.format("All events hosted by organiser %d retrieved successfully", organiserId)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("The organiser with ID %d does not have associated events or the organiser does not exist", organiserId)));

    }
  
      /**
     * Retrieves a list of current events associated with a specific event organizer. (events with event date after current date)
     *
     * @param organiserId The ID of the event organizer whose events are to be retrieved.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of Event objects or an error message.
     */
  
      @GetMapping("/current-events/{organiserId}")
    public ResponseEntity<GeneralApiResponse<Object>> findAllCurrentEventsByOrganiser(@PathVariable("organiserId") Integer organiserId, @NonNull HttpServletRequest request) {
        if (!isAdminRequest(request) && retrieveOrganiserFromRequest(request).getOrganiserId() != organiserId) {
            throw new IllegalArgumentException("Unable to cancel other user's order");
        }

        List<Event> events = eventOrganiserService.findAllCurrentEventsByOrganiser(organiserId);
        if (!events.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(events, String.format("All current events hosted by organiser %d retrieved successfully", organiserId)));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("The organiser with ID %d does not have associated current events or the organiser does not exist", organiserId)));

    }

    /**
     * Updates an event organizer's information.
     *
     * @param organiserId The ID of the event organizer to be updated.
     * @param name The new name of the event organizer (optional).
     * @param description The new description of the event organizer (optional).
     * @param password The new password for the event organizer (optional).
     * @return A ResponseEntity containing a GeneralApiResponse with the updated EventOrganiser or an error message.
     */

    @PutMapping
    public ResponseEntity<GeneralApiResponse<Object>> updateEventOrganiser(@RequestParam(value = "organiserId") Integer organiserId,
                                                                           @RequestParam(value = "name", required = false) String name,
                                                                           @RequestParam(value = "description", required = false) String description,
                                                                           @RequestParam(value = "password", required = false) String password) {
        EventOrganiserUpdateDto eventOrganiserUpdateDto = new EventOrganiserUpdateDto(organiserId, name, description, password, null, null, null, null);
        EventOrganiser eventOrganiser = eventOrganiserService.updateEventOrganiser(eventOrganiserUpdateDto);
        if (eventOrganiser != null) {
            return ResponseEntity.ok(generateApiResponse(eventOrganiser, String.format("Event organiser %d updated successfully.", organiserId)));
        } else {
            return ResponseEntity.badRequest().body(generateApiResponse(null, "Event organiser update unsuccessful"));
        }
    }

    /**
     * Updates an event organizer's profile image.
     *
     * @param file The MultipartFile containing the new image file.
     * @param request The HttpServletRequest containing the request information.
     * @return A ResponseEntity containing a GeneralApiResponse with the updated EventOrganiser or an error message.
     */
    @PutMapping("/image")
    public ResponseEntity<GeneralApiResponse<Object>> updateOrganiserImage(@RequestParam("file") MultipartFile file,
                                                                           @NonNull HttpServletRequest request) {
        EventOrganiser eventOrganiser = retrieveOrganiserFromRequest(request);
        try {
            //generating the file name with the extension
            String fileExtension = getFileExtension(file.getContentType());
            String imageName = eventOrganiser.getOrganiserId() + fileExtension;

            //update eventOrganiser with image name and save to db
            eventOrganiserService.updateEventOrganiserImage(eventOrganiser.getOrganiserId(), imageName);

            amazonS3Service.uploadFile(file, imageName, "event_organiser_profile");
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

        return ResponseEntity.ok(generateApiResponse(eventOrganiser, String.format("Event organiser %d updated successfully.", eventOrganiser.getOrganiserId())));
    }


    /**
     * Deletes one or more event organizers by their IDs.
     *
     * @param organiserIdString A comma-separated string of event organizer IDs to be deleted.
     * @return A ResponseEntity containing a GeneralApiResponse with the results of the deletion process or an error message.
     */
    @PutMapping("/delete")
    public ResponseEntity<GeneralApiResponse<Object>> deleteEventOrganiser(@RequestParam("organiserId") String organiserIdString) {
        try {
            List<Integer> organiserIdList = Arrays.stream(organiserIdString.split(","))
                    .map(Integer::parseInt)
                    .toList();

            //check if all events exist first
            for (Integer organiserId : organiserIdList) {
                if (eventOrganiserService.findOrganiserById(organiserId).isEmpty()) {
                    throw new NonExistentException(String.format("Organiser %d does not exist, deletion halted", organiserId));
                }
            }

            StringBuilder results = new StringBuilder();

            for (Integer organiserId : organiserIdList) {
                results.append(eventOrganiserService.deleteEventOrganiser(organiserId)).append(" ");
            }

            return ResponseEntity.ok(generateApiResponse(null, results.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generateApiResponse(null, e.getMessage()));
        }
    }
}