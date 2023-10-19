package com.authenticket.authenticket.controller;


import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.EventTypeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**This is the event type controller class and the base path for this controller's endpoint is api/v2/event-type.*/

@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.POST},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/v2/event-type")
public class EventTypeController extends Utility {

    private final EventTypeServiceImpl eventTypeService;

    @Autowired
    public EventTypeController(EventTypeServiceImpl eventTypeService) {
        this.eventTypeService = eventTypeService;
    }

    /**
     * Test endpoint to check if the controller is working.
     *
     * @return A string indicating a successful test.
     */
    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    /**
     * Retrieve a list of all event types.
     *
     * @return A ResponseEntity containing a GeneralApiResponse with a list of event types if found, or a NOT_FOUND status if no event types are found.
     */
    @GetMapping
    public ResponseEntity<GeneralApiResponse<Object>> findAllEventTypes() {
        List<EventType> eventTypeList = eventTypeService.findAllEventType();
        if (eventTypeList == null || eventTypeList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "Event Types Not Found"));
        }
        return ResponseEntity.ok(generateApiResponse(eventTypeList, "Event Types Returned Successfully"));
    }

    /**
     * Create a new event type with the specified type name.
     *
     * @param typeName The name of the new event type.
     * @return A ResponseEntity containing a GeneralApiResponse with the newly created event type and a success message.
     */
    @PostMapping
    public ResponseEntity<GeneralApiResponse<Object>> saveEventType(@RequestParam("typeName") String typeName) {
        EventType eventType = new EventType(null, typeName);
        return ResponseEntity.ok(generateApiResponse(eventTypeService.saveEventType(eventType), "Event Type Created Successfully"));
    }
}