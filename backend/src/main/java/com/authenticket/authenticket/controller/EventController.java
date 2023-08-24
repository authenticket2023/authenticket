package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.event.EventDTO;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.service.EventService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/event")

public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping
    public List<EventDTO> findAllEvent() {
        return eventService.findAllEvent();
    }

    @GetMapping("/{event_id}")
    public Optional<EventDTO> findEventById(@PathVariable("event_id") Long event_id) {
        return eventService.findById(event_id);
    }

    @PostMapping
    public Event saveEvent(@RequestBody Event event) {
        return eventService.saveEvent(event);
    }

    @PutMapping
    public Event updateEvent(@RequestBody Event event) {
        return eventService.updateEvent(event);
    }

    @DeleteMapping("/{event_id}")
    public String deleteEvent(@PathVariable("event_id") Long event_id) {
        return eventService.deleteEvent(event_id);
    }

}
