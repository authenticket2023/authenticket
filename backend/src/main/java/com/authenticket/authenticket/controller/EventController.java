package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.model.EventModel;
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
    public List<EventModel> findAllEvent() {
        return eventService.findAllEvent();
    }

    @GetMapping("/{event_id}")
    public Optional<EventModel> findEventById(@PathVariable("event_id") Long event_id) {
        return eventService.findById(event_id);
    }

    @PostMapping
    public EventModel saveEvent(@RequestBody EventModel eventModel) {
        return eventService.saveEvent(eventModel);
    }

    @PutMapping
    public EventModel updateEvent(@RequestBody EventModel eventModel) {
        return eventService.updateEvent(eventModel);
    }

    @DeleteMapping("/{event_id}")
    public String deleteEvent(@PathVariable("event_id") Long event_id) {
        return eventService.deleteEvent(event_id);
    }

}
