package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.SectionRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.VenueRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.SectionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/api/v2/section")
public class SectionController extends Utility {

    private final SectionServiceImpl sectionService;
    private final SectionRepository sectionRepository;
    private final VenueRepository venueRepository;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final EventRepository eventRepository;


    @Autowired
    public SectionController(SectionServiceImpl sectionService,
                             SectionRepository sectionRepository,
                             VenueRepository venueRepository,
                             TicketCategoryRepository ticketCategoryRepository,
                             EventRepository eventRepository) {
        this.sectionService = sectionService;
        this.sectionRepository = sectionRepository;
        this.venueRepository = venueRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @PostMapping
    public ResponseEntity<GeneralApiResponse<Object>> saveSection(@RequestParam(value = "sectionId") String sectionId,
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
        VenueSectionId venueSectionId = new VenueSectionId(venue,sectionId);
        if(sectionRepository.findById(venueSectionId).isPresent()){
            throw new AlreadyExistsException(String.format("Section %s already exists in venue %d", sectionId,venueId));
        }

        Section newSection = new Section(sectionId,venue, ticketCategory, noOfRows, noOfSeatsPerRow);
        Section section = sectionService.saveSection(newSection);

        return ResponseEntity.ok(generateApiResponse(section, "Section Created Successfully"));
    }

    @PostMapping("/ticket-details")
    public ResponseEntity<GeneralApiResponse<Object>> findTicketDetailsBySection(
            @RequestParam(value = "eventId") Integer eventId,
            @RequestParam(value = "sectionId") String sectionId) {

        //get section details
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event ==null) {
            throw new NonExistentException("Event",eventId);
        }
        Venue venue = event.getVenue();
        VenueSectionId venueSectionId = new VenueSectionId(venue,sectionId);
        Section section = sectionRepository.findById(venueSectionId).orElse(null);

        if (section == null) {
            throw new NonExistentException(String.format("Section ID:%s does not exist in venue ID:%d",sectionId,venue.getVenueId()));
        }
        List<SectionTicketDetailsDto> sectionTicketDetailsDto = sectionService.findSectionDetail(event,section);

        return ResponseEntity.ok(generateApiResponse(sectionTicketDetailsDto, "Ticket details for section return successfully"));
    }

    @PostMapping("/seat-matrix")
    public ResponseEntity<GeneralApiResponse<Object>> seatMatrix(
            @RequestParam(value = "eventId") Integer eventId,
                                            @RequestParam(value = "sectionId") String sectionId) {

        //get section details
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event ==null) {
            throw new NonExistentException("Event does not exist");
        }
        Venue venue = event.getVenue();
        VenueSectionId venueSectionId  =new VenueSectionId(venue,sectionId);
        Section section = sectionRepository.findById(venueSectionId).orElse(null);

        if (section == null) {
            throw new NonExistentException(String.format("Section %s does not exist in venue %d",sectionId,venue.getVenueId()));
        }
        sectionService.getCurrentSeatMatrix(event,section);
        return ResponseEntity.ok(generateApiResponse(null, "seat matrix method called"));
    }
}
