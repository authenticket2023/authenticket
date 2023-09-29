package com.authenticket.authenticket.controller;


import com.authenticket.authenticket.dto.section.SeatAllocationDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.repository.SectionRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.SectionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
@RequestMapping("/api/section")
public class SectionController extends Utility {

    private final SectionServiceImpl sectionService;
    private final SectionRepository sectionRepository;

    private final VenueRepository venueRepository;
    private final TicketCategoryRepository ticketCategoryRepository;


    @Autowired
    public SectionController(SectionServiceImpl sectionService, SectionRepository sectionRepository, VenueRepository venueRepository, TicketCategoryRepository ticketCategoryRepository) {
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
        this.venueRepository = venueRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

//    @GetMapping
//    public ResponseEntity<?> findAllEvents() {
//        List<EventType> eventTypeList = eventTypeService.findAllEventType();
//        if (eventTypeList.isEmpty() || eventTypeList ==null){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body( generateApiResponse(null, "Event Types Not Found"));
//        }
//        return ResponseEntity.ok( generateApiResponse(eventTypeList, "Event Types Returned Successfully"));
//    }

    @PostMapping
    public ResponseEntity<?> saveSection(@RequestParam(value = "svgFile", required = false) MultipartFile svgFile,
                                         @RequestParam(value = "sectionId") Integer sectionId,
                                         @RequestParam(value = "venueId") Integer venueId,
                                         @RequestParam(value = "catId") Integer catId,
                                         @RequestParam(value = "noOfRows") Integer noOfRows,
                                         @RequestParam(value = "noOfSeatsPerRow") Integer noOfSeatsPerRow
    ) {


        Venue venue = venueRepository.findById(venueId).orElse(null);
        TicketCategory ticketCategory = ticketCategoryRepository.findById(catId).orElse(null);
        if (venue == null) {
            throw new NonExistentException("Venue does not exist");
        } else if (ticketCategory == null) {
            throw new NonExistentException("Ticket Category does not exist");
        }
        Section newSection = new Section(sectionId, venue, ticketCategory, noOfRows, noOfSeatsPerRow);
        Section section = sectionService.saveSection(newSection);

        return ResponseEntity.ok(generateApiResponse(section, "Section Created Successfully"));
    }

    @PostMapping("/seat-allocation")
    public ResponseEntity<?> seatAllocation(@RequestParam(value = "eventId") Integer eventId,
                                            @RequestParam(value = "sectionId") Integer sectionId,
                                            @RequestParam(value = "ticketsToPurchase") Integer ticketsToPurchase) {
        List<Ticket> seatAllocationDtoList = sectionService.seatAllocate(eventId, sectionId, ticketsToPurchase);
        return ResponseEntity.ok(generateApiResponse(null, "seatAllocation method called"));
    }
}
