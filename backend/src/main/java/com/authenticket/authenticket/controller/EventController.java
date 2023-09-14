package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.JSONFormat;
import com.authenticket.authenticket.TicketCategoryJSON;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api")
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
    private AdminRepository adminRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private EventDtoMapper eventDtoMapper;


    @GetMapping("/public/event/test")
    public String test() {
        return "test successful";
    }


    @GetMapping("/public/event")
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

    @GetMapping("/public/event/{eventId}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventById(@PathVariable("eventId") Integer eventId) {
        OverallEventDto overallEventDto = eventService.findEventById(eventId);
        if (overallEventDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("Event with id %d not found", eventId)));
        }
        return ResponseEntity.ok(generateApiResponse(overallEventDto, String.format("Event %d successfully returned.", eventId)));

    }

    @GetMapping("/public/event/recently-added")
    public ResponseEntity<GeneralApiResponse<Object>> findRecentlyAddedEvents() {
        List<EventHomeDto> eventList = eventService.findRecentlyAddedEvents();
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No recently added events found")));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Recently added events successfully returned."));

    }

    @GetMapping("/public/event/featured")
    public ResponseEntity<GeneralApiResponse<Object>> findFeaturedEvents() {
        List<FeaturedEventDto> eventList = eventService.findFeaturedEvents();
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No featured events found")));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Featured events successfully returned."));

    }

    @GetMapping("/public/event/bestseller")
    public ResponseEntity<GeneralApiResponse<Object>> findBestSellerEvents() {
        List<EventHomeDto> eventList = eventService.findBestSellerEvents();
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No bestseller events found")));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Bestseller events successfully returned."));

    }

    @GetMapping("/public/event/upcoming")
    public ResponseEntity<GeneralApiResponse<Object>> findUpcomingEvents() {
        List<EventHomeDto> eventList = eventService.findUpcomingEvents();
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No upcoming events found")));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Upcoming events successfully returned."));

    }

    @PostMapping("/event")
    public ResponseEntity<?> saveEvent(@RequestParam("file") MultipartFile file,
                                       @RequestParam("eventName") String eventName,
                                       @RequestParam("eventDescription") String eventDescription,
                                       @RequestParam("eventDate") LocalDateTime eventDate,
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
            Event newEvent = new Event(null, eventName, eventDescription, eventDate, otherEventInfo, null,
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


    @PutMapping("/event")
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

    @PutMapping("/event/{eventId}")
    public ResponseEntity<GeneralApiResponse<Object>> deleteEvent(@RequestParam("eventId") String eventIdString) {
        try {
            List<Integer> eventIdList = Arrays.stream(eventIdString.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

            //check if all events exist first
            for(Integer eventId: eventIdList){
                if(eventRepository.findById(eventId).isEmpty()){
                    throw new NonExistentException(String.format("Event %d does not exist, deletion halted", eventId));
                }
            }

            StringBuilder results = new StringBuilder();

            for(Integer eventId: eventIdList){
                results.append(eventService.deleteEvent(eventId)).append(" ");
            }

            return ResponseEntity.ok(generateApiResponse(null, results.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generateApiResponse(null, e.getMessage()));
        }
    }

    //response not handled yet
    @DeleteMapping("/event/{eventId}")
    public String removeEvent(@PathVariable("eventId") Integer eventId) {
        return eventService.removeEvent(eventId);
    }

    @PutMapping("/event/approve")
    public ResponseEntity<?> approveEvent(@RequestParam(value = "eventId") Integer eventId, @RequestParam(value = "approvedBy") Integer approvedBy) {
        Event event = eventService.approveEvent(eventId, approvedBy);
        if (event == null) {
            return ResponseEntity.status(404).body(generateApiResponse(null, "Approve not successful, event does not exist"));
        } else {
            return ResponseEntity.ok(generateApiResponse(event, "Event approved successfully"));

        }
    }

    @PutMapping("/event/addArtistToEvent")
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


    @PostMapping("/public/event/featured")
    public ResponseEntity<GeneralApiResponse<Object>> saveFeaturedEvents(@RequestParam("eventId") Integer eventId,
                                                                         @RequestParam("startDate") LocalDateTime startDate,
                                                                         @RequestParam("endDate") LocalDateTime endDate,
                                                                         @RequestParam("addedBy") Integer adminId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        Admin admin = adminRepository.findById(adminId).orElse(null);

        if (event == null) {
            throw new NonExistentException(String.format("No event of id %d found", eventId));
        } else if (admin == null) {
            throw new NonExistentException("Admin not found");
        } else if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("end date is earlier than start date");
        }
        try {
            FeaturedEvent featuredEvent = new FeaturedEvent(null, event, startDate, endDate, admin);
            return ResponseEntity.ok(generateApiResponse(eventService.saveFeaturedEvent(featuredEvent), "Featured Event Successfully Saved"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, "Featured event with event id could already exists"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }

    }

    //getting artist list for one specific event
    @GetMapping("/event/getArtistsByEvent")
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

    @PutMapping("/event/addTicketCategory")
    public ResponseEntity<GeneralApiResponse> addTicketCategory(@RequestBody JSONFormat jsonFormat) {
        Integer eventId = jsonFormat.getEventId();
        TicketCategoryJSON[] ticketCategoryJSONS = jsonFormat.getData();
        for (TicketCategoryJSON ticketCategoryJSON : ticketCategoryJSONS) {
            eventService.addTicketCategory(ticketCategoryJSON.getCatId(), eventId, ticketCategoryJSON.getPrice(), ticketCategoryJSON.getAvailableTickets(), ticketCategoryJSON.getTotalTicketsPerCat());
        }
        return ResponseEntity.ok(generateApiResponse(eventService.findEventById(eventId), "Ticket Category successfully added to event"));
    }

    @PutMapping("/event/updateTicketCategory")
    public ResponseEntity<GeneralApiResponse> updateTicketCategory(@RequestBody JSONFormat jsonFormat) {
        Integer eventId = jsonFormat.getEventId();
        TicketCategoryJSON[] ticketCategoryJSONS = jsonFormat.getData();
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            //resetting value before update
            event.setTotalTickets(0);
            event.setTotalTicketsSold(0);
            eventRepository.save(event);
        } else {
            throw new NonExistentException("Event does not exist");
        }
        for (TicketCategoryJSON ticketCategoryJSON : ticketCategoryJSONS) {
            eventService.updateTicketCategory(ticketCategoryJSON.getCatId(), eventId, ticketCategoryJSON.getPrice(), ticketCategoryJSON.getAvailableTickets(), ticketCategoryJSON.getTotalTicketsPerCat());
        }
        return ResponseEntity.ok(generateApiResponse(eventService.findEventById(eventId), "Ticket Category successfully updated for event"));
    }

//    @PutMapping("/addTicketCategory")
//    public ResponseEntity<GeneralApiResponse> addTicketCategory(
//            @RequestParam("catId") Integer catId,
//            @RequestParam("eventId") Integer eventId,
//            @RequestParam("price") Double price,
//            @RequestParam("availableTickets") Integer availableTickets,
//            @RequestParam("totalTicketsPerCat") Integer totalTicketsPerCat) {
//        EventDisplayDto eventDisplayDto = eventService.addTicketCategory(catId, eventId, price, availableTickets, totalTicketsPerCat);
//        if (eventDisplayDto != null) {
//            return ResponseEntity.ok(generateApiResponse(eventDisplayDto, "Ticket Category successfully added to event"));
//        } else {
//            return ResponseEntity.status(401).body(generateApiResponse(null, "Ticket Category failed to be added"));
//        }
//    }

//    @PutMapping("/removeTicketCategory")
//    public ResponseEntity<GeneralApiResponse> removeTicketCategory(
//            @RequestParam("catId") Integer catId,
//            @RequestParam("eventId") Integer eventId) {
//        EventDisplayDto eventDisplayDto = eventService.removeTicketCategory(catId, eventId);
//        return ResponseEntity.ok(generateApiResponse(eventDisplayDto, "Ticket Category successfully removed from event"));
//    }

    @GetMapping("/event/{eventId}/getTicketCategory")
    public ResponseEntity<GeneralApiResponse> getTicketCategory(
            @RequestParam("eventId") Integer eventId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new NonExistentException("Event does not exist");
        }
        Set<EventTicketCategory> eventTicketCategorySet = optionalEvent.get().getEventTicketCategorySet();
        return ResponseEntity.ok(generateApiResponse(eventTicketCategorySet, "Returning event ticket category set"));
    }

    @GetMapping("/event/review-status/{status}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventsByReviewStatus(@PathVariable("status") String status) {
        List<EventDisplayDto> eventList = eventService.findEventsByReviewStatus(status);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, String.format("No %s events found.", status)));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, String.format("%s events successfully returned.", status)));

    }
}
