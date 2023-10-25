package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.exception.InvalidRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.*;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/** This is event controller class and base path for the controller's endpoints is "/api/v2".*/
@RestController
@CrossOrigin(origins = {
        "${authenticket.frontend-production-url}",
        "${authenticket.frontend-dev-url}",
        "${authenticket.loadbalancer-url}"
}, methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT }, allowedHeaders = { "Authorization",
        "Cache-Control", "Content-Type" }, allowCredentials = "true")
@RequestMapping("/api/v2")
public class EventController extends Utility {
    private final EventServiceImpl eventService;

    private final PresaleService presaleService;

    private final AmazonS3Service amazonS3Service;

    private final EventRepository eventRepository;

    private final UserRepository userRepository;

    private final ArtistRepository artistRepository;

    private final VenueRepository venueRepository;

    private final EventTypeRepository eventTypeRepository;

    private final EventDtoMapper eventDtoMapper;

    private final TaskScheduler taskScheduler;

    private final TicketService ticketService;

    private final QueueService queueService;
  
    private final JwtService jwtService;

    private static final int PRESALE_HOURS = 24;

    @Autowired
    public EventController(EventServiceImpl eventService,
            AmazonS3Service amazonS3Service,
            EventRepository eventRepository,
            ArtistRepository artistRepository,
            VenueRepository venueRepository,
            EventTypeRepository eventTypeRepository,
            EventDtoMapper eventDtoMapper,
            PresaleService presaleService,
            UserRepository userRepository,
            TaskScheduler taskScheduler,
                           TicketService ticketService,
                           QueueService queueService,
                           JwtService jwtService) {
        this.eventService = eventService;
        this.amazonS3Service = amazonS3Service;
        this.eventRepository = eventRepository;
        this.artistRepository = artistRepository;
        this.venueRepository = venueRepository;
        this.eventTypeRepository = eventTypeRepository;
        this.eventDtoMapper = eventDtoMapper;
        this.presaleService = presaleService;
        this.userRepository = userRepository;
        this.taskScheduler = taskScheduler;
        this.ticketService = ticketService;
        this.queueService = queueService;
        this.jwtService = jwtService;
    }

    @GetMapping("/public/event/test")
    public String test() {
        return "test successful";
    }

    /**
     * Retrieves a list of events based on the provided pagination that is
     * accessible without the need of token
     * parameters.
     *
     * @param pageable The pagination information for retrieving the events.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         public events
     *         or an appropriate message if no events are found or if an error
     *         occurs.
     */
    @GetMapping("/public/event")
    public ResponseEntity<GeneralApiResponse<Object>> findAllPublicEvent(Pageable pageable) {
        try {
            List<EventHomeDto> eventList = eventService.findAllPublicEvent(pageable);
            if (eventList.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(eventList, "No events found."));
            } else {
                return ResponseEntity.ok(generateApiResponse(eventList, "Events successfully returned."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, "Error getting the events."));
        }
    }

    /**
     * Retrieves a specific event by its unique identifier.
     *
     * @param eventId The unique identifier of the event to retrieve.
     * @return A ResponseEntity containing a GeneralApiResponse with the event
     *         details
     *         or a "Not Found" response if the event is not found.
     */
    @GetMapping("/public/event/{eventId}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventById(@PathVariable("eventId") Integer eventId) {
        OverallEventDto overallEventDto = eventService.findEventById(eventId);
        if (overallEventDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(generateApiResponse(null, String.format("Event with id %d not found", eventId)));
        }
        return ResponseEntity
                .ok(generateApiResponse(overallEventDto, String.format("Event %d successfully returned.", eventId)));

    }

