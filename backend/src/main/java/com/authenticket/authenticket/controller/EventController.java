package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.venue.VenueDisplayDto;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.EventOrganiserRepository;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.ArtistServiceImpl;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import org.apache.tomcat.util.http.parser.HttpParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/event")
public class EventController extends Utility {
    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventOrganiserRepository eventOrganiserRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventDtoMapper eventDtoMapper;

    @Autowired
    private ArtistDtoMapper artistDtoMapper;


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
        OverallEventDto overallEventDto = eventService.findEventById(eventId);
        if (overallEventDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("Event with id %d not found", eventId)));
        }
        return ResponseEntity.ok(generateApiResponse(overallEventDto, String.format("Event %d successfully returned.", eventId)));

    }

    @GetMapping("/recently-added")
    public ResponseEntity<GeneralApiResponse<Object>> findRecentlyAddedEvents() {
        List<EventHomeDto> eventList = eventService.findRecentlyAddedEvents();
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No recently added events found")));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Recently added events successfully returned."));

    }

    @GetMapping("/featured")
    public ResponseEntity<GeneralApiResponse<Object>> findFeaturedEvents() {
//        List<EventHomeDto> eventList = eventService.findRecentlyAddedEvents();
//        if(eventList == null || eventList.isEmpty()){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No recently added events found")));
//        }
//        return ResponseEntity.ok(generateApiResponse(eventList, "Recently added events successfully returned."));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "featured events not done yet"));

    }

    @GetMapping("/bestseller")
    public ResponseEntity<GeneralApiResponse<Object>> findBestSellerEvents() {
        List<EventHomeDto> eventList = eventService.findBestSellerEvents();
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No bestseller events found")));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Bestseller events successfully returned."));

    }

    @GetMapping("/upcoming")
    public ResponseEntity<GeneralApiResponse<Object>> findUpcomingEvents() {
        List<EventHomeDto> eventList = eventService.findUpcomingEvents();
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No upcoming events found")));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Upcoming events successfully returned."));

    }


    @PostMapping
    public ResponseEntity<?> saveEvent(@RequestParam("file") MultipartFile file,
                                       @RequestParam("eventName") String eventName,
                                       @RequestParam("eventDescription") String eventDescription,
                                       @RequestParam("eventDate") LocalDateTime eventDate,
                                       @RequestParam("eventLocation") String eventLocation,
                                       @RequestParam("otherEventInfo") String otherEventInfo,
                                       @RequestParam("ticketSaleDate") LocalDateTime ticketSaleDate,
                                       @RequestParam("organiserId") Integer organiserId,
                                       @RequestParam("venueId") Integer venueId,
                                       @RequestParam("typeId") Integer typeId,
                                       //comma separated string
                                       @RequestParam("artistId") String artistIdString) {
        String imageName;
        Event savedEvent;
        //Getting the Respective Objects for Organiser, Venue and Type and checking if it exists
        EventOrganiser eventOrganiser = eventOrganiserRepository.findById(organiserId).orElse(null);
        Venue venue = venueRepository.findById(venueId).orElse(null);
        EventType eventType = eventTypeRepository.findById(typeId).orElse(null);
        if (eventOrganiser == null) {
            throw new NonExistentException("Event Organiser does not exist");
        } else if (venue == null) {
            throw new NonExistentException("Venue does not exist");
        } else if (eventType == null) {
            throw new NonExistentException("Event Type does not exist");
        }

        //totalTickets will be derived from event-ticketCat, set it after adding the ticket categories for event

        //save event first to get the event id
        try {
            //save event first without image name to get the event id
            Event newEvent = new Event(null, eventName, eventDescription, eventDate, eventLocation, otherEventInfo, null,
                    ticketSaleDate, 0, 0, null, "pending", null, eventOrganiser, venue, null, eventType, null);
            savedEvent = eventService.saveEvent(newEvent);

            //generating the file name with the extension
            String fileExtension = getFileExtension(file.getContentType());
            imageName = savedEvent.getEventId() + fileExtension;

            //update event with image name and save to db again, IMAGE HAS NOT BEEN UPLOADED HERE
            savedEvent.setEventImage(imageName);
            eventService.saveEvent(savedEvent);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, "DataIntegrityViolationException: Ticket sale date is earlier than event created date."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }

        //uploading event image to s3 server
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

        Integer eventId = savedEvent.getEventId();
        //adding artist to event
        List<Integer> artistIdList = Arrays.stream(artistIdString.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        for (Integer artistId : artistIdList) {
            eventService.addArtistToEvent(artistId, eventId);
        }


        //setting all the fields for event
        OverallEventDto overallEventDto = eventDtoMapper.applyOverallEventDto(savedEvent);

        return ResponseEntity.ok(generateApiResponse(overallEventDto, "Event created successfully."));
    }

    @PutMapping
    public ResponseEntity<?> updateEvent(@RequestParam("eventId") Integer eventId,
                                         @RequestParam("eventName") String eventName,
                                         @RequestParam(value = "eventDescription") String eventDescription,
                                         @RequestParam(value = "eventDate") LocalDateTime eventDate,
                                         @RequestParam(value = "eventLocation") String eventLocation,
                                         @RequestParam(value = "otherEventInfo") String otherEventInfo,
                                         @RequestParam(value = "ticketSaleDate") LocalDateTime ticketSaleDate) {

        EventUpdateDto eventUpdateDto = new EventUpdateDto(eventId, eventName, eventDescription, eventDate, eventLocation, otherEventInfo, ticketSaleDate);
        Event event = eventService.updateEvent(eventUpdateDto);

        if (event != null) {
            return ResponseEntity.ok(generateApiResponse(event, "Event updated successfully."));
        } else {
            return ResponseEntity.status(404).body(generateApiResponse(null, "Event not found, update not successful."));
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

    @PutMapping("/addArtistToEvent")
    public ResponseEntity<GeneralApiResponse> addArtistToEvent(
            @RequestParam("artistId") Integer artistId,
            @RequestParam("eventId") Integer eventId) {
        try {
            EventDisplayDto artist = eventService.addArtistToEvent(artistId, eventId);
            if (artist != null) {
                return ResponseEntity.ok(generateApiResponse(artist, "Artist successfully assigned to event"));
            } else {
                return ResponseEntity.status(401).body(generateApiResponse(null, "Artist failed to assigned to event"));
            }
        } catch (DataIntegrityViolationException | StackOverflowError e) {
            return ResponseEntity.status(400).body(generateApiResponse(null, "Artist already linked to stated event,or Event and Artist does not exists"));
        }
    }

    @GetMapping("/getAssignedEvents")
    public ResponseEntity<GeneralApiResponse> getAssignEvents() {
        try {
            List<Object[]> assignedObjects = eventRepository.getAssignedEvent();
            return ResponseEntity.ok(generateApiResponse(eventDtoMapper.mapAssignedEvent(assignedObjects), "Assigned events returned"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.ok(generateApiResponse(null, "Artist already linked to stated event,or Event and Artist does not exists"));
        }
    }

    //getting artist list for one specific event
    @GetMapping("/getArtistsByEvent")
    public ResponseEntity<GeneralApiResponse> getArtistsForEvent(@RequestParam("eventId") Integer eventId) {
        try {
            Set<ArtistDisplayDto> artistList = eventService.findArtistForEvent(eventId);
            if (artistList.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(artistList, String.format("Artist List for Event %d is empty", eventId)));

            }

            return ResponseEntity.ok(generateApiResponse(artistList, String.format("Artist List for Event %d returned", eventId)));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.ok(generateApiResponse(null, "Artist already linked to stated event,or Event and Artist does not exists"));
        }
    }

    @PutMapping("/addTicketCategory")
    public ResponseEntity<GeneralApiResponse> addTicketCategory(
            @RequestParam("catId") Integer catId,
            @RequestParam("eventId") Integer eventId,
            @RequestParam("price") Double price,
            @RequestParam("availableTickets") Integer availableTickets,
            @RequestParam("totalTicketsPerCat") Integer totalTicketsPerCat) {
        EventDisplayDto eventDisplayDto = eventService.addTicketCategory(catId, eventId, price, availableTickets, totalTicketsPerCat);
        if (eventDisplayDto != null) {
            return ResponseEntity.ok(generateApiResponse(eventDisplayDto, "Ticket Category successfully added to event"));
        } else {
            return ResponseEntity.status(401).body(generateApiResponse(null, "Ticket Category failed to be added"));
        }
    }

    @GetMapping("/review-status/{status}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventsByReviewStatus(@PathVariable("status") String status) {
        List<EventDisplayDto> eventList = eventService.findEventsByReviewStatus(status);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No %s events found.", status)));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, String.format("%s events successfully returned.", status)));

    }

}
