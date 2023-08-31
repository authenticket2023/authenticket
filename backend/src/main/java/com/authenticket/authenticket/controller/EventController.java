package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.event.EventUpdateDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventOrganiser;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/event")
public class EventController extends Utility {
    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping
    public List<EventDisplayDto> findAllEvent() {
        return eventService.findAllEvent();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<?> findEventById(@PathVariable("eventId") Integer eventId) {
        Optional<EventDisplayDto> eventDisplayDtoOptional = eventService.findEventById(eventId);
        if(eventDisplayDtoOptional.isPresent()){
            return ResponseEntity.ok(eventDisplayDtoOptional.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Event Not Found");

    }

    @PostMapping
    public ResponseEntity<?> saveEvent(@RequestParam("file") MultipartFile file,
                                       @RequestParam("eventName") String eventName,
                                       @RequestParam(value = "eventDescription") String eventDescription,
                                       @RequestParam(value = "eventDate") LocalDateTime eventDate,
                                       @RequestParam(value = "eventLocation") String eventLocation,
                                       @RequestParam(value = "otherEventInfo") String otherEventInfo,
                                       @RequestParam(value = "ticketSaleDate") LocalDateTime ticketSaleDate,
                                       @RequestParam(value = "organiserId") Integer organiserId) {
        String imageName;
        Event savedEvent;
        EventOrganiser eventOrganiser = eventOrganiserRepository.findById(organiserId).orElse(null);

        try {
            //save event first without image name to get the event id
            Event newEvent = new Event(null, eventName, eventDescription, eventDate, eventLocation, otherEventInfo, null, ticketSaleDate, null, eventOrganiser);
            savedEvent = eventService.saveEvent(newEvent);

            //generating the file name with the extension
            String fileExtension = getFileExtension(file.getContentType());
            imageName = savedEvent.getEventId() + fileExtension;
            //update event with image name and save to db again
            savedEvent.setEventImage(imageName);
            eventService.saveEvent(savedEvent);


        }catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("Organiser does not exist in the database");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body("error saving event");
        }


        try {
            amazonS3Service.uploadFile(file, imageName, "event_images");
            // delete event from db if got error saving image
        } catch (AmazonS3Exception e) {
            eventService.deleteEvent(savedEvent.getEventId());
            String errorCode = e.getErrorCode();
            if ("AccessDenied".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied to Amazon S3");
            } else if ("NoSuchBucket".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("S3 bucket not found");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during S3 interaction");
            }
        } catch (Exception e) {
            eventService.deleteEvent(savedEvent.getEventId());
            return ResponseEntity.badRequest().body("Error uploading event image");
        }

        return ResponseEntity.ok(savedEvent);
    }

    @PutMapping
    public ResponseEntity<?> updateEvent(@RequestParam("eventId") Integer eventId,
                                         @RequestParam("eventName") String eventName,
                                         @RequestParam(value = "eventDescription") String eventDescription,
                                         @RequestParam(value = "eventDate") LocalDateTime eventDate,
                                         @RequestParam(value = "eventLocation") String eventLocation,
                                         @RequestParam(value = "otherEventInfo") String otherEventInfo,
                                         @RequestParam(value = "ticketSaleDate") LocalDateTime ticketSaleDate) {
        EventUpdateDto eventUpdateDto = new EventUpdateDto(eventId, eventName, eventDescription, eventDate, eventLocation, otherEventInfo,  ticketSaleDate);
        Event event = eventService.updateEvent(eventUpdateDto);
        if(event!= null){
            return ResponseEntity.ok(event);
        } else{
            return ResponseEntity.status(404).body("event not found, update not successful");

        }



    }

    @PutMapping("/{eventId}")
    public String deleteEvent(@PathVariable("eventId") Integer eventId) {
        return eventService.deleteEvent(eventId);
    }

    @DeleteMapping("/{eventId}")
    public String removeEvent(@PathVariable("eventId") Integer eventId) {
        return eventService.removeEvent(eventId);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approveEvent(@RequestParam(value = "eventId") Integer eventId, @RequestParam(value = "approvedBy") Integer approvedBy) {
        Event event = eventService.approveEvent(eventId, approvedBy);
        if(event == null){
            return ResponseEntity.status(404).body("update unsuccessful, event does not exist");
        } else{
            return ResponseEntity.ok(event);

        }
    }


}
