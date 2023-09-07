package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.dto.event.EventDisplayDto;
import com.authenticket.authenticket.dto.event.EventUpdateDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
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
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
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
    public ResponseEntity<GeneralApiResponse<Object>> findAllEvent() {
        try {
            List<EventDisplayDto> eventList = eventService.findAllEvent();
            if (eventList.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(eventList, "No events found."));
            } else {
                return ResponseEntity.ok(generateApiResponse(eventList, "Events successfully returned."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, "Error getting the events."));
        }
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventById(@PathVariable("eventId") Integer eventId) {
        Optional<EventDisplayDto> eventDisplayDtoOptional = eventService.findEventById(eventId);
        return eventDisplayDtoOptional.map(eventDisplayDto -> ResponseEntity.ok(generateApiResponse(eventDisplayDto, String.format("Event %d successfully returned.", eventId)))).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("Event with id %d not found", eventId))));

    }

    @PostMapping
    public ResponseEntity<?> saveEvent(@RequestParam("file") MultipartFile file,
                                       @RequestParam("eventName") String eventName,
                                       @RequestParam(value = "eventDescription") String eventDescription,
                                       @RequestParam(value = "eventDate") LocalDateTime eventDate,
                                       @RequestParam(value = "eventLocation") String eventLocation,
                                       @RequestParam(value = "otherEventInfo") String otherEventInfo,
                                       @RequestParam(value = "ticketSaleDate") LocalDateTime ticketSaleDate,
                                       @RequestParam(value = "totalTickets") Integer totalTickets,
                                       @RequestParam(value = "organiserId") Integer organiserId) {
        String imageName;
        Event savedEvent;
        EventOrganiser eventOrganiser = eventOrganiserRepository.findById(organiserId).orElse(null);

        try {
            //save event first without image name to get the event id
            Event newEvent = new Event(null, eventName, eventDescription, eventDate, eventLocation, otherEventInfo, null, ticketSaleDate, totalTickets, null, null, eventOrganiser);
            savedEvent = eventService.saveEvent(newEvent);

            //generating the file name with the extension
            String fileExtension = getFileExtension(file.getContentType());
            imageName = savedEvent.getEventId() + fileExtension;
            //update event with image name and save to db again
            savedEvent.setEventImage(imageName);
            eventService.saveEvent(savedEvent);


        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, "Organiser does not exist OR ticket sale date is earlier than event created date."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }


        try {
            amazonS3Service.uploadFile(file, imageName, "event_images");
            // delete event from db if got error saving image
        } catch (AmazonS3Exception e) {
            eventService.deleteEvent(savedEvent.getEventId());
            String errorCode = e.getErrorCode();
            if ("AccessDenied".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(generateApiResponse(null, "Access Denied to Amazon."));
            } else if ("NoSuchBucket".equals(errorCode)) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "S3 bucket not found."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateApiResponse(null, "An error occurred during S3 interaction."));
            }
        } catch (Exception e) {
            eventService.deleteEvent(savedEvent.getEventId());
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
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
        try {
            EventUpdateDto eventUpdateDto = new EventUpdateDto(eventId, eventName, eventDescription, eventDate, eventLocation, otherEventInfo, ticketSaleDate);
            Event event = eventService.updateEvent(eventUpdateDto);

            if (event != null) {
                return ResponseEntity.ok(generateApiResponse(event, "Event updated successfully."));
            } else {
                return ResponseEntity.status(404).body(generateApiResponse(null, "Event not found, update not successful."));
            }

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }

    }

    @PutMapping("/{eventId}")
    public ResponseEntity<GeneralApiResponse<Object>> deleteEvent(@PathVariable("eventId") Integer eventId) {
        try {
            //if delete is successful
            eventService.deleteEvent(eventId);
            return ResponseEntity.ok(generateApiResponse(null, String.format("Event %d Deleted Successfully", eventId)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, e.getMessage()));
        } catch (AlreadyDeletedException e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }


    }

    //response not handled yet
    @DeleteMapping("/{eventId}")
    public String removeEvent(@PathVariable("eventId") Integer eventId) {
        return eventService.removeEvent(eventId);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approveEvent(@RequestParam(value = "eventId") Integer eventId, @RequestParam(value = "approvedBy") Integer approvedBy) {
        Event event = eventService.approveEvent(eventId, approvedBy);
        if (event == null) {
            return ResponseEntity.status(404).body(generateApiResponse(null, "Approve not successful, event does not exist"));
        } else {
            return ResponseEntity.ok(generateApiResponse(event, "Event approved successfully"));

        }
    }


}
