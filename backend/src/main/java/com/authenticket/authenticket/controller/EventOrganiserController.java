package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Admin;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.service.impl.EventOrganiserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/event-organiser")
public class EventOrganiserController extends Utility {
    private final EventOrganiserServiceImpl eventOrganiserService;

    private final AmazonS3Service amazonS3Service;

    private final PasswordEncoder passwordEncoder;

    private final AdminRepository adminRepository;

    @Autowired
    public EventOrganiserController(EventOrganiserServiceImpl eventOrganiserService, AmazonS3Service amazonS3Service, PasswordEncoder passwordEncoder, AdminRepository adminRepository) {
        this.eventOrganiserService = eventOrganiserService;
        this.amazonS3Service = amazonS3Service;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }


    @GetMapping
    public ResponseEntity<GeneralApiResponse<Object>> findAllEventOrganiser() {

        List<EventOrganiserDisplayDto> eventOrganiserList = eventOrganiserService.findAllEventOrganisers();
        if(eventOrganiserList.isEmpty()){
            return ResponseEntity.ok(generateApiResponse(eventOrganiserList, "No event organisers found."));

        } else{
            return ResponseEntity.ok(generateApiResponse(eventOrganiserList, "Event organisers successfully returned."));

        }
    }

    @GetMapping("/{organiserId}")
    public ResponseEntity<?> findEventOrganiserById(@PathVariable("organiserId") Integer organiserId) {
        Optional<EventOrganiserDisplayDto> organiserDisplayDtoOptional = eventOrganiserService.findOrganiserById(organiserId);
        return organiserDisplayDtoOptional.map(eventOrganiserDisplayDto -> ResponseEntity.ok(generateApiResponse(eventOrganiserDisplayDto, String.format("Event organiser %d successfully returned.", organiserId)))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("Event organiser with id %d not found", organiserId))));

    }

    @GetMapping("/events/{organiserId}")
    public ResponseEntity<?> findAllEventsByOrganiser(@PathVariable("organiserId") Integer organiserId) {
            List<Event> events = eventOrganiserService.findAllEventsByOrganiser(organiserId);
            if (!events.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(events, String.format("All events hosted by organiser %d retrieved successfully", organiserId)));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("The organiser with ID %d does not have associated events or the organiser does not exist", organiserId)));

    }

//    @GetMapping("/events/findMappedOrganisers")
//    public ResponseEntity<GeneralApiResponse> findMappedOrganisers() {
//        List<ArtistEvent> artistEventList = artistEventRepository.findAll();
//
//        List<Artist> artists = artistRepository.findAll();
//
//        if (!events.isEmpty()) {
//            return ResponseEntity.ok(generateApiResponse(events, String.format("All events hosted by organiser %d retrieved successfully", organiserId)));
//        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("The organiser with ID %d does not have associated events or the organiser does not exist", organiserId)));
//
//    }

    @PostMapping
    public ResponseEntity<?> saveEventOrganiser(@RequestParam("name") String name,
                                       @RequestParam("email") String email,
                                       @RequestParam("description") String description) {

            //save eventOrganiser first without image name to get the eventOrganiser id
            EventOrganiser newEventOrganiser = new EventOrganiser(null, name, email, passwordEncoder.encode(generateRandomPassword()), description,null,false,null,"pending",null,null);
            EventOrganiser savedEventOrganiser = eventOrganiserService.saveEventOrganiser(newEventOrganiser);

            return ResponseEntity.ok(generateApiResponse(savedEventOrganiser,"Event organiser created successfully"));
    }

    @PutMapping
    public ResponseEntity<?> updateEventOrganiser(@RequestParam(value = "organiserId") Integer organiserId,
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

    @PutMapping("/image")
    public ResponseEntity<?> updateOrganiserImage(@RequestParam("organiserId") Integer organiserId,
                                                @RequestParam("file") MultipartFile file) {
        EventOrganiser eventOrganiser;
        try {
            //generating the file name with the extension
            String fileExtension = getFileExtension(file.getContentType());
            String imageName = organiserId+fileExtension;

            //update eventOrganiser with image name and save to db
            eventOrganiser = eventOrganiserService.updateEventOrganiserImage(organiserId,imageName);

            if(eventOrganiser !=null){
                amazonS3Service.uploadFile(file,imageName,"event_organiser_profile");
            } else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "Event Organiser does not exist, file upload fail"));
            }

        }catch (AmazonS3Exception e) {
            String errorCode = e.getErrorCode();
            if ("AccessDenied".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(generateApiResponse(null, "Access Denied to Amazon."));
            } else if ("NoSuchBucket".equals(errorCode)) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "S3 bucket not found."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateApiResponse(null, "An error occurred during S3 interaction."));
            }
        }

        return ResponseEntity.ok(generateApiResponse(eventOrganiser, String.format("Event organiser %d updated successfully.", organiserId)));
    }



    @PutMapping("/delete")
    public ResponseEntity<GeneralApiResponse> deleteEventOrganiser(@RequestParam("organiserId") String organiserIdString) {
        try {
            List<Integer> organiserIdList = Arrays.stream(organiserIdString.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            //check if all events exist first
            for(Integer organiserId: organiserIdList){
                if(eventOrganiserService.findOrganiserById(organiserId).isEmpty()){
                    throw new NonExistentException(String.format("Organiser %d does not exist, deletion halted", organiserId));
                }
            }

            StringBuilder results = new StringBuilder();

            for(Integer organiserId: organiserIdList){
                results.append(eventOrganiserService.deleteEventOrganiser(organiserId)).append(" ");
            }

            return ResponseEntity.ok(generateApiResponse(null, results.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generateApiResponse(null, e.getMessage()));
        }
    }

    @DeleteMapping("/{organiserId}")
    public ResponseEntity<?> removeEventOrganiser(@PathVariable("organiserId") Integer organiserId) {
        try{
        return ResponseEntity.ok(eventOrganiserService.removeEventOrganiser(organiserId));}
        catch(DataIntegrityViolationException e){
            return ResponseEntity.status(409).body(String.format("The organiser with ID %d cannot be deleted because it has associated events.", organiserId));
        }
    }
}