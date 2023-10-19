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

/**
 * The `TicketController` class handles HTTP requests related to ticket management and allocation.
 */
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

    /**
     * A test endpoint to check if the controller is operational.
     *
     * @return A simple test message indicating the operation was successful.
     */
    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    /**
     * Retrieve a list of all tickets in the system.
     *
     * @return A list of ticket information as `TicketDisplayDto`.
     */
    @GetMapping
    public List<TicketDisplayDto> findAllTicket() {
        return ticketService.findAllTicket();
    }

    /**
     * Retrieve a list of all tickets in the system.
     *
     * @return A list of ticket information as `TicketDisplayDto`.
     */
    @GetMapping("/{ticketId}")
    public ResponseEntity<GeneralApiResponse<Object>> findTicketById(@PathVariable("ticketId") Integer ticketId) {
        return ResponseEntity.ok(generateApiResponse(ticketService.findTicketById(ticketId), "Tickets returned successfully"));
    }

    /**
     * Allocate seats for an event within a specific section.
     *
     * @param eventId           The ID of the event for seat allocation.
     * @param sectionId         The ID of the section for seat allocation.
     * @param ticketsToPurchase The number of tickets to allocate.
     * @return A response indicating the successful allocation of seats.
     */
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

    /**
     * Save a new ticket with optional details.
     *
     * @param eventId     The ID of the event for the ticket.
     * @param categoryId  The ID of the ticket category.
     * @param sectionId   The ID of the section for the ticket.
     * @param rowNo       The row number for the ticket (optional).
     * @param seatNo      The seat number for the ticket (optional).
     * @param ticketHolder The name of the ticket holder (optional).
     * @param orderId     The ID of the order associated with the ticket (optional).
     * @return A response indicating the successful creation of the ticket.
     */
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
