package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.event.EventUpdateDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AdminServiceImpl;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.EventOrganiserServiceImpl;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/admin")
public class AdminController extends Utility {

    private final AdminServiceImpl adminService;

    private final AdminDtoMapper adminDtoMapper;

    private final AdminRepository adminRepository;

    private final EventOrganiserServiceImpl eventOrganiserService;

    private final VenueRepository venueRepository;

    private final EventServiceImpl eventService;

    private final AmazonS3ServiceImpl amazonS3Service;

    private final EventTypeRepository eventTypeRepository;

    private final PasswordEncoder passwordEncoder;


    @Autowired
    public AdminController(AdminServiceImpl adminService,
                           AdminDtoMapper adminDtoMapper,
                           AdminRepository adminRepository,
                           PasswordEncoder passwordEncoder,
                           EventOrganiserServiceImpl eventOrganiserService,
                           VenueRepository venueRepository,
                           EventServiceImpl eventService,
                           AmazonS3ServiceImpl amazonS3Service,
                           EventTypeRepository eventTypeRepository) {
        this.adminService = adminService;
        this.adminDtoMapper = adminDtoMapper;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventOrganiserService = eventOrganiserService;
        this.venueRepository = venueRepository;
        this.eventService = eventService;
        this.amazonS3Service = amazonS3Service;
        this.eventTypeRepository = eventTypeRepository;
    }

    //have to implement response entity
    @GetMapping("/test")
    public String test() {

        return "test successful";
    }

    @GetMapping
    public List<AdminDisplayDto> findAllAdmin() {
        return adminService.findAllAdmin();
    }

    @GetMapping("/{admin_id}")
    public ResponseEntity<GeneralApiResponse> findAdminById(@PathVariable(value = "admin_id") Integer admin_id){
        Optional<AdminDisplayDto> adminDisplayDto = adminService.findAdminById(admin_id);
        if(adminDisplayDto.isPresent()){
            return ResponseEntity.status(200).body(generateApiResponse(adminDisplayDto.get(), "Admin found"));
        }
        return ResponseEntity.status(404).body(generateApiResponse(null, "Admin does not exist"));
    }

    @PostMapping("/saveAdmin")
    public ResponseEntity<GeneralApiResponse> saveAdmin(@RequestParam(value = "name") String name,
                                                        @RequestParam("email") String email,
                                                        @RequestParam("password") String password) {
        if(adminRepository.findByEmail(email).isEmpty()){
//            try{
                Admin newAdmin = Admin
                        .builder()
                        .adminId(null)
                        .name(name)
                        .email(email)
                        .password(passwordEncoder.encode(password))
                        .build();
                Admin savedAdmin =  adminService.saveAdmin(newAdmin);
                return ResponseEntity.status(200).body(generateApiResponse(adminDtoMapper.apply(savedAdmin), "Admin has been saved"));
//            } catch (Exception e){
//                return ResponseEntity.status(400).body(generateApiResponse(null, "Something went wrong"));
//            }
        }
        return ResponseEntity.status(401).body(generateApiResponse(null, "Admin already exist"));
    }

    @PutMapping("/updateAdmin")
    public ResponseEntity<GeneralApiResponse> updateAdmin(@RequestBody Admin newAdmin){
        if(adminRepository.findByEmail(newAdmin.getEmail()).isPresent()){
            AdminDisplayDto updatedAdmin = adminService.updateAdmin(newAdmin);
            return ResponseEntity.status(200).body(generateApiResponse(updatedAdmin, "admin has been successfully updated"));
        }

        return ResponseEntity.status(404).body(generateApiResponse(null, "Admin does not exist"));
    }

    @PutMapping("/updateEventOrganiser")
    public ResponseEntity<GeneralApiResponse> updateEventOrganiser(@RequestParam("organiserId") Integer organiserId,
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
            if(adminOptional.isEmpty()) {
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

    @PutMapping("/updateEvent")
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
//                amazonS3Service.deleteFile()
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
        return ResponseEntity.ok(generateApiResponse(event, "Event updated successfully."));
    }

    @GetMapping("/event-organiser/review-status/{status}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventOrganisersByReviewStatus(@PathVariable("status") String status) {
        List<EventOrganiserDisplayDto> organiserList = eventOrganiserService.findEventOrganisersByReviewStatus(status);
        return ResponseEntity.ok(generateApiResponse(organiserList, String.format("%s organisers successfully returned.", status)));
    }

    @GetMapping("/event/review-status/{status}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventsByReviewStatus(@PathVariable("status") String status) {
        List<EventDisplayDto> eventList = eventService.findEventsByReviewStatus(status);
        return ResponseEntity.ok(generateApiResponse(eventList, String.format("%s events successfully returned.", status)));
    }
}
