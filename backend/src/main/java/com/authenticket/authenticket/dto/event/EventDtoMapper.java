package com.authenticket.authenticket.dto.event;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDisplayDto;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDtoMapper;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventDtoMapper implements Function<Event, EventDisplayDto> {

    @Autowired
    private EventOrganiserDtoMapper eventOrganiserDtoMapper;

    @Autowired
    private EventTicketCategoryDtoMapper eventTicketCategoryDisplayDtoMapper;

    @Autowired
    private VenueDtoMapper venueDtoMapper;

    @Autowired
    private ArtistDtoMapper artistDtoMapper;

    @Autowired
    private EventRepository eventRepository;

    public EventDisplayDto apply(Event event) {
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
                event.getEventTicketCategorySet());
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
        if (dto.eventLocation() != null) {
            event.setEventLocation(dto.eventLocation());
        }
        if (dto.otherEventInfo() != null) {
            event.setOtherEventInfo(dto.otherEventInfo());
        }
        if (dto.ticketSaleDate() != null) {
            event.setTicketSaleDate(dto.ticketSaleDate());
        }
    }

    public ArtistEventDto applyAssignedEvent(Object[] assignedEvents) {
        return new ArtistEventDto(
                assignedEvents[0],
                assignedEvents[1],
                assignedEvents[2],
                assignedEvents[3],
                assignedEvents[4],
                assignedEvents[5],
                assignedEvents[6],
                assignedEvents[7],
                assignedEvents[8],
                assignedEvents[9],
                assignedEvents[10],
                assignedEvents[11],
                assignedEvents[12],
                assignedEvents[13],
                assignedEvents[14],
                assignedEvents[15],
                assignedEvents[16]
        );
    }

    public List<ArtistEventDto> mapAssignedEvent(List<Object[]> eventObjects) {
        return eventObjects.stream()
                .map(this::applyAssignedEvent)
                .collect(Collectors.toList());
    }

    public OverallEventDto applyOverallEventDto(Event event) {

        Integer eventId = event.getEventId();
        EventOrganiserDisplayDto organiserDisplayDto = eventOrganiserDtoMapper.apply(event.getOrganiser());
//        VenueDisplayDto venueDisplayDto = venueDtoMapper.apply(event.getVenue());
//        String eventTypeName = event.getEventType().getEventTypeName();
        Set<ArtistDisplayDto> artistSet = artistDtoMapper.mapArtistDisplayDto(eventRepository.getArtistByEventId(eventId));
        Set<EventTicketCategoryDisplayDto> eventTicketCategorySet = eventTicketCategoryDisplayDtoMapper.map(event.getEventTicketCategorySet());

        //create and do something similar above for the eventTicketCategory dto

        return new OverallEventDto(
                event.getEventId(),
                event.getEventName(),
                event.getEventDescription(),
                event.getEventDate(),
                event.getOtherEventInfo(),
                event.getEventImage(),
                event.getTotalTickets(),
                event.getTotalTicketsSold(),
                event.getTicketSaleDate(),
                eventTicketCategorySet,
                organiserDisplayDto,
                event.getVenue(),
                artistSet,
                event.getEventType().getEventTypeName()
        );
    }

//    public List<OverallEventDto> mapOverallEventDto(List<Object[]> eventObjects) {
//        return eventObjects.stream()
//                .map(this::applyOverallEventDto)
//                .collect(Collectors.toList());
//    }

}