    /**
     * Retrieves a list of featured events based on the provided pagination
     * parameters.
     *
     * @param pageable The pagination information for retrieving the featured
     *                 events.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         featured events
     *         or a message indicating no featured events are found.
     */
    @GetMapping("/public/event/featured")
    public ResponseEntity<GeneralApiResponse<Object>> findFeaturedEvents(Pageable pageable) {
        List<FeaturedEventDto> eventList = eventService.findFeaturedEvents(pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No featured events found"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Featured events successfully returned."));

    }

    /**
     * Retrieves a list of recently added events based on the provided pagination
     * parameters.
     *
     * @param pageable The pagination information for retrieving the recently added
     *                 events.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         recently added events
     *         or a message indicating no recently added events are found.
     */
    @GetMapping("/public/event/recently-added")
    public ResponseEntity<GeneralApiResponse<Object>> findRecentlyAddedEvents(Pageable pageable) {
        List<EventHomeDto> eventList = eventService.findRecentlyAddedEvents(pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No recently added events found"));

        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Recently added events successfully returned."));

    }

    /**
     * Retrieves a list of bestseller events.
     *
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         bestseller events
     *         or a message indicating no bestseller events are found.
     */
    @GetMapping("/public/event/bestseller")
    public ResponseEntity<GeneralApiResponse<Object>> findBestSellerEvents() {
        List<EventHomeDto> eventList = eventService.findBestSellerEvents();
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No bestseller events found"));

        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Bestseller events successfully returned."));

    }

    /**
     * Retrieves a list of upcoming events based on the provided pagination
     * parameters.
     *
     * @param pageable The pagination information for retrieving the upcoming
     *                 events.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         upcoming events
     *         or a message indicating no upcoming events are found.
     */
    @GetMapping("/public/event/upcoming")
    public ResponseEntity<GeneralApiResponse<Object>> findUpcomingEvents(Pageable pageable) {
        List<EventHomeDto> eventList = eventService.findUpcomingEventsByTicketSalesDate(pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No upcoming events found"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Upcoming events successfully returned."));

    }

    /**
     * Retrieves a list of current events based on the provided pagination
     * parameters.
     *
     * @param pageable The pagination information for retrieving the current events.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         current events
     *         or a message indicating no current events are found.
     */
    @GetMapping("/public/event/current")
    public ResponseEntity<GeneralApiResponse<Object>> findCurrentEventsByEventDate(Pageable pageable) {
        List<EventHomeDto> eventList = eventService.findCurrentEventsByEventDate(pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No current events found"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Current events successfully returned."));

    }

    /**
     * Retrieves a list of past events based on the provided pagination parameters.
     *
     * @param pageable The pagination information for retrieving the past events.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of past
     *         events
     *         or a message indicating no past events are found.
     */
    @GetMapping("/public/event/past")
    public ResponseEntity<GeneralApiResponse<Object>> findPastEventsByEventDate(Pageable pageable) {
        List<EventHomeDto> eventList = eventService.findPastEventsByEventDate(pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No past events found"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Past events successfully returned."));

    }

    /**
     * Retrieves a list of events associated with a specific venue based on the
     * provided pagination parameters.
     *
     * @param pageable The pagination information for retrieving events for the
     *                 venue.
     * @param venueId  The unique identifier of the venue for which events are being
     *                 retrieved.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         events for the venue
     *         or a message indicating no events are found for the venue.
     */
    @GetMapping("/public/event/venue/{venueId}")
    public ResponseEntity<GeneralApiResponse<Object>> findEventsByVenue(Pageable pageable,
            @PathVariable("venueId") Integer venueId) {
        List<EventHomeDto> eventList = eventService.findEventsByVenue(venueId, pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No events found for venue"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Events for venue successfully returned."));
    }

    /**
     * Retrieves a list of past events associated with a specific venue based on the
     * provided pagination parameters.
     *
     * @param pageable The pagination information for retrieving past events for the
     *                 venue.
     * @param venueId  The unique identifier of the venue for which past events are
     *                 being retrieved.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of past
     *         events for the venue
     *         or a message indicating no past events are found for the venue.
     */
    @GetMapping("/public/event/venue/past/{venueId}")
    public ResponseEntity<GeneralApiResponse<Object>> findPastEventsByVenue(Pageable pageable,
            @PathVariable("venueId") Integer venueId) {
        List<EventHomeDto> eventList = eventService.findPastEventsByVenue(venueId, pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No events found for venue"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Past events for venue successfully returned."));

    }

    /**
     * Retrieves a list of upcoming events associated with a specific venue based on
     * the provided pagination parameters.
     *
     * @param pageable The pagination information for retrieving upcoming events for
     *                 the venue.
     * @param venueId  The unique identifier of the venue for which upcoming events
     *                 are being retrieved.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         upcoming events for the venue
     *         or a message indicating no upcoming events are found for the venue.
     */
    @GetMapping("/public/event/venue/upcoming/{venueId}")
    public ResponseEntity<GeneralApiResponse<Object>> findUpcomingEventsByVenue(Pageable pageable,
            @PathVariable("venueId") Integer venueId) {
        List<EventHomeDto> eventList = eventService.findEventsByVenue(venueId, pageable);
        if (eventList == null || eventList.isEmpty()) {
            return ResponseEntity.ok(generateApiResponse(null, "No events found for venue"));
        }
        return ResponseEntity.ok(generateApiResponse(eventList, "Upcoming events for venue successfully returned."));

    }

    /**
     * Retrieves a list of all events for admin
     *
     * @return A ResponseEntity containing a GeneralApiResponse with a list of all
     *         events
     *         or an error message if an exception occurs during retrieval.
     */
    @GetMapping("/event")
    public ResponseEntity<GeneralApiResponse<Object>> findAllEvent() {
        try {
            List<EventAdminDisplayDto> eventList = eventService.findAllEvent();
            if (eventList.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(eventList, "No events found."));
            } else {
                return ResponseEntity.ok(generateApiResponse(eventList, "Events successfully returned."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }
    }

    /**
     * Retrieves a list of enhanced events for a specific event organizer.
     *
     * @param request The HTTPServletRequest containing information for identifying
     *                the event organizer.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of
     *         enhanced events for the organizer
     *         or an error message if an exception occurs during retrieval.
     */
    @GetMapping("/event/enhanced")
    public ResponseEntity<GeneralApiResponse<Object>> findAllEnhancedEventForOrg(@NonNull HttpServletRequest request) {
        EventOrganiser organiser = retrieveOrganiserFromRequest(request);
        Integer organiserId = organiser.getOrganiserId();

        try {
            List<EventHomeDto> eventList = eventService.findEventsByOrganiserAndEnhancedStatus(organiserId,
                    Boolean.TRUE);
            if (eventList.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(eventList, "No events found."));
            } else {
                return ResponseEntity.ok(generateApiResponse(eventList, "Enhanced events successfully returned."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }
    }

    /**
     * Retrieves a list of not enhanced events for a specific event organizer.
     *
     * @param request The HTTPServletRequest containing information for identifying
     *                the event organizer.
     * @return A ResponseEntity containing a GeneralApiResponse with a list of not
     *         enhanced events for the organizer
     *         or an error message if an exception occurs during retrieval.
     */
    @GetMapping("/event/not-enhanced")
    public ResponseEntity<GeneralApiResponse<Object>> findAllNotEnhancedEventForOrg(
            @NonNull HttpServletRequest request) {
        EventOrganiser organiser = retrieveOrganiserFromRequest(request);
        Integer organiserId = organiser.getOrganiserId();

        try {
            List<EventHomeDto> eventList = eventService.findEventsByOrganiserAndEnhancedStatus(organiserId,
                    Boolean.FALSE);
            if (eventList.isEmpty()) {
                return ResponseEntity.ok(generateApiResponse(eventList, "No events found."));
            } else {
                return ResponseEntity.ok(generateApiResponse(eventList, "Not enhanced events successfully returned."));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }
    }

    /**
     * Creates and saves a new event with the provided information, including event
     * details, ticket categories, and image upload.
     *
     * @param file               The image file representing the event.
     * @param eventName          The name of the event.
     * @param eventDescription   The description of the event.
     * @param eventDate          The date and time of the event.
     * @param otherEventInfo     Additional information about the event.
     * @param ticketSaleDate     The date and time when ticket sales begin.
     * @param venueId            The unique identifier of the venue for the event.
     * @param typeId             The unique identifier of the event type.
     * @param artistIdString     A comma-separated string of artist IDs associated
     *                           with the event.
     * @param ticketPricesString A comma-separated string of ticket prices for
     *                           different categories.
     * @param hasPresale         Indicates whether the event has a presale.
     * @param isEnhanced         Indicates whether the event is enhanced.
     * @param request            The HTTPServletRequest for authorization and
     *                           validation.
     * @return A ResponseEntity containing a GeneralApiResponse with the details of
     *         the created event
     *         or an error message if an exception occurs during the creation
     *         process.
     */
    @PostMapping("/event")
    public ResponseEntity<GeneralApiResponse<Object>> saveEvent(
            @RequestParam("file") MultipartFile file,
            @RequestParam("eventName") String eventName,
            @RequestParam("eventDescription") String eventDescription,
            @RequestParam("eventDate") LocalDateTime eventDate,
            @RequestParam("otherEventInfo") String otherEventInfo,
            @RequestParam("ticketSaleDate") LocalDateTime ticketSaleDate,
            // @RequestParam("organiserId") Integer organiserId,
            @RequestParam("venueId") Integer venueId,
            @RequestParam("typeId") Integer typeId,
            // comma separated string
            @RequestParam("artistId") String artistIdString,
            // comma separated string
            @RequestParam("ticketPrices") String ticketPricesString,
            @RequestParam("hasPresale") Boolean hasPresale,
            @RequestParam("isEnhanced") Boolean isEnhanced,
            @NonNull HttpServletRequest request) {
        String imageName;
        Event savedEvent;
        // Getting the Respective Objects for Organiser, Venue and Type and checking if
        // it exists
        EventOrganiser eventOrganiser = retrieveOrganiserFromRequest(request);
        Venue venue = venueRepository.findById(venueId).orElse(null);
        EventType eventType = eventTypeRepository.findById(typeId).orElse(null);

        // artistIdString to artistId List
        List<Integer> artistIdList = Arrays.stream(artistIdString.split(","))
                .map(Integer::parseInt)
                .toList();
        // check that all artist is valid first
        for (Integer artistId : artistIdList) {
            if (artistRepository.findById(artistId).isEmpty()) {
                throw new NonExistentException(
                        String.format("Artist with id %d does not exist, please try again", artistId));
            }
        }

        if (eventOrganiser == null) {
            throw new NonExistentException("Event Organiser does not exist");
        } else if (venue == null) {
            throw new NonExistentException("Venue does not exist");
        } else if (eventType == null) {
            throw new NonExistentException("Event Type does not exist");
        }

        // save event first to get the event id
        try {
            // save event first without image name to get the event id
            Event newEvent = new Event(null, eventName, eventDescription, eventDate, otherEventInfo, null,
                    ticketSaleDate, null, "pending", null, isEnhanced, hasPresale, false, eventOrganiser, venue, null,
                    eventType, new HashSet<TicketPricing>(), new HashSet<Order>());
            savedEvent = eventService.saveEvent(newEvent);

            // generating the file name with the extension
            String fileExtension = getFileExtension(file.getContentType());
            imageName = savedEvent.getEventId() + fileExtension;

            // update event with image name and save to db again, IMAGE HAS NOT BEEN
            // UPLOADED HERE
            savedEvent.setEventImage(imageName);
            eventService.saveEvent(savedEvent);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null,
                    "DataIntegrityViolationException: Ticket sale date is earlier than event created date."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }

        // uploading event image to s3 server
        try {
            amazonS3Service.uploadFile(file, imageName, "event_images");
            // delete event from db if got error saving image
        } catch (AmazonS3Exception e) {
            eventService.deleteEvent(savedEvent.getEventId());

            String errorCode = e.getErrorCode();
            if ("AccessDenied".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(generateApiResponse(null, "Access Denied to Amazon."));
            } else if ("NoSuchBucket".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(generateApiResponse(null, "S3 bucket not found."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(generateApiResponse(null, "An error occurred during S3 interaction."));
            }
        } catch (Exception e) {
            eventService.deleteEvent(savedEvent.getEventId());
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }

        Integer eventId = savedEvent.getEventId();

        for (Integer artistId : artistIdList) {
            eventService.addArtistToEvent(artistId, eventId);
        }

        // adding ticket pricing for each cat
        List<Double> ticketPrices = Arrays.stream(ticketPricesString.split(","))
                .map(Double::parseDouble)
                .toList();

        if (ticketPrices.size() != 5) {
            throw new IllegalArgumentException("Ticket Prices should have 5 values");
        }

        eventService.addTicketCategory(1, eventId, ticketPrices.get(0));
        eventService.addTicketCategory(2, eventId, ticketPrices.get(1));
        eventService.addTicketCategory(3, eventId, ticketPrices.get(2));
        eventService.addTicketCategory(4, eventId, ticketPrices.get(3));
        eventService.addTicketCategory(5, eventId, ticketPrices.get(4));

        // setting all the fields for event
        OverallEventDto overallEventDto = eventDtoMapper.applyOverallEventDto(savedEvent);

        // Set presale to run 1 day before ticket sale date
        if (hasPresale) {
            LocalDateTime scheduledCheckTime = ticketSaleDate.minusHours(PRESALE_HOURS);
            taskScheduler.schedule(() -> presaleService.selectPresaleUsersForEvent(savedEvent),
                    Date.from(scheduledCheckTime.atZone(ZoneId.systemDefault()).toInstant()));
        }

        return ResponseEntity.status(201).body(generateApiResponse(overallEventDto, "Event created successfully."));
    }

    /**
     * Updates an existing event's details, including event information, ticket
     * categories, and image, without the need for review. This action is targeted
     * towards event organizers.
     *
     * @param eventImageFile     The new image file to replace the existing event
     *                           image (optional).
     * @param eventId            The unique identifier of the event to be updated.
     * @param eventName          The new name of the event (optional).
     * @param eventDescription   The new description of the event (optional).
     * @param eventDate          The new date and time of the event (optional).
     * @param eventLocation      The new location of the event (optional).
     * @param otherEventInfo     Additional information about the event (optional).
     * @param ticketSaleDate     The new date and time when ticket sales begin
     *                           (optional).
     * @param venueId            The new venue for the event (optional).
     * @param typeId             The new event type (optional).
     * @param ticketPricesString The updated ticket prices as a comma-separated
     *                           string (optional).
     * @param request            The HTTPServletRequest for authorization and
     *                           validation.
     * @return A ResponseEntity containing a GeneralApiResponse with the updated
     *         event details
     *         or an error message if an exception occurs during the update process.
     */
    @PutMapping("/event")
    public ResponseEntity<GeneralApiResponse<Object>> updateEvent(
            @RequestParam(value = "file", required = false) MultipartFile eventImageFile,
            @RequestParam(value = "eventId") Integer eventId,
            @RequestParam(value = "eventName", required = false) String eventName,
            @RequestParam(value = "eventDescription", required = false) String eventDescription,
            @RequestParam(value = "eventDate", required = false) LocalDateTime eventDate,
            @RequestParam(value = "eventLocation", required = false) String eventLocation,
            @RequestParam(value = "otherEventInfo", required = false) String otherEventInfo,
            @RequestParam(value = "ticketSaleDate", required = false) LocalDateTime ticketSaleDate,
            @RequestParam(value = "venueId", required = false) Integer venueId,
            @RequestParam(value = "typeId", required = false) Integer typeId,
            @RequestParam(value = "ticketPrices", required = false) String ticketPricesString,
            @NonNull HttpServletRequest request) {
        EventOrganiser organiser = retrieveOrganiserFromRequest(request);
        if (!eventRepository.existsEventByEventIdAndOrganiser(eventId, organiser)) {
            throw new IllegalArgumentException(
                    "Organiser is not allowed to update events created by other organisers.");
        }

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

        if (ticketPricesString != null) {
            List<Double> ticketPrices = Arrays.stream(ticketPricesString.split(","))
                    .map(Double::parseDouble)
                    .toList();

            if (ticketPrices.size() != 5) {
                throw new IllegalArgumentException("Ticket Prices should have 5 values");
            }
            eventService.updateTicketPricing(1, eventId, ticketPrices.get(0));
            eventService.updateTicketPricing(2, eventId, ticketPrices.get(1));
            eventService.updateTicketPricing(3, eventId, ticketPrices.get(2));
            eventService.updateTicketPricing(4, eventId, ticketPrices.get(3));
            eventService.updateTicketPricing(5, eventId, ticketPrices.get(4));
        }

        EventUpdateDto eventUpdateDto = new EventUpdateDto(eventId, eventName, eventDescription, eventDate,
                eventLocation, otherEventInfo, ticketSaleDate, venue, eventType, null, null, null);
        Event event = eventService.updateEvent(eventUpdateDto);
        // update event image if not null
        if (eventImageFile != null) {
            try {
                System.out.println(eventImageFile.getName());
                amazonS3Service.uploadFile(eventImageFile, event.getEventImage(), "event_images");
                // delete event from db if got error saving image
            } catch (AmazonS3Exception e) {
                String errorCode = e.getErrorCode();
                if ("AccessDenied".equals(errorCode)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(generateApiResponse(null, "Access Denied to Amazon."));
                } else if ("NoSuchBucket".equals(errorCode)) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(generateApiResponse(null, "S3 bucket not found."));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                            generateApiResponse(null, "An error occurred during S3 interaction: " + e.getMessage()));
                }
            }
        }
        return ResponseEntity.ok(generateApiResponse(event, "Event updated successfully."));
    }

    /**
     * Deletes one or more events based on their unique identifiers. This action can
     * be performed by event organizers and administrators.
     *
     * @param eventIdString A comma-separated string of event IDs to be deleted.
     * @param request       The HTTPServletRequest for authorization and validation.
     * @return A ResponseEntity containing a GeneralApiResponse indicating the
     *         results of event deletion
     *         or an error message if an exception occurs during the deletion
     *         process.
     */
    @PutMapping("/event/delete")
    public ResponseEntity<GeneralApiResponse<Object>> deleteEvent(@RequestParam("eventId") String eventIdString,
            @NonNull HttpServletRequest request) {
        // Check if deleteEvent is called by admin or event Organiser
        boolean isAdmin = isAdminRequest(request);
        EventOrganiser organiser = null;

        if (!isAdmin) {
            organiser = retrieveOrganiserFromRequest(request);
        }

        try {
            List<Integer> eventIdList = Arrays.stream(eventIdString.split(","))
                    .map(Integer::parseInt)
                    .toList();

            // check if all events exist first
            for (Integer eventId : eventIdList) {
                if (eventRepository.findById(eventId).isEmpty()) {
                    throw new NonExistentException(String.format("Event %d does not exist, deletion halted", eventId));
                }
                if (!isAdmin && !eventRepository.existsEventByEventIdAndOrganiser(eventId, organiser)) {
                    throw new IllegalArgumentException("No such event listed under organiser, deletion halted");
                }
            }

            StringBuilder results = new StringBuilder();

            for (Integer eventId : eventIdList) {
                results.append(eventService.deleteEvent(eventId)).append(" ");
            }

            return ResponseEntity.ok(generateApiResponse(null, results.toString()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(generateApiResponse(null, e.getMessage()));
        }
    }

    /**
     * Update event with one or more artists based on the artist's and event's unique identifiers.
     * This action can be performed by event organizers and administrators.
     *
     * @param artistIdString A comma-separated string of artist IDs to be added to event if they
     *                       are not already there.
     * @param eventId       The unique identifier of the event to be featured.
     * @param request       The HTTPServletRequest for authorization and validation.
     * @return A ResponseEntity containing a GeneralApiResponse indicating the
     *         results of artist(s) being successfully assigned to the given event
     *         or an error message if an exception occurs during the deletion
     *         process.
     */
    @PutMapping("/event/update-artist")
    public ResponseEntity<GeneralApiResponse<Object>> updateEventArtist(@RequestParam("artistIdString") String artistIdString,
            @RequestParam("eventId") Integer eventId,
            @NonNull HttpServletRequest request) {
        EventOrganiser eventOrganiser = retrieveOrganiserFromRequest(request);
        if (!eventRepository.existsEventByEventIdAndOrganiser(eventId, eventOrganiser)) {
            throw new IllegalArgumentException("Event organiser does not have an event with id " + eventId);
        }

        List<Integer> artistIdList = Arrays.stream(artistIdString.split(","))
                .map(Integer::parseInt)
                .toList();

        // check that all artist is valid first
        for (Integer artistId : artistIdList) {
            if (artistRepository.findById(artistId).isEmpty()) {
                throw new NonExistentException(
                        String.format("Artist with id %d does not exist, please try again", artistId));
            }
        }

        eventService.removeAllArtistFromEvent(eventId);

        for (Integer artistId : artistIdList) {
            eventService.addArtistToEvent(artistId, eventId);
        }

        return ResponseEntity.ok(generateApiResponse(eventService.findArtistForEvent(eventId),
                String.format("Artist successfully assigned to event %d", eventId)));
    }

    /**
     * Saves an event as a featured event, allowing it to be displayed as a featured
     * event for a specified time period. This action is performed by
     * administrators.
     *
     * @param eventId   The unique identifier of the event to be featured.
     * @param startDate The start date and time for the featured event display.
     * @param endDate   The end date and time for the featured event display.
     * @param request   The HTTPServletRequest for authorization and validation.
     * @return A ResponseEntity containing a GeneralApiResponse indicating the
     *         success of saving the event as a featured event
     *         or an error message if an exception occurs during the saving process.
     */
    @PostMapping("/event/featured")
    public ResponseEntity<GeneralApiResponse<Object>> saveFeaturedEvents(@RequestParam("eventId") Integer eventId,
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            @NonNull HttpServletRequest request) {
        Event event = eventRepository.findById(eventId).orElse(null);
        Admin admin = retrieveAdminFromRequest(request);

        if (event == null) {
            throw new NonExistentException(String.format("No event of id %d found", eventId));
        } else if (admin == null) {
            throw new NonExistentException("Admin not found");
        } else if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("end date is earlier than start date");
        }
        try {
            FeaturedEvent featuredEvent = new FeaturedEvent(null, event, startDate, endDate, admin);
            return ResponseEntity.ok(generateApiResponse(eventService.saveFeaturedEvent(featuredEvent),
                    "Featured Event Successfully Saved"));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest()
                    .body(generateApiResponse(null, "Featured event with event id could already exists"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }

    }

    /**
     * Retrieves section ticket details for a specific event. This action is
     * available to the public.
     *
     * @param eventId The unique identifier of the event for which section ticket
     *                details are being retrieved.
     * @return A ResponseEntity containing a GeneralApiResponse with the section
     *         ticket details for the event
     *         or an error message if the event is not found, is not approved, or is
     *         deleted.
     */
    @GetMapping("/public/event/section-ticket-details/{eventId}")
    public ResponseEntity<GeneralApiResponse<Object>> findAllSectionsByEvent(
            @PathVariable("eventId") Integer eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);

        checkIfEventExistsAndIsApprovedAndNotDeleted(eventId);

        List<SectionTicketDetailsDto> sectionDetailsForEvent = eventService.findAllSectionDetailsForEvent(event);

        return ResponseEntity.ok(generateApiResponse(sectionDetailsForEvent,
                String.format("Success returning all section ticket details for event %d", eventId)));
    }

    /**
     * Checks if tickets are available for a specific event. This action is
     * available to event administrators.
     *
     * @param eventId The unique identifier of the event for which ticket
     *                availability is being checked.
     * @return A ResponseEntity containing a GeneralApiResponse indicating the
     *         availability of tickets for the event
     *         or an error message if the event does not exist.
     */
    @GetMapping("/event/available")
    public ResponseEntity<GeneralApiResponse<Object>> eventHasTickets(@RequestParam("eventId") Integer eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            throw new NonExistentException("Event", eventId);
        }

        return ResponseEntity.ok(generateApiResponse(ticketService.getEventHasTickets(event),
                String.format("Success returning tickets available for event %d", eventId)));
    }

    /**
     * Allows a user to indicate their interest in a specific event's presale. This
     * action is available to users.
     *
     * @param eventId The unique identifier of the event for which the user is
     *                indicating interest.
     * @param request The HTTPServletRequest for authorization and validation.
     * @return A ResponseEntity indicating the successful recording of presale
     *         interest or an error message if the event does not exist
     *         or the interest indication period has ended.
     */
    @PutMapping("/event/interest")
    public ResponseEntity<GeneralApiResponse<Object>> userIndicateInterest(@RequestParam("eventId") Integer eventId,
            @NonNull HttpServletRequest request) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();

        if (LocalDateTime.now().isAfter(event.getTicketSaleDate().minusHours(PRESALE_HOURS))) {
            throw new ApiRequestException(
                    "The presale interest indication period for event '" + event.getEventName() + "' has ended.");
        }

        User user = retrieveUserFromRequest(request);

        presaleService.setPresaleInterest(user, event, false, false);
        return ResponseEntity.status(201).body(generateApiResponse(null, "Presale interest recorded"));
    }

    /**
     * Check whether an event is a presale event.
     *
     * @param eventId The unique identifier of the event for which the user is
     *                indicating interest.
     * @return A ResponseEntity with information about the presale status of the event.
     *          if event exists
     */
    @GetMapping("/event/presale-event")
    public ResponseEntity<GeneralApiResponse<Object>> isPresaleEvent(@RequestParam("eventId") Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }

        return ResponseEntity.ok(generateApiResponse(eventOptional.get().getHasPresale(),
                "Returned presale status for event " + eventId));
    }

    /**
     * Checks the presale status of a specific event for a given user. This action
     * is available to users.
     *
     * @param eventId The unique identifier of the event for which the presale
     *                status is being checked.
     * @param userId  The unique identifier of the user for whom the presale status
     *                is being checked.
     * @return A ResponseEntity indicating the presale status for the event and user
     *         or an error message if either the event or the user does not exist or
     *         if the event does not have a presale period.
     */
    @GetMapping("/event/presale-status")
    public ResponseEntity<GeneralApiResponse<Object>> checkPresaleStatus(@RequestParam("eventId") Integer eventId,
            @RequestParam("userId") Integer userId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();
        if (!event.getHasPresale()) {
            throw new IllegalArgumentException("Event '" + event.getEventName() + "' does not have a presale period");
        }

        Optional<User> userOptional = userRepository.findUserByUserId(userId);
        if (userOptional.isEmpty()) {
            throw new NonExistentException("User", userId);
        }
        User user = userOptional.get();

        return ResponseEntity.ok(generateApiResponse(presaleService.existsById(new EventUserId(user, event)),
                "Returned presale status for event id " + eventId + ", user id " + userId));
    }

    /**
     * Checks if a user has been selected for presale access to a specific event.
     * This action is available to users.
     *
     * @param eventId The unique identifier of the event for which the user's
     *                selection status is being checked.
     * @param request The HTTPServletRequest for authorization and validation.
     * @return A ResponseEntity indicating whether the user has been selected for
     *         presale access to the event or not,
     *         or an error message if the event, user, or the event's presale period
     *         does not exist.
     */
    @GetMapping("/event/user-selected")
    public ResponseEntity<GeneralApiResponse<Object>> checkIfUserSelected(@RequestParam("eventId") Integer eventId,
            // @RequestParam("userId") Integer userId,
            @NonNull HttpServletRequest request) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();
        if (!event.getHasPresale()) {
            throw new IllegalArgumentException("Event '" + event.getEventName() + "' does not have a presale period");
        }

        User user = retrieveUserFromRequest(request);

        Optional<PresaleInterest> presaleInterestOptional = presaleService
                .findPresaleInterestByID(new EventUserId(user, event));
        if (presaleInterestOptional.isPresent() && presaleInterestOptional.get().getIsSelected()) {
            return ResponseEntity.ok(generateApiResponse(true, "User " + user.getUserId() + " has been selected"));
        }

        return ResponseEntity.ok(generateApiResponse(false, "User " + user.getUserId() + " has not been selected"));
    }

    /**
     * Retrieves a list of users who have been selected to participate in the
     * presale for a specific event.
     * This action is available to authorized users.
     *
     * @param eventId The unique identifier of the event for which the selected
     *                users are being retrieved.
     * @return A ResponseEntity containing a list of users allowed in the presale
     *         for the event, or an error message
     *         if the event, its presale period, or user selection information does
     *         not exist.
     */
    @GetMapping("/event/selected-users")
    public ResponseEntity<GeneralApiResponse<Object>> getEventSelectedUsers(@RequestParam("eventId") Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();
        if (!event.getHasPresale()) {
            throw new IllegalArgumentException("Event '" + event.getEventName() + "' does not have a presale period");
        }
        if (!event.getHasPresaleUsers()) {
            throw new InvalidRequestException("Users have yet to be selected");
        }

        return ResponseEntity.ok(generateApiResponse(presaleService.findUsersSelectedForEvent(event, true),
                "Returned list of users allowed in presale"));
    }

    @GetMapping("/event/purchaseable-tickets")
    public ResponseEntity<GeneralApiResponse<Object>> getNumberOfPurchaseableTickets(@RequestParam("eventId") Integer eventId,
                                                                                     @NonNull HttpServletRequest request) {
        User user = retrieveUserFromRequest(request);

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();

        return ResponseEntity.ok(generateApiResponse(ticketService.getNumberOfTicketsPurchaseable(event, user), "Returned number of tickets user can purchase"));
    }

    @GetMapping("/event/queue-position")
    public ResponseEntity<GeneralApiResponse<Object>> getQueuePosition(@RequestParam("eventId") Integer eventId,
                                                                       @NonNull HttpServletRequest request) {
        User user = retrieveUserFromRequest(request);

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();

        return ResponseEntity.ok(generateApiResponse(queueService.getPosition(user, event), "Returned queue number"));
    }

    @GetMapping("/event/queue-total")
    public ResponseEntity<GeneralApiResponse<Object>> getQueuePosition(@RequestParam("eventId") Integer eventId) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();

        return ResponseEntity.ok(generateApiResponse(queueService.getTotalInQueue(event), "Returned number of users in queue"));
    }

    @PutMapping("/event/enter-queue")
    public ResponseEntity<GeneralApiResponse<Object>> enterQueue(@RequestParam("eventId") Integer eventId,
                                                                 @NonNull HttpServletRequest request) {
        User user = retrieveUserFromRequest(request);

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();

        queueService.addToQueue(user, event);
        return ResponseEntity.status(201).body(generateApiResponse(queueService.getPosition(user, event), "Added to queue and returned queue number"));
    }

    @PutMapping("/event/leave-queue")
    public ResponseEntity<GeneralApiResponse<Object>> leaveQueue(@RequestParam("eventId") Integer eventId,
                                                                 @NonNull HttpServletRequest request) {
        User user = retrieveUserFromRequest(request);

        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = eventOptional.get();

        queueService.removeFromQueue(user, event);
        return ResponseEntity.ok(generateApiResponse(null, "Removed from queue"));
      
    /**
     * Handles the check-in process for event tickets based on a JWT token.
     *
     * @param token    The JWT token provided in the request query parameters.
     * @param request  The HTTP request object containing information about the request.
     * @return A ResponseEntity containing a GeneralApiResponse with ticket details on success.
     * @throws InvalidRequestException if the token is expired or has an invalid role.
     * @throws IllegalArgumentException if the ticket ID is not valid, or the ticket does not correspond to the event by the organiser.
     */
    @PutMapping("/event/valid-qr")
    public ResponseEntity<GeneralApiResponse<Object>> getQR(@RequestParam("token")String token,
                                                            @NonNull HttpServletRequest request) {
        EventOrganiser organiser = retrieveOrganiserFromRequest(request);

        if (jwtService.isTokenExpired(token)) {
            throw new InvalidRequestException("Ticket has expired");
        }

        if (!"ticket".equals(jwtService.extractRole(token))) {
            throw new InvalidRequestException("Token role is not valid");
        }

        try {
            Integer ticketId = Integer.parseInt(jwtService.extractUsername(token));
            TicketDisplayDto dto = ticketService.findTicketById(ticketId);
            if (!eventRepository.existsEventByEventIdAndOrganiser(dto.eventId(), organiser)) {
                throw new IllegalArgumentException("Ticket does not correspond to event by organiser");
            }
            if (dto.checkedIn()) {
                throw new InvalidRequestException("User already checked in");
            }
            ticketService.setCheckIn(ticketId, true);
            return ResponseEntity.ok(generateApiResponse(dto, "Returned ticket details"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ticket ID for QR is not valid.");
        }
    }
}
