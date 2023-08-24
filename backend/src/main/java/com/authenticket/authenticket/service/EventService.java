package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.EventModel;
import com.authenticket.authenticket.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventModel> findAllEvent() {
        return eventRepository.findAll();
    }

    public Optional<EventModel> findById(Long event_id) {
        return eventRepository.findById(event_id);
    }

    public EventModel saveEvent(EventModel eventModel) {
        return eventRepository.save(eventModel);
    }

    public EventModel updateEvent(EventModel eventModel) {
        return eventRepository.save(eventModel);
    }

    public String deleteEvent(Long event_id) {
        Optional<EventModel> eventOptional = eventRepository.findById(event_id);

        if (eventOptional.isPresent()) {
            EventModel event = eventOptional.get();
            event.setDeletedAt(LocalDateTime.now());
            eventRepository.save(event);
            return "event deleted successfully";
        }

        return "error: event deleted unsuccessfully";
    }
}
