package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.event.*;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.AmazonS3Service;
import com.authenticket.authenticket.service.EmailService;
import com.authenticket.authenticket.service.EventService;
import org.hibernate.sql.ast.tree.expression.Over;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final ArtistRepository artistRepository;

    private final FeaturedEventRepository featuredEventRepository;

    private final TicketCategoryRepository ticketCategoryRepository;

    private final EventTicketCategoryRepository eventTicketCategoryRepository;

    private final EventDtoMapper eventDTOMapper;

    private final ArtistDtoMapper artistDtoMapper;

    private final AmazonS3Service amazonS3Service;

    private final EmailService emailService;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository,
                            ArtistRepository artistRepository,
                            FeaturedEventRepository featuredEventRepository,
                            TicketCategoryRepository ticketCategoryRepository,
                            EventTicketCategoryRepository eventTicketCategoryRepository,
                            EventDtoMapper eventDTOMapper,
                            ArtistDtoMapper artistDtoMapper,
                            AmazonS3Service amazonS3Service,
                            EmailService emailService) {
        this.eventRepository = eventRepository;
        this.artistRepository = artistRepository;
        this.featuredEventRepository = featuredEventRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.eventTicketCategoryRepository = eventTicketCategoryRepository;
        this.eventDTOMapper = eventDTOMapper;
        this.artistDtoMapper = artistDtoMapper;
        this.amazonS3Service = amazonS3Service;
        this.emailService = emailService;
    }

    //get all events for home page
    public List<EventHomeDto> findAllPublicEvent(Pageable pageable) {
        return eventDTOMapper.mapEventHomeDto(eventRepository.findAllByReviewStatusAndDeletedAtIsNull("approved",pageable).getContent());
    }


    //find all events for admin
    public List<EventAdminDisplayDto> findAllEvent() {
        return eventDTOMapper.mapEventAdminDisplayDto(eventRepository.findAllByOrderByEventIdAsc());
    }

    public OverallEventDto findEventById(Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            OverallEventDto overallEventDto = eventDTOMapper.applyOverallEventDto(event);
            return overallEventDto;
        }
        return null;
    }
    //find recently added events by created at date for home
    public List<EventHomeDto> findRecentlyAddedEvents(Pageable pageable) {
        return eventDTOMapper.mapEventHomeDto(eventRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtDesc("approved", pageable).getContent());

    }

    public List<FeaturedEventDto> findFeaturedEvents(Pageable pageable) {
        Page<FeaturedEvent> featuredEvents = featuredEventRepository.findAllFeaturedEventsByStartDateBeforeAndEndDateAfter(LocalDateTime.now(),LocalDateTime.now(),pageable);
        return eventDTOMapper.mapFeaturedEventDto(featuredEvents.getContent());
    }

    public List<EventHomeDto> findBestSellerEvents(Pageable pageable) {
        return eventDTOMapper.mapEventHomeDto(eventRepository.findBestSellerEvents(pageable).getContent());
    }


    public List<EventHomeDto> findUpcomingEventsByTicketSalesDate(Pageable pageable) {
        LocalDateTime currentDate = LocalDateTime.now();
        return eventDTOMapper.mapEventHomeDto(eventRepository.findAllByReviewStatusAndTicketSaleDateAfterAndDeletedAtIsNullOrderByTicketSaleDateAsc("approved", currentDate,pageable).getContent());
    }

    public List<EventHomeDto> findCurrentEventsByEventDate(Pageable pageable) {
        LocalDateTime currentDate = LocalDateTime.now();
        return eventDTOMapper.mapEventHomeDto(eventRepository.findAllByReviewStatusAndEventDateAfterAndDeletedAtIsNullOrderByEventDateAsc("approved", currentDate,pageable).getContent());
    }

    public List<EventHomeDto> findPastEventsByEventDate(Pageable pageable) {
        LocalDateTime currentDate = LocalDateTime.now();
        return eventDTOMapper.mapEventHomeDto(eventRepository.findAllByReviewStatusAndEventDateBeforeAndDeletedAtIsNullOrderByEventDateDesc("approved", currentDate,pageable).getContent());
    }

    public List<EventDisplayDto> findEventsByReviewStatus(String reviewStatus) {
        LocalDateTime currentDate = LocalDateTime.now();
        return eventDTOMapper.map(eventRepository.findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(reviewStatus));
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public FeaturedEventDto saveFeaturedEvent(FeaturedEvent featuredEvent) {

        return eventDTOMapper.applyFeaturedEventDto(featuredEventRepository.save(featuredEvent));
    }

    public Event updateEvent(EventUpdateDto eventUpdateDto) {
        Optional<Event> eventOptional = eventRepository.findById(eventUpdateDto.eventId());

        if (eventOptional.isPresent()) {
            Event existingEvent = eventOptional.get();
            eventDTOMapper.update(eventUpdateDto, existingEvent);

            // Send email
            String reviewStatus = eventUpdateDto.reviewStatus();
            if (reviewStatus != null) {
                if(reviewStatus.equals("approved") || reviewStatus.equals("rejected")) {
                    EventOrganiser eventOrganiser = existingEvent.getOrganiser();
                    // Send email to organiser

                    emailService.send(eventOrganiser.getEmail(), EmailServiceImpl.buildEventReviewEmail(existingEvent), "Event Review");
                }
            }

            eventRepository.save(existingEvent);
            return existingEvent;
        }

        throw new NonExistentException("Event", eventUpdateDto.eventId());
    }


    public String deleteEvent(Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            if (event.getDeletedAt() != null) {
                return String.format("Event %d is already deleted.", eventId);
            } else{
                event.setDeletedAt(LocalDateTime.now());
                eventRepository.save(event);
                return String.format("Event %d is successfully deleted.", eventId);
            }
        } else {
            return String.format("Event %d does not exist, deletion unsuccessful.", eventId);
        }

    }

    public String removeEvent(Integer eventId) {

        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            String imageName = event.getEventImage();
            if (event.getEventImage() != null) {
                amazonS3Service.deleteFile(imageName, "event_organiser_profile");
            }
            eventRepository.deleteById(eventId);
            return "event removed successfully";
        }
        return "error: event does not exist";

    }

    public Event approveEvent(Integer eventId, Integer adminId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
//            event.setReviewedBy(adminId);
            eventRepository.save(event);
            return event;
        }
        return null;
    }

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

    public EventDisplayDto addTicketCategory(Integer catId, Integer eventId, Double price, Integer availableTickets, Integer totalTicketsPerCat) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<EventTicketCategory> eventTicketCategoryOptional = eventTicketCategoryRepository.findById(new EventTicketCategoryId(ticketCategory, event));
            if (eventTicketCategoryOptional.isPresent()) {
                throw new AlreadyExistsException("Ticket Category already linked to stated event");
            }

            event.addTicketCategory(ticketCategory, price, availableTickets, totalTicketsPerCat);
            //adding to total tickets
            Integer currentTotalTickets = event.getTotalTickets();
            currentTotalTickets += totalTicketsPerCat;
            event.setTotalTickets(currentTotalTickets);

            //adding to total tickets sold
            Integer currentTotalTicketsSold = event.getTotalTicketsSold();
            currentTotalTicketsSold += (totalTicketsPerCat - availableTickets);
            event.setTotalTicketsSold(currentTotalTicketsSold);

            eventRepository.save(event);
            return eventDTOMapper.apply(event);
