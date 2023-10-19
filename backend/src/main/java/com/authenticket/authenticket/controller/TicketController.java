package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;


@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/v2/ticket")
public class TicketController extends Utility {

    private final TicketServiceImpl ticketService;
    private final TicketCategoryRepository ticketCategoryRepository;
    private final TicketPricingRepository ticketPricingRepository;
    private final SectionRepository sectionRepository;
    private final EventRepository eventRepository;
    private final OrderRepository orderRepository;

    @Autowired
    public TicketController(TicketServiceImpl ticketService,
                            TicketCategoryRepository ticketCategoryRepository,
                            EventRepository eventRepository,
                            TicketPricingRepository ticketPricingRepository,
                            SectionRepository sectionRepository,
                            OrderRepository orderRepository) {
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.eventRepository = eventRepository;
        this.ticketPricingRepository = ticketPricingRepository;
        this.ticketService = ticketService;
        this.sectionRepository = sectionRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping
    public List<TicketDisplayDto> findAllTicket() {
        return ticketService.findAllTicket();
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<GeneralApiResponse<Object>> findTicketById(@PathVariable("ticketId") Integer ticketId) {
        return ResponseEntity.ok(generateApiResponse(ticketService.findTicketById(ticketId), "Tickets returned successfully"));
    }

    @PostMapping("/allocate-seats")
    public ResponseEntity<GeneralApiResponse<Object>> allocateSeats(@RequestParam(value = "eventId") Integer eventId,
                                           @RequestParam(value = "sectionId") String sectionId,
                                           @RequestParam(value = "ticketsToPurchase") Integer ticketsToPurchase) {
        List<Ticket> ticketList;
        try{
            ticketList = ticketService.allocateSeats(eventId, sectionId, ticketsToPurchase);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }
        return ResponseEntity.ok(generateApiResponse(ticketList, String.format("%d seats successfully assigned", ticketsToPurchase)));
    }

    @PostMapping
    public ResponseEntity<GeneralApiResponse<Object>> saveTicket(@RequestParam(value = "eventId") Integer eventId,
                                        @RequestParam(value = "categoryId") Integer categoryId,
                                        @RequestParam(value = "sectionId") String sectionId,
                                        @RequestParam(value = "rowNo") Integer rowNo,
                                        @RequestParam(value = "seatNo") Integer seatNo,
                                        @RequestParam(value = "ticketHolder", required = false) String ticketHolder,
                                        @RequestParam(value = "orderId", required = false) Integer orderId) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = optionalEvent.get();
        Venue venue = event.getVenue();

        Optional<TicketCategory> optionalTicketCategory = ticketCategoryRepository.findById(categoryId);
        if (optionalTicketCategory.isEmpty()) {
            throw new NonExistentException("Ticket Category", categoryId);
        }
        TicketCategory ticketCategory = optionalTicketCategory.get();

        VenueSectionId venueSectionId = new VenueSectionId(venue,sectionId);

        Optional<Section> optionalSection = sectionRepository.findById(venueSectionId);
        if (optionalSection.isEmpty()) {
            throw new NonExistentException("Section", sectionId);
        }
        Section section = optionalSection.get();

        EventTicketCategoryId id = new EventTicketCategoryId(ticketCategory, event);
        Optional<TicketPricing> optionalTicketPricing = ticketPricingRepository.findById(id);
        if (optionalTicketPricing.isEmpty()) {
            throw new NonExistentException("Ticket Pricing", id);
        }
        TicketPricing ticketPricing = optionalTicketPricing.get();


        Order order = null;
        if(orderId != null){
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isPresent()) {
                order = optionalOrder.get();
            }
        }

        Ticket ticket = new Ticket(null, ticketPricing, section, rowNo, seatNo, ticketHolder, order);

        Ticket savedTicket = ticketService.saveTicket(ticket);
        return ResponseEntity.ok(generateApiResponse(savedTicket, "Tickets created successfully"));
    }
}
