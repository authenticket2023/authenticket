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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventDtoMapper implements Function<Event, EventDisplayDto> {

    private final EventOrganiserDtoMapper eventOrganiserDtoMapper;

    private final EventTicketCategoryDtoMapper eventTicketCategoryDisplayDtoMapper;

    private final ArtistDtoMapper artistDtoMapper;

    private final AdminDtoMapper adminDtoMapper;

    private final EventRepository eventRepository;

    private final VenueRepository venueRepository;

    private final TicketRepository ticketRepository;


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

    public FeaturedEventDto applyFeaturedEventDto(FeaturedEvent featuredEvent) {
        return new FeaturedEventDto(
                featuredEvent.getFeaturedId(),
                this.applyEventHomeDto(featuredEvent.getEvent()),
                featuredEvent.getStartDate(),
                featuredEvent.getEndDate()
        );

    }

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


    public List<EventDisplayDto> map(List<Event> eventList) {
        return eventList.stream()
                .map(this::apply)
                .collect(Collectors.toList());
    }

    public List<EventHomeDto> mapEventHomeDto(List<Event> eventList) {
        return eventList.stream()
                .map(this::applyEventHomeDto)
                .collect(Collectors.toList());
    }

    public List<FeaturedEventDto> mapFeaturedEventDto(List<FeaturedEvent> featuredEventList) {
        return featuredEventList.stream()
                .map(this::applyFeaturedEventDto)
                .collect(Collectors.toList());
    }

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

    public List<OverallEventDto> mapOverallEventDto(List<Event> eventList) {
        return eventList.stream()
                .map(this::applyOverallEventDto)
                .collect(Collectors.toList());
    }

    public List<EventAdminDisplayDto> mapEventAdminDisplayDto(List<Event> eventList) {
        return eventList.stream()
                .map(this::applyEventAdminDisplayDto)
                .collect(Collectors.toList());
    }


}