//            Set<EventTicketCategory> eventTicketCategorySet = event.getEventTicketCategorySet();

//            if(eventTicketCategoryOptional.isEmpty()){
//                EventTicketCategory eventTicketCategory = new EventTicketCategory(ticketCategory, event, price, availableTickets, totalTicketsPerCat);
//                eventTicketCategoryRepository.save(eventTicketCategory);
//
//                eventTicketCategorySet.add(new EventTicketCategory(ticketCategory, event, price, availableTickets, totalTicketsPerCat));
//                System.out.println(eventTicketCategorySet.size());
//                event.setEventTicketCategorySet(eventTicketCategorySet);
//
//                eventRepository.save(event);
//                return eventDTOMapper.apply(event);
//            } else {
//                throw new AlreadyExistsException("Ticket Category already linked to stated event");
//            }
        } else {
            if (categoryOptional.isEmpty()) {
                throw new NonExistentException("Category does not exist");
            } else {
                throw new NonExistentException("Event does not exist");
            }
        }
    }

    public void updateTicketCategory(Integer catId, Integer eventId, Double price, Integer availableTickets, Integer totalTicketsPerCat) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<EventTicketCategory> eventTicketCategoryOptional = eventTicketCategoryRepository.findById(new EventTicketCategoryId(ticketCategory, event));
            if (eventTicketCategoryOptional.isEmpty()) {
                throw new NonExistentException("Ticket Category " + ticketCategory.getCategoryName() + " is not linked to Event '" + event.getEventName() + "'");
            }
            EventTicketCategory eventTicketCategory = eventTicketCategoryOptional.get();

            //adding to total tickets
            Integer currentTotalTickets = event.getTotalTickets();
            currentTotalTickets += totalTicketsPerCat;
            event.setTotalTickets(currentTotalTickets);

            //adding to total tickets sold
            Integer currentTotalTicketsSold = event.getTotalTicketsSold();
            currentTotalTicketsSold += (totalTicketsPerCat - availableTickets);
            event.setTotalTicketsSold(currentTotalTicketsSold);

            if (!event.updateTicketCategory(eventTicketCategory, price, availableTickets, totalTicketsPerCat)) {
                throw new NonExistentException("Event " + event.getEventName() + " is not linked to " + ticketCategory.getCategoryName());
            }
            eventRepository.save(event);
        } else {
            if (categoryOptional.isEmpty()) {
                throw new NonExistentException("Category does not exist");
            } else {
                throw new NonExistentException("Event does not exist");
            }
        }
    }

    public EventDisplayDto removeTicketCategory(Integer catId, Integer eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        Optional<TicketCategory> categoryOptional = ticketCategoryRepository.findById(catId);
        if (categoryOptional.isPresent() && eventOptional.isPresent()) {
            TicketCategory ticketCategory = categoryOptional.get();
            Event event = eventOptional.get();

            Optional<EventTicketCategory> eventTicketCategoryOptional = eventTicketCategoryRepository.findById(new EventTicketCategoryId(ticketCategory, event));
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

    //return artist for a specific event
    public Set<ArtistDisplayDto> findArtistForEvent(Integer eventId) throws NonExistentException {

        if (eventRepository.findById(eventId).isEmpty()) {
            throw new NonExistentException("Event does not exist");
        }
        List<Object[]> artistObject = eventRepository.getArtistByEventId(eventId);
        Set<ArtistDisplayDto> artistDisplayDtoList = artistDtoMapper.mapArtistDisplayDto(artistObject);
        return artistDisplayDtoList;
    }


}
