package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDtoMapper;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.FeaturedEvent;
import com.authenticket.authenticket.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service class responsible for mapping Event entities to various DTOs.
 * {@link EventDisplayDto} DTOs and performing updates on event entities.
 */
@Service
public class EventDtoMapper implements Function<Event, EventDisplayDto> {

    private final EventOrganiserDtoMapper eventOrganiserDtoMapper;

    private final EventTicketCategoryDtoMapper eventTicketCategoryDisplayDtoMapper;

    private final ArtistDtoMapper artistDtoMapper;

    private final AdminDtoMapper adminDtoMapper;

    private final EventRepository eventRepository;

    private final VenueRepository venueRepository;

    private final TicketRepository ticketRepository;


    /**
     * Constructs a new EventDtoMapper with the specified dependencies.
     *
     * @param eventOrganiserDtoMapper              The mapper for converting EventOrganiser entities to DTOs.
     * @param eventTicketCategoryDisplayDtoMapper  The mapper for converting EventTicketCategory entities to DTOs.
     * @param venueDtoMapper                       The mapper for converting Venue entities to DTOs.
     * @param artistDtoMapper                      The mapper for converting Artist entities to DTOs.
     * @param adminDtoMapper                       The mapper for converting Admin entities to DTOs.
     * @param eventRepository                      The repository for Event entities.
     * @param venueRepository                      The repository for Venue entities.
     * @param ticketRepository                     The repository for Ticket entities.
     */
    @Autowired
    public EventDtoMapper(EventOrganiserDtoMapper eventOrganiserDtoMapper,
                          EventTicketCategoryDtoMapper eventTicketCategoryDisplayDtoMapper,
                          VenueDtoMapper venueDtoMapper, ArtistDtoMapper artistDtoMapper,
                          AdminDtoMapper adminDtoMapper, EventRepository eventRepository,
                          VenueRepository venueRepository, TicketRepository ticketRepository) {
        this.eventOrganiserDtoMapper = eventOrganiserDtoMapper;
        this.eventTicketCategoryDisplayDtoMapper = eventTicketCategoryDisplayDtoMapper;
        this.artistDtoMapper = artistDtoMapper;
        this.adminDtoMapper = adminDtoMapper;
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.ticketRepository = ticketRepository;
    }
    /**
     * Maps an Event entity to an EventDisplayDto.
     *
     * @param event The Event entity to be mapped.
     * @return The corresponding EventDisplayDto.
     */
    public EventDisplayDto apply(Event event) {

        Set<EventTicketCategoryDisplayDto> eventTicketCategorySet = new HashSet<>();
        if(event.getTicketPricingSet() != null){
            eventTicketCategorySet = eventTicketCategoryDisplayDtoMapper.map(event.getTicketPricingSet());

        }
        return new EventDisplayDto(
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventDate(),
                event.getOtherEventInfo(),
                event.getTicketSaleDate(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                event.getArtists(),
                eventTicketCategorySet);
    }

    /**
     * Maps an Event entity to an EventHomeDto.
     *
     * @param event The Event entity to be mapped.
     * @return The corresponding EventHomeDto.
     */
    public EventHomeDto applyEventHomeDto(Event event) {
        return new EventHomeDto(
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventImage(),
                event.getEventType().getEventTypeName(),
                event.getEventDate(),
                venueRepository.findNoOfSeatsByVenue(event.getVenue().getVenueId()),
                event.getVenue().getVenueName());
    }

    /**
     * Maps an Object array to an EventHomeDto.
     *
     * @param queryObj The Object array to be mapped.
     * @return The corresponding EventHomeDto.
     */
    public EventHomeDto applyEventHomeDtoForObj(Object[] queryObj) {
        return new EventHomeDto(
                (Integer) queryObj[0],
                (String) queryObj[1],
                (String)queryObj[2],
                (String)queryObj[3],
                (String)queryObj[4],
                ((Timestamp)queryObj[5]).toLocalDateTime(),
                venueRepository.findNoOfSeatsByVenue((Integer)queryObj[6]),
                (String)queryObj[7]);
    }

    /**
     * Maps a FeaturedEvent entity to a FeaturedEventDto.
     *
     * @param featuredEvent The FeaturedEvent entity to be mapped.
     * @return The corresponding FeaturedEventDto.
     */
    public FeaturedEventDto applyFeaturedEventDto(FeaturedEvent featuredEvent) {
        return new FeaturedEventDto(
                featuredEvent.getFeaturedId(),
                this.applyEventHomeDto(featuredEvent.getEvent()),
                featuredEvent.getStartDate(),
                featuredEvent.getEndDate()
        );

    }

    /**
     * Maps an Event entity to an EventAdminDisplayDto.
     *
     * @param event The Event entity to be mapped.
     * @return The corresponding EventAdminDisplayDto.
     */
    public EventAdminDisplayDto applyEventAdminDisplayDto(Event event) {
        String organiserEmail = null;
        if(event.getOrganiser() != null){
            organiserEmail = event.getOrganiser().getEmail();
        }

        String reviewedBy = null;
        if(event.getReviewedBy()!=null ){
            reviewedBy = event.getReviewedBy().getEmail();
        }
        return new EventAdminDisplayDto(
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventDate(),
                event.getTicketSaleDate(),
                organiserEmail,
                event.getReviewRemarks(),
                event.getReviewStatus(),
                reviewedBy,
                event.getDeletedAt()
        );

    }

    /**
     * Maps a list of Event entities to a list of EventHomeDto.
     *
     * @param eventList The list of Event entities to be mapped.
     * @return The corresponding list of EventHomeDto.
     */
    public List<EventDisplayDto> map(List<Event> eventList) {
        return eventList.stream()
                .map(this::apply)
                .collect(Collectors.toList());
    }

    /**
     * Maps a list of Event entities to a list of EventHomeDto.
     *
     * @param eventList The list of Event entities to be mapped.
     * @return The corresponding list of EventHomeDto.
     */
    public List<EventHomeDto> mapEventHomeDto(List<Event> eventList) {
        return eventList.stream()
                .map(this::applyEventHomeDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps a list of Object arrays to a list of EventHomeDto.
     *
     * @param objList The list of Object arrays to be mapped.
     * @return The corresponding list of EventHomeDto.
     */
    public List<EventHomeDto> mapEventHomeDtoForObj(List<Object[]> objList) {
        return objList.stream()
                .map(this::applyEventHomeDtoForObj)
                .collect(Collectors.toList());
    }

    /**
     * Maps a list of FeaturedEvent entities to a list of FeaturedEventDto.
     *
     * @param featuredEventList The list of FeaturedEvent entities to be mapped.
     * @return The corresponding list of FeaturedEventDto.
     */
    public List<FeaturedEventDto> mapFeaturedEventDto(List<FeaturedEvent> featuredEventList) {
        return featuredEventList.stream()
                .map(this::applyFeaturedEventDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an Event entity with values from an EventUpdateDto.
     *
     * @param dto   The EventUpdateDto containing updated values.
     * @param event The Event entity to be updated.
     */
    public void update(EventUpdateDto dto, Event event) {
        if (dto.eventName() != null) {
            event.setEventName(dto.eventName());
        }
        if (dto.eventDescription() != null) {
            event.setEventDescription(dto.eventDescription());
        }
        if (dto.eventDate() != null) {
            event.setEventDate(dto.eventDate());
        }
        if (dto.otherEventInfo() != null) {
            event.setOtherEventInfo(dto.otherEventInfo());
        }
        if (dto.ticketSaleDate() != null) {
            event.setTicketSaleDate(dto.ticketSaleDate());
        }
        if (dto.eventType() != null) {
            event.setEventType(dto.eventType());
        }
        if (dto.venue() != null) {
            event.setVenue(dto.venue());
        }
        if (dto.reviewStatus() != null) {
            event.setReviewStatus(dto.reviewStatus());
        }
        if (dto.reviewRemarks() != null) {
            event.setReviewRemarks(dto.reviewRemarks());
        }
        if (dto.reviewStatus() != null) {
            event.setReviewedBy(dto.reviewedBy());
        }
    }

    /**
     * Maps an Event entity to an OverallEventDto.
     *
     * @param event The Event entity to be mapped.
     * @return The corresponding OverallEventDto.
     */
    public OverallEventDto applyOverallEventDto(Event event) {

        Integer eventId = event.getEventId();
        EventOrganiserDisplayDto organiserDisplayDto = eventOrganiserDtoMapper.apply(event.getOrganiser());

        Set<ArtistDisplayDto> artistSet = new HashSet<>();
        if(eventRepository.getArtistByEventId(eventId)!=null){
            artistSet = artistDtoMapper.mapArtistDisplayDto(eventRepository.getArtistByEventId(eventId));
        }
        //map event ticket cat to dto
        Set<EventTicketCategoryDisplayDto> eventTicketCategorySet = new HashSet<>();
        if(event.getTicketPricingSet() != null){
            eventTicketCategorySet = eventTicketCategoryDisplayDtoMapper.map(event.getTicketPricingSet());
        }
        //convert admin to dto
        AdminDisplayDto adminDisplayDto = null;
        if(event.getReviewedBy() != null) {
            adminDisplayDto = adminDtoMapper.apply(event.getReviewedBy());
        }
        //create and do something similar above for the eventTicketCategory dto
        return new OverallEventDto(
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventDate(),
                event.getOtherEventInfo(),
                event.getEventImage(),
                venueRepository.findNoOfSeatsByVenue(event.getVenue().getVenueId()),
                ticketRepository.countAllByTicketPricingEventEventId(eventId),
                event.getTicketSaleDate(),
                event.getReviewStatus(),
                event.getReviewRemarks(),
                adminDisplayDto, //reviewBy
                event.getIsEnhanced(),
                event.getHasPresale(),
                event.getHasPresaleUsers(),
                eventTicketCategorySet, //ticket category set
                organiserDisplayDto, //organiser
                event.getVenue(), //venue
                artistSet, //artist set
                event.getEventType().getEventTypeName() //event type
        );
    }

    /**
     * Maps a list of Event entities to a list of OverallEventDto.
     *
     * @param eventList The list of Event entities to be mapped.
     * @return The corresponding list of OverallEventDto.
     */
    public List<OverallEventDto> mapOverallEventDto(List<Event> eventList) {
        return eventList.stream()
                .map(this::applyOverallEventDto)
                .collect(Collectors.toList());
    }

    /**
     * Maps a list of Event entities to a list of EventAdminDisplayDto.
     *
     * @param eventList The list of Event entities to be mapped.
     * @return The corresponding list of EventAdminDisplayDto.
     */
    public List<EventAdminDisplayDto> mapEventAdminDisplayDto(List<Event> eventList) {
        return eventList.stream()
                .map(this::applyEventAdminDisplayDto)
                .collect(Collectors.toList());
    }


}
