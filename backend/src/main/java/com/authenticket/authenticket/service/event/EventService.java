package com.authenticket.authenticket.service.event;

import com.authenticket.authenticket.DTO.event.EventDTO;
import com.authenticket.authenticket.DTO.event.EventDTOMapper;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final EventDTOMapper eventDTOMapper;

    public EventService(EventRepository eventRepository, EventDTOMapper eventDTOMapper) {
        this.eventRepository = eventRepository;
        this.eventDTOMapper = eventDTOMapper;
    }

    public List<EventDTO> findAllEvent() {
        return eventRepository.findAll()
                .stream()
                .map(eventDTOMapper)
                        .collect(Collectors.toList());
    }

    public Optional<EventDTO> findById(Long event_id) {
        return eventRepository.findById(event_id).map(eventDTOMapper);
    }

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Event event) {
        return eventRepository.save(event);
    }

    public String deleteEvent(Long event_id) {
        Optional<Event> eventOptional = eventRepository.findById(event_id);

        if (eventOptional.isPresent()) {
            Event event = eventOptional.get();
            event.setDeletedAt(LocalDateTime.now());
            eventRepository.save(event);
            return "event deleted successfully";
        }

        return "error: event deleted unsuccessfully";
    }
}
