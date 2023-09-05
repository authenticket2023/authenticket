package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserUpdateDto;
import com.authenticket.authenticket.model.Event;
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

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/event-organiser")
public class EventOrganiserController extends Utility {
    @Autowired
    private EventOrganiserServiceImpl eventOrganiserService;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }



    @GetMapping
    public List<EventOrganiserDisplayDto> findAllEventOrganiser() {
        return eventOrganiserService.findAllEventOrganisers();
    }

    @GetMapping("/{organiserId}")
    public ResponseEntity<?> findEventOrganiserById(@PathVariable("organiserId") Integer organiserId) {
        Optional<EventOrganiserDisplayDto> organiserDisplayDtoOptional = eventOrganiserService.findOrganiserById(organiserId);
        if(organiserDisplayDtoOptional.isPresent()){
            return ResponseEntity.ok(organiserDisplayDtoOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("The organiser with ID %d does not exist", organiserId));

    }

    @GetMapping("/events/{organiserId}")
    public ResponseEntity<?> findAllEventsByOrganiser(@PathVariable("organiserId") Integer organiserId) {
        List<Event> events = eventOrganiserService.findAllEventsByOrganiser(organiserId);
        if(!events.isEmpty()){
            return ResponseEntity.ok(events);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format("The organiser with ID %d does not have associated events or the organiser does not exist", organiserId));

    }

    @PostMapping
    public ResponseEntity<?> saveEventOrganiser(@RequestParam("name") String name,
                                       @RequestParam("email") String email,
                                       @RequestParam("description") String description) {
        EventOrganiser savedEventOrganiser;
        try {
            //save eventOrganiser first without image name to get the eventOrganiser id
            EventOrganiser newEventOrganiser = new EventOrganiser(null, name,  passwordEncoder.encode(generateRandomPassword()), email, description,null,null,null,null);
            savedEventOrganiser = eventOrganiserService.saveEventOrganiser(newEventOrganiser);


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving event organiser" + e.getClass());
        }


        return ResponseEntity.ok(savedEventOrganiser);
    }

    @PutMapping
    public ResponseEntity<?> updateEventOrganiser(@RequestParam("organiserId") Integer organiserId,
                                                  @RequestParam("name") String name,
                                                  @RequestParam(value = "description") String description,
                                                  @RequestParam(value = "password") String password) {
        EventOrganiserUpdateDto eventOrganiserUpdateDto = new EventOrganiserUpdateDto(organiserId, name,description,password);
        EventOrganiser eventOrganiser = eventOrganiserService.updateEventOrganiser(eventOrganiserUpdateDto);
        if(eventOrganiser!= null){
            return ResponseEntity.ok(eventOrganiser);
        }
        return ResponseEntity.badRequest().body("update not successful");


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
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event Organiser does not exist");
            }

        }catch (AmazonS3Exception e) {
            String errorCode = e.getErrorCode();
            if ("AccessDenied".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied to Amazon S3");
            } else if ("NoSuchBucket".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("S3 bucket not found");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during S3 interaction");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading event organiser image");
        }


        return ResponseEntity.ok(eventOrganiser);
    }



    @PutMapping("/{organiserId}")
    public String deleteEventOrganiser(@PathVariable("organiserId") Integer organiserId) {
        return eventOrganiserService.deleteEventOrganiser(organiserId);
    }

    @DeleteMapping("/{organiserId}")
    public ResponseEntity<?> removeEventOrganiser(@PathVariable("organiserId") Integer organiserId) {
        try{
        return ResponseEntity.ok(eventOrganiserService.removeEventOrganiser(organiserId));}
        catch(DataIntegrityViolationException e){
            return ResponseEntity.status(409).body(String.format("The organiser with ID %d cannot be deleted because it has associated events.", organiserId));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getClass());
        }
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approveEventOrganiser(@RequestParam(value = "organiserId") Integer organiserId, @RequestParam(value = "approvedBy") Integer approvedBy) {
        return ResponseEntity.ok(eventOrganiserService.approveOrganiser(organiserId, approvedBy));
    }


}