package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.event.EventUpdateDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AdminServiceImpl;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.EventOrganiserServiceImpl;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**This is the admin  controller class and the base path for this controller's endpoint is api/v2/admin.*/

@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/v2/admin")
public class AdminController extends Utility {

    private final AdminServiceImpl adminService;

    private final AdminRepository adminRepository;

    private final EventOrganiserServiceImpl eventOrganiserService;

    private final VenueRepository venueRepository;

    private final EventServiceImpl eventService;

    private final AmazonS3ServiceImpl amazonS3Service;

    private final EventTypeRepository eventTypeRepository;

    @Autowired
    public AdminController(AdminServiceImpl adminService,
                           AdminRepository adminRepository,
                           EventOrganiserServiceImpl eventOrganiserService,
                           VenueRepository venueRepository,
                           EventServiceImpl eventService,
                           AmazonS3ServiceImpl amazonS3Service,
                           EventTypeRepository eventTypeRepository) {
        this.adminService = adminService;
        this.adminRepository = adminRepository;
        this.eventOrganiserService = eventOrganiserService;
        this.venueRepository = venueRepository;
        this.eventService = eventService;
        this.amazonS3Service = amazonS3Service;
        this.eventTypeRepository = eventTypeRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    /**
     * Retrieves a list of AdminDisplayDto objects representing administrators.
     *
     * @return A List of AdminDisplayDto objects containing administrator information.
     */
    @GetMapping
    public List<AdminDisplayDto> findAllAdmin() {
        return adminService.findAllAdmin();
    }

    /**
     * Retrieve an administrator by their unique identifier.
     *
     * @param admin_id The unique identifier of the administrator to retrieve.
     * @return A ResponseEntity containing a GeneralApiResponse with the administrator's data if found,
     *         or an error message if the administrator does not exist.
     */
    @GetMapping("/{admin_id}")
    public ResponseEntity<GeneralApiResponse<Object>> findAdminById(@PathVariable(value = "admin_id") Integer admin_id) {
        Optional<AdminDisplayDto> adminDisplayDto = adminService.findAdminById(admin_id);
        if (adminDisplayDto.isPresent()) {
            return ResponseEntity.status(200).body(generateApiResponse(adminDisplayDto.get(), "Admin found"));
        }
        return ResponseEntity.status(404).body(generateApiResponse(null, "Admin does not exist"));
    }

    /**
     * Update an administrator's information.
     *
     * @param newAdmin The new administrator data to update.
     * @return A ResponseEntity containing a GeneralApiResponse with the updated administrator's data
     *         and a success message if the update is successful, or an error message if the administrator
     *         does not exist in the repository.
     */
    @PutMapping
    public ResponseEntity<GeneralApiResponse<Object>> updateAdmin(@RequestBody Admin newAdmin) {
        if (adminRepository.findByEmail(newAdmin.getEmail()).isPresent()) {
            AdminDisplayDto updatedAdmin = adminService.updateAdmin(newAdmin);
            return ResponseEntity.status(200).body(generateApiResponse(updatedAdmin, "admin has been successfully updated"));
        }

        return ResponseEntity.status(404).body(generateApiResponse(null, "Admin does not exist"));
    }


    /**
     * Update an event organizer's information by specifying various parameters.
     *
     * @param organiserId The unique identifier of the event organizer to update.
     * @param name The new name for the event organizer (optional).
     * @param description The new description for the event organizer (optional).
     * @param password The new password for the event organizer (optional).
     * @param reviewStatus The new review status for the event organizer (optional).
     * @param reviewRemarks The new review remarks for the event organizer (optional).
     * @param reviewedBy The unique identifier of the admin who reviewed the event organizer (optional).
     * @param enabled The new status of the event organizer (optional).
     * @return A ResponseEntity containing a GeneralApiResponse with the updated event organizer's data
     *         and a success message if the update is successful, or an error message if the update fails.
     * @throws NonExistentException if the 'reviewedBy' parameter is provided and does not correspond to an existing admin.
     */
    @PutMapping("/update-organiser")
    public ResponseEntity<GeneralApiResponse<Object>> updateEventOrganiser(@RequestParam("organiserId") Integer organiserId,
                                                                   @RequestParam(value = "name", required = false) String name,
                                                                   @RequestParam(value = "description", required = false) String description,
                                                                   @RequestParam(value = "password", required = false) String password,
                                                                   @RequestParam(value = "reviewStatus", required = false) String reviewStatus,
                                                                   @RequestParam(value = "reviewRemarks", required = false) String reviewRemarks,
                                                                   @RequestParam(value = "reviewedBy", required = false) Integer reviewedBy,
                                                                   @RequestParam(value = "enabled", required = false) Boolean enabled) {
        Admin admin = null;
        if (reviewedBy != null) {
            Optional<Admin> adminOptional = adminRepository.findById(reviewedBy);
            if (adminOptional.isEmpty()) {
                throw new NonExistentException("Admin with ID " + reviewedBy + " does not exist");
            }
            admin = adminOptional.get();
        }

        EventOrganiserUpdateDto eventOrganiserUpdateDto = new EventOrganiserUpdateDto(organiserId, name, description, password, enabled, reviewStatus, reviewRemarks, admin);
        EventOrganiser eventOrganiser = eventOrganiserService.updateEventOrganiser(eventOrganiserUpdateDto);
        if (eventOrganiser != null) {
            return ResponseEntity.ok(generateApiResponse(eventOrganiser, String.format("Event organiser %d updated successfully.", organiserId)));
        } else {
            return ResponseEntity.badRequest().body(generateApiResponse(null, "Event organiser update unsuccessful"));
        }
    }

    /**
     * Update event information, including optional event image upload.
     *
     * @param eventImageFile The new event image file (optional).
     * @param eventId The unique identifier of the event to update.
     * @param eventName The new name for the event (optional).
     * @param eventDescription The new description for the event (optional).
     * @param eventDate The new date for the event (optional).
     * @param eventLocation The new location for the event (optional).
     * @param otherEventInfo Other additional information about the event (optional).
     * @param ticketSaleDate The new date when ticket sales start (optional).
     * @param venueId The unique identifier of the venue for the event (optional).
     * @param typeId The unique identifier of the event type (optional).
     * @param reviewRemarks The new review remarks for the event (optional).
     * @param reviewStatus The new review status for the event (optional).
     * @param reviewedBy The unique identifier of the admin who reviewed the event (optional).
     * @return A ResponseEntity containing a GeneralApiResponse with the updated event's data
     *         and a success message if the update is successful, or an error message if the update fails.
     * @throws NonExistentException if the 'venueId', 'typeId', or 'reviewedBy' parameter is provided and
     *         does not correspond to an existing venue, event type, or admin, respectively.
     */

    @PutMapping("/update-event")
    public ResponseEntity<?> updateEvent(@RequestParam(value = "file", required = false) MultipartFile eventImageFile,
                                         @RequestParam(value = "eventId") Integer eventId,
                                         @RequestParam(value = "eventName", required = false) String eventName,
                                         @RequestParam(value = "eventDescription", required = false) String eventDescription,
                                         @RequestParam(value = "eventDate", required = false) LocalDateTime eventDate,
                                         @RequestParam(value = "eventLocation", required = false) String eventLocation,
                                         @RequestParam(value = "otherEventInfo", required = false) String otherEventInfo,
                                         @RequestParam(value = "ticketSaleDate", required = false) LocalDateTime ticketSaleDate,
                                         @RequestParam(value = "venueId", required = false) Integer venueId,
                                         @RequestParam(value = "typeId", required = false) Integer typeId,
                                         @RequestParam(value = "reviewRemarks", required = false) String reviewRemarks,
                                         @RequestParam(value = "reviewStatus", required = false) String reviewStatus,
                                         @RequestParam(value = "reviewedBy", required = false) Integer reviewedBy) {
        Venue venue = null;
        if (venueId != null) {
            Optional<Venue> venueOptional = venueRepository.findById(venueId);
            if (venueOptional.isPresent()) {
                venue = venueOptional.get();
            } else {
                throw new NonExistentException("Venue does not exist");
            }
        }

        EventType eventType = null;
        if (typeId != null) {
            Optional<EventType> eventTypeOptional = eventTypeRepository.findById(typeId);
            if (eventTypeOptional.isPresent()) {
                eventType = eventTypeOptional.get();
            } else {
                throw new NonExistentException("Event type does not exist");
            }
        }

        Admin admin = null;
        if (reviewedBy != null) {
            Optional<Admin> adminOptional = adminRepository.findById(reviewedBy);

            if (adminOptional.isPresent()) {
                admin = adminOptional.get();
            } else {
                throw new NonExistentException("Admin does not exist");
            }
        }

        EventUpdateDto eventUpdateDto = new EventUpdateDto(eventId, eventName, eventDescription, eventDate, eventLocation, otherEventInfo, ticketSaleDate, venue, eventType, reviewRemarks, reviewStatus, admin);
        Event event = eventService.updateEvent(eventUpdateDto);
        //update event image if not null
        if (eventImageFile != null) {
            try {
                System.out.println(eventImageFile.getName());
                amazonS3Service.uploadFile(eventImageFile, event.getEventImage(), "event_images");
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
        return ResponseEntity.ok(generateApiResponse(event, "Event updated successfully."));
    }

    /**
     * Retrieve a list of event organizers by their review status.
     *
     * @param status The review status to filter the event organizers by. Valid statuses are "approved","pending" and "rejected".
     * @return A ResponseEntity containing a GeneralApiResponse with a list of event organizers matching the given review status,
     *         or a message indicating that no organizers were found with the specified review status.
     */
    @GetMapping("/event-organiser/review-status/{status}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventOrganisersByReviewStatus(@PathVariable("status") String status) {
        List<EventOrganiserDisplayDto> organiserList = eventOrganiserService.findEventOrganisersByReviewStatus(status);
        if (organiserList == null || organiserList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, String.format("No %s organisers found.", status)));
        }
        return ResponseEntity.ok(generateApiResponse(organiserList, String.format("%s organisers successfully returned.", status)));
    }

    /**
     * Retrieve a list of events by their review status.
     *
     * @param status The review status to filter the events by. Valid statuses are "approved","pending" and "rejected".
     * @return A ResponseEntity containing a GeneralApiResponse with a list of events matching the given review status,
     *         or a message indicating that no events were found with the specified review status.
     */

    @GetMapping("/event/review-status/{status}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventsByReviewStatus(@PathVariable("status") String status) {
        List<EventDisplayDto> eventList = eventService.findEventsByReviewStatus(status);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, String.format("No %s events found.", status)));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, String.format("%s events successfully returned.", status)));
    }
}
