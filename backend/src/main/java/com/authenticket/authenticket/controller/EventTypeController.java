package com.authenticket.authenticket.controller;


import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.EventTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(
        origins = "http://localhost:3000",
        methods = {RequestMethod.GET, RequestMethod.POST},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/event-type")
public class EventTypeController extends Utility {

    private final EventTypeServiceImpl eventTypeService;

    @Autowired
    public EventTypeController(EventTypeServiceImpl eventTypeService) {
        this.eventTypeService = eventTypeService;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @PostMapping
    public ResponseEntity<?> saveEventType(@RequestParam("typeName") String typeName) {

        EventType eventType = new EventType(null, typeName);

        return ResponseEntity.ok( generateApiResponse(eventTypeService.saveEventType(eventType), "Event Type Created Successfully"));
    }
}
