package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.event.EventDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.service.impl.EventServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/event")

public class EventController {
    private final EventServiceImpl eventService;

    public EventController(EventServiceImpl eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping
    public List<EventDto> findAllEvent() {
        return eventService.findAllEvent();
    }

    @GetMapping("/{event_id}")
    public Optional<EventDto> findEventById(@PathVariable("event_id") Long event_id) {
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
