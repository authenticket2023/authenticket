package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.dto.section.SectionDtoMapper;
import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.EmailService;
import com.authenticket.authenticket.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final ArtistRepository artistRepository;

    private final FeaturedEventRepository featuredEventRepository;

    private final TicketCategoryRepository ticketCategoryRepository;

    private final TicketPricingRepository ticketPricingRepository;

    private final EventDtoMapper eventDTOMapper;

    private final SectionDtoMapper sectionDtoMapper;

    private final ArtistDtoMapper artistDtoMapper;

    private final AmazonS3Service amazonS3Service;

    private final TicketRepository ticketRepository;

    private final EmailService emailService;

    /**
     * Constructs an instance of EventServiceImpl with the specified dependencies.
     * This constructor is used for dependency injection to set various
     * repositories,
     * mappers, services, and other dependencies required for managing events in the
     * application.
     *
     * @param eventRepository          The EventRepository to interact with event
     *                                 data.
     * @param artistRepository         The ArtistRepository to interact with artist
     *                                 data.
     * @param featuredEventRepository  The FeaturedEventRepository to manage
     *                                 featured events.
     * @param ticketCategoryRepository The TicketCategoryRepository to work with
     *                                 ticket categories.
     * @param ticketPricingRepository  The TicketPricingRepository to manage ticket
     *                                 pricing information.
     * @param eventDTOMapper           The EventDtoMapper for mapping Event entities
     *                                 to DTOs.
     * @param artistDtoMapper          The ArtistDtoMapper for mapping Artist
     *                                 entities to DTOs.
     * @param amazonS3Service          The AmazonS3Service for interacting with
     *                                 Amazon S3 storage.
     * @param emailService             The EmailService for sending emails.
     * @param ticketRepository         The TicketRepository for ticket-related
     *                                 operations.
     * @param sectionDtoMapper         The SectionDtoMapper for mapping section
     *                                 details to DTOs.
     */
    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
            ArtistRepository artistRepository,
            FeaturedEventRepository featuredEventRepository,
            TicketCategoryRepository ticketCategoryRepository,
            TicketPricingRepository ticketPricingRepository,
            EventDtoMapper eventDTOMapper,
            ArtistDtoMapper artistDtoMapper,
            AmazonS3Service amazonS3Service,
            EmailService emailService,
            TicketRepository ticketRepository,
            SectionDtoMapper sectionDtoMapper) {
        this.eventRepository = eventRepository;
        this.artistRepository = artistRepository;
        this.featuredEventRepository = featuredEventRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.ticketPricingRepository = ticketPricingRepository;
        this.eventDTOMapper = eventDTOMapper;
        this.artistDtoMapper = artistDtoMapper;
        this.amazonS3Service = amazonS3Service;
        this.emailService = emailService;
        this.ticketRepository = ticketRepository;
        this.sectionDtoMapper = sectionDtoMapper;
    }

    /**
     * Retrieves a list of public events that are suitable for display on the home
     * page.
     *
     * @param pageable Pagination and sorting information for the results.
     * @return A list of EventHomeDto objects, each representing an approved public
     *         event.
     */
    @Override
    public List<EventHomeDto> findAllPublicEvent(Pageable pageable) {
        return eventDTOMapper.mapEventHomeDto(eventRepository
                .findAllByReviewStatusAndDeletedAtIsNull(Event.ReviewStatus.APPROVED.getStatusValue(), pageable)
                .getContent());
    }

    /**
     * Retrieves a list of all events intended for administrative purposes.
     *
     * @return A list of EventAdminDisplayDto objects, each representing an event
     *         with details suitable for administrative use.
     */
    @Override
    public List<EventAdminDisplayDto> findAllEvent() {
        return eventDTOMapper.mapEventAdminDisplayDto(eventRepository.findAllByOrderByEventIdAsc());
    }

    /**
     * Retrieves an event's details by its unique identifier.
     *
     * @param eventId The unique identifier of the event to retrieve.
     * @return An OverallEventDto containing the details of the event, or null if
     *         the event with the given ID does not exist.
     */
    @Override
    public OverallEventDto findEventById(Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            OverallEventDto overallEventDto = eventDTOMapper.applyOverallEventDto(event);
            return overallEventDto;
        }
        return null;
    }

    /**
     * Retrieves a list of recently added events for display on the home page.
     *
     * @param pageable A Pageable object to specify pagination settings.
     * @return A list of EventHomeDto objects containing details of recently added
     *         events, ordered by creation date.
     */
    @Override
    public List<EventHomeDto> findRecentlyAddedEvents(Pageable pageable) {
        return eventDTOMapper
                .mapEventHomeDto(eventRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtDesc(
                        Event.ReviewStatus.APPROVED.getStatusValue(), pageable).getContent());

    }

    /**
     * Retrieves a list of featured events for display, filtered by their start and
     * end dates.
     *
     * @param pageable A Pageable object to specify pagination settings.
     * @return A list of FeaturedEventDto objects containing details of featured
     *         events.
     */
    @Override
    public List<FeaturedEventDto> findFeaturedEvents(Pageable pageable) {
        Page<FeaturedEvent> featuredEvents = featuredEventRepository
                .findAllFeaturedEventsByStartDateBeforeAndEndDateAfter(LocalDateTime.now(), LocalDateTime.now(),
                        pageable);
        return eventDTOMapper.mapFeaturedEventDto(featuredEvents.getContent());
    }

    /**
     * Retrieves a list of best-selling events for display.
     *
     * @return A list of EventHomeDto objects containing details of the best-selling
     *         events.
     */
    @Override
    public List<EventHomeDto> findBestSellerEvents() {
        return eventDTOMapper.mapEventHomeDtoForObj(eventRepository.findBestSellerEvents());
    }

    /**
     * Retrieves a list of upcoming events based on their ticket sale dates.
     *
     * @param pageable Pagination information to control the number of results
     *                 returned.
     * @return A list of EventHomeDto objects containing details of upcoming events.
     */
    @Override
    public List<EventHomeDto> findUpcomingEventsByTicketSalesDate(Pageable pageable) {
        LocalDateTime currentDate = LocalDateTime.now();
        return eventDTOMapper
                .mapEventHomeDto(
                        eventRepository
                                .findAllByReviewStatusAndTicketSaleDateAfterAndDeletedAtIsNullOrderByTicketSaleDateAsc(
                                        Event.ReviewStatus.APPROVED.getStatusValue(), currentDate, pageable)
                                .getContent());
    }

    /**
     * Retrieves a list of current events based on their event dates.
     *
     * @param pageable Pagination information to control the number of results
     *                 returned.
     * @return A list of EventHomeDto objects containing details of current events.
     */
    @Override
    public List<EventHomeDto> findCurrentEventsByEventDate(Pageable pageable) {
        LocalDateTime currentDate = LocalDateTime.now();
        return eventDTOMapper
                .mapEventHomeDto(
                        eventRepository
                                .findAllByReviewStatusAndEventDateAfterAndDeletedAtIsNullOrderByEventDateAsc(
                                        Event.ReviewStatus.APPROVED.getStatusValue(), currentDate, pageable)
                                .getContent());
    }

    /**
     * Retrieves a list of past events based on their event dates, considering the
     * specified pagination.
     *
     * @param pageable Pagination information to control the result size and page
     *                 number.
     * @return A list of {@link EventHomeDto} objects representing past events,
     *         sorted by event date in descending order.
     * @see EventHomeDto
     */
    @Override
    public List<EventHomeDto> findPastEventsByEventDate(Pageable pageable) {
        LocalDateTime currentDate = LocalDateTime.now();
        return eventDTOMapper
                .mapEventHomeDto(
                        eventRepository
                                .findAllByReviewStatusAndEventDateBeforeAndDeletedAtIsNullOrderByEventDateDesc(
                                        Event.ReviewStatus.APPROVED.getStatusValue(), currentDate, pageable)
                                .getContent());
    }

    /**
     * Retrieves a list of events based on their review status, sorted by creation
     * date in ascending order.
     *
     * @param reviewStatus The review status of the events to retrieve (e.g.,
     *                     "approved", "pending", "rejected").
     * @return A list of {@link EventDisplayDto} objects representing events with
     *         the specified review status, sorted by creation date in ascending
     *         order.
     * @see EventDisplayDto
     */
    @Override
    public List<EventDisplayDto> findEventsByReviewStatus(String reviewStatus) {
        LocalDateTime currentDate = LocalDateTime.now();
        return eventDTOMapper
                .map(eventRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(reviewStatus));
    }

    /**
     * Find a list of EventHomeDto objects by a given venue ID and pageable information.
     *
     * @param venueId   The unique identifier of the venue.
     * @param pageable  Pageable object for pagination and sorting.
     * @return A list of EventHomeDto objects representing events at the specified venue.
     */
    @Override
    public List<EventHomeDto> findEventsByVenue(Integer venueId, Pageable pageable) {
        return eventDTOMapper.mapEventHomeDto(
                eventRepository.findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullOrderByEventDateDesc(
                        Event.ReviewStatus.APPROVED.getStatusValue(), venueId, pageable).getContent());
    }

    /**
     * Retrieves a list of past events that took place at a specific venue, based on
     * the venue's unique identifier (venueId).
     *
     * @param venueId  The unique identifier of the venue.
     * @param pageable Pagination information for retrieving a subset of events.
     * @return A list of {@link EventHomeDto} objects representing past events that
     *         occurred at the specified venue, sorted by event date in descending
     *         order.
     * @see EventHomeDto
     */
    @Override
    public List<EventHomeDto> findPastEventsByVenue(Integer venueId, Pageable pageable) {
        return eventDTOMapper.mapEventHomeDto(eventRepository
                .findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullAndEventDateBeforeOrderByEventDateDesc(
                        Event.ReviewStatus.APPROVED.getStatusValue(), venueId, pageable, LocalDateTime.now())
                .getContent());
    }

    /**
     * Retrieves a list of upcoming events scheduled to take place at a specific
     * venue, based on the venue's unique identifier (venueId).
     *
     * @param venueId  The unique identifier of the venue.
     * @param pageable Pagination information for retrieving a subset of events.
     * @return A list of {@link EventHomeDto} objects representing upcoming events
     *         scheduled at the specified venue, sorted by event date in descending
     *         order.
     * @see EventHomeDto
     */
    @Override
    public List<EventHomeDto> findUpcomingEventsByVenue(Integer venueId, Pageable pageable) {
        return eventDTOMapper.mapEventHomeDto(eventRepository
                .findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullAndEventDateAfterOrderByEventDateDesc(
                        Event.ReviewStatus.APPROVED.getStatusValue(), venueId, pageable, LocalDateTime.now())
                .getContent());
    }

    /**
     * Saves a new event or updates an existing event in the database.
     *
     * @param event The {@link Event} object to be saved or updated.
     * @return The saved or updated {@link Event} object with its database-generated
     *         identifier.
     * @see Event
     */
    @Override
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    /**
     * Saves a new featured event in the database.
     *
     * @param featuredEvent The {@link FeaturedEvent} object to be saved.
     * @return The saved {@link FeaturedEventDto} object, which represents the newly
     *         saved featured event.
     * @see FeaturedEvent
     * @see FeaturedEventDto
     */
    @Override
    public FeaturedEventDto saveFeaturedEvent(FeaturedEvent featuredEvent) {

        return eventDTOMapper.applyFeaturedEventDto(featuredEventRepository.save(featuredEvent));
    }

    /**
     * Updates an existing event with the information provided in the
     * {@link EventUpdateDto}.
     *
     * @param eventUpdateDto The data transfer object containing updated event
     *                       information.
     * @return The updated {@link Event} object after the changes have been applied.
     * @throws NonExistentException If the event with the specified ID does not
     *                              exist in the database.
     * @see EventUpdateDto
     * @see Event
     * @see NonExistentException
     */
    @Override
    public Event updateEvent(EventUpdateDto eventUpdateDto) {
        Optional<Event> eventOptional = eventRepository.findById(eventUpdateDto.eventId());

        if (eventOptional.isPresent()) {
            Event existingEvent = eventOptional.get();
            eventDTOMapper.update(eventUpdateDto, existingEvent);
            existingEvent.setUpdatedAt(LocalDateTime.now());
            // Send email
            String reviewStatus = eventUpdateDto.reviewStatus();
            if (reviewStatus != null) {
                if (reviewStatus.equals(Event.ReviewStatus.APPROVED.getStatusValue())
                        || reviewStatus.equals(Event.ReviewStatus.REJECTED.getStatusValue())) {
                    EventOrganiser eventOrganiser = existingEvent.getOrganiser();
                    // Send email to organiser
                    emailService.send(eventOrganiser.getEmail(), EmailServiceImpl.buildEventReviewEmail(existingEvent),
                            "Event Review");
                }
            }

            eventRepository.save(existingEvent);
            return existingEvent;
        }

        throw new NonExistentException("Event", eventUpdateDto.eventId());
    }

    /**
     * Deletes an event with the specified ID.
     *
     * @param eventId The ID of the event to be deleted.
     * @return A message indicating the result of the deletion.
     * @throws NonExistentException If the event with the specified ID does not
     *                              exist in the database.
     * @see Event
     * @see NonExistentException
     */
    @Override
    public String deleteEvent(Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getDeletedAt() != null) {
                return String.format("Event %d is already deleted.", eventId);
            } else {
                event.setDeletedAt(LocalDateTime.now());
                eventRepository.save(event);
                return String.format("Event %d is successfully deleted.", eventId);
            }
        } else {
            throw new NonExistentException("Event", eventId);
        }

    }

    /**
     * Adds an artist to an event.
     *
     * @param artistId The ID of the artist to be added.
     * @param eventId  The ID of the event to which the artist should be added.
     * @return An EventDisplayDto representing the updated event with the artist
     *         added.
     * @throws NonExistentException   If either the artist with the specified ID or
     *                                the event with the specified ID does not exist
     *                                in the database.
     * @throws AlreadyExistsException If the artist is already linked to the stated
     *                                event.
     * @see Artist
     * @see Event
     * @see EventDisplayDto
     * @see NonExistentException
     * @see AlreadyExistsException
     */
    @Override
    public EventDisplayDto addArtistToEvent(Integer artistId, Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<Artist> artistOptional = artistRepository.findById(artistId);

        if (artistOptional.isPresent() && eventOptional.isPresent()) {

            Artist artist = artistOptional.get();
            Event event = eventOptional.get();
            Set<Artist> artistSet = event.getArtists();
            if (artistSet == null) {
                artistSet = new HashSet<>();
            }
            if (!artistSet.contains(artist)) {

                artistSet.add(artist);
                event.setArtists(artistSet);

                eventRepository.save(event);
                return eventDTOMapper.apply(event);
            } else {
                throw new AlreadyExistsException("Artist already linked to stated event");
            }
        } else {
            if (artistOptional.isEmpty()) {
                throw new NonExistentException("Artist does not exists");
            } else {
                throw new NonExistentException("Event does not exists");
            }
        }
    }

    /**
     * Removes all artists from an event.
     *
     * @param eventId The ID of the event from which all artists should be removed.
     * @see Event
     */
    @Override
    public void removeAllArtistFromEvent(Integer eventId) {
        eventRepository.deleteAllArtistByEventId(eventId);
    }

    /**
     * Adds a ticket category to an event with the specified price.
     *
     * @param catId   The ID of the ticket category to be added.
     * @param eventId The ID of the event to which the ticket category should be
     *                added.
     * @param price   The price for the ticket category.
     * @return The updated event with the newly added ticket category.
     * @throws AlreadyExistsException if the ticket category is already linked to
     *                                the event.
     * @throws NonExistentException   if the ticket category or event does not
     *                                exist.
     * @see Event
     * @see TicketCategory
     */
    @Override
    public EventDisplayDto addTicketCategory(Integer catId, Integer eventId, Double price) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<TicketPricing> eventTicketCategoryOptional = ticketPricingRepository
                    .findById(new EventTicketCategoryId(ticketCategory, event));
            if (eventTicketCategoryOptional.isPresent()) {
                throw new AlreadyExistsException("Ticket Category already linked to stated event");
            }

            event.addTicketPricing(ticketCategory, price);
            // adding to total tickets
            // Integer currentTotalTickets = event.getTotalTickets();
            // currentTotalTickets += totalTicketsPerCat;
            // event.setTotalTickets(currentTotalTickets);

            // adding to total tickets sold
            // Integer currentTotalTicketsSold = event.getTotalTicketsSold();
            // currentTotalTicketsSold += (totalTicketsPerCat - availableTickets);
            // event.setTotalTicketsSold(currentTotalTicketsSold);

            eventRepository.save(event);
            return eventDTOMapper.apply(event);
            // Set<EventTicketCategory> eventTicketCategorySet =
            // event.getEventTicketCategorySet();

            // if(eventTicketCategoryOptional.isEmpty()){
            // EventTicketCategory eventTicketCategory = new
            // EventTicketCategory(ticketCategory, event, price, availableTickets,
            // totalTicketsPerCat);
            // eventTicketCategoryRepository.save(eventTicketCategory);
            //
            // eventTicketCategorySet.add(new EventTicketCategory(ticketCategory, event,
            // price, availableTickets, totalTicketsPerCat));
            // System.out.println(eventTicketCategorySet.size());
            // event.setEventTicketCategorySet(eventTicketCategorySet);
            //
            // eventRepository.save(event);
            // return eventDTOMapper.apply(event);
            // } else {
            // throw new AlreadyExistsException("Ticket Category already linked to stated
            // event");
            // }
        } else {
            if (categoryOptional.isEmpty()) {
                throw new NonExistentException("Category does not exist");
            } else {
                throw new NonExistentException("Event does not exist");
            }
        }
    }

    /**
     * Updates the pricing for a ticket category linked to an event.
     *
     * @param catId   The ID of the ticket category to be updated.
     * @param eventId The ID of the event to which the ticket category is linked.
     * @param price   The new price for the ticket category.
     * @throws NonExistentException if the ticket category or event does not exist,
     *                              or if the ticket category is not linked to the
     *                              event.
     * @see Event
     * @see TicketCategory
     */
    @Override
    public void updateTicketPricing(Integer catId, Integer eventId, Double price) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<TicketPricing> ticketPricingOptional = ticketPricingRepository
                    .findById(new EventTicketCategoryId(ticketCategory, event));
            if (ticketPricingOptional.isEmpty()) {
                throw new NonExistentException("Ticket Category " + ticketCategory.getCategoryName()
                        + " is not linked to Event '" + event.getEventName() + "'");
            }
            TicketPricing ticketPricing = ticketPricingOptional.get();

            // adding to total tickets
            // Integer currentTotalTickets = event.getTotalTickets();
            // currentTotalTickets += totalTicketsPerCat;
            // event.setTotalTickets(currentTotalTickets);

            // adding to total tickets sold
            // Integer currentTotalTicketsSold = event.getTotalTicketsSold();
            // currentTotalTicketsSold += (totalTicketsPerCat - availableTickets);
            // event.setTotalTicketsSold(currentTotalTicketsSold);

            event.updateTicketPricing(ticketPricing, price);
            eventRepository.save(event);
        } else {
            if (categoryOptional.isEmpty()) {
                throw new NonExistentException("Category does not exist");
            } else {
                throw new NonExistentException("Event does not exist");
            }
        }
    }

    /**
     * Removes a ticket category from an event, effectively unlinking it.
     *
     * @param catId   The ID of the ticket category to be removed from the event.
     * @param eventId The ID of the event from which the ticket category should be
     *                removed.
     * @return The updated event information after removing the ticket category.
     * @throws NonExistentException if the ticket category is not linked to the
     *                              event, or if either the category or the event
     *                              does not exist.
     * @see Event
     * @see TicketCategory
     */
    @Override
    public EventDisplayDto removeTicketCategory(Integer catId, Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<TicketPricing> eventTicketCategoryOptional = ticketPricingRepository
                    .findById(new EventTicketCategoryId(ticketCategory, event));
            if (eventTicketCategoryOptional.isEmpty()) {
                throw new NonExistentException("Ticket Category not linked to stated event");
            }

            event.removeTicketCategory(ticketCategory);
            eventRepository.save(event);
            return eventDTOMapper.apply(event);
        } else {
            if (categoryOptional.isEmpty()) {
                throw new NonExistentException("Category does not exist");
            } else {
                throw new NonExistentException("Event does not exist");
            }
        }
    }

    /**
     * Returns the set of artists associated with a specific event.
     *
     * @param eventId The ID of the event for which to retrieve the associated
     *                artists.
     * @return A set of artist information (display DTOs) linked to the specified
     *         event.
     * @throws NonExistentException if the event with the given ID does not exist.
     * @see Event
     * @see ArtistDisplayDto
     */
    @Override
    // return artist for a specific event
    public Set<ArtistDisplayDto> findArtistForEvent(Integer eventId) throws NonExistentException {

        if (eventRepository.findById(eventId).isEmpty()) {
            throw new NonExistentException("Event does not exist");
        }
        List<Object[]> artistObject = eventRepository.getArtistByEventId(eventId);
        Set<ArtistDisplayDto> artistDisplayDtoList = artistDtoMapper.mapArtistDisplayDto(artistObject);
        return artistDisplayDtoList;
    }

    /**
     * Retrieves and returns the section ticket details for a given event.
     *
     * @param event The event for which to fetch section ticket details.
     * @return A list of SectionTicketDetailsDto containing information about ticket
     *         sections for the specified event.
     * @see Event
     * @see SectionTicketDetailsDto
     */
    @Override
    public List<SectionTicketDetailsDto> findAllSectionDetailsForEvent(Event event) {
        return sectionDtoMapper
                .mapSectionTicketDetailsDto(ticketRepository.findAllTicketDetailsBySectionForEvent(event.getEventId()));
    }

    /**
     * Retrieves and returns a list of events organized by a specific organizer
     * based on the enhanced status.
     *
     * @param organiserId The unique identifier of the event organizer.
     * @param enhanced    A boolean flag indicating whether to filter enhanced
     *                    events (true) or non-enhanced events (false).
     * @return A list of EventHomeDto containing event information that matches the
     *         specified criteria.
     * @see EventHomeDto
     * @see Event
     */
    @Override
    public List<EventHomeDto> findEventsByOrganiserAndEnhancedStatus(Integer organiserId, Boolean enhanced) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        List<EventHomeDto> eventHomeDtoList = eventDTOMapper.mapEventHomeDto(eventRepository
                .findAllByReviewStatusAndEventDateAfterAndDeletedAtIsNullAndIsEnhancedAndOrganiserOrganiserIdOrderByEventDateAsc(
                        Event.ReviewStatus.APPROVED.getStatusValue(), currentDateTime, enhanced, organiserId));

        return eventHomeDtoList;
    }
}
