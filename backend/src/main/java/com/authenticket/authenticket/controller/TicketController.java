package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.*;
import javax.swing.text.html.Option;
import java.time.LocalDateTime;
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
@RequestMapping("/api/ticket")
public class TicketController extends Utility {
    private final TicketRepository ticketRepository;

    private final TicketServiceImpl ticketService;

    private final TicketCategoryRepository ticketCategoryRepository;

    private final TicketPricingRepository ticketPricingRepository;

    private final SectionRepository sectionRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    @Autowired
    public TicketController(TicketRepository ticketRepository,
                            TicketServiceImpl ticketService,
                            TicketCategoryRepository ticketCategoryRepository,
                            UserRepository userRepository,
                            EventRepository eventRepository,
                            TicketPricingRepository ticketPricingRepository,
                            SectionRepository sectionRepository) {
        this.ticketRepository = ticketRepository;
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketPricingRepository = ticketPricingRepository;
        this.ticketService = ticketService;
        this.sectionRepository = sectionRepository;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @PostMapping("/test-post")
    public String testPost(@RequestParam(value = "eventId") Integer eventId,
                           @RequestParam(value = "sectionId") Integer sectionId) {
        return ticketRepository.findNoOfAvailableTicketsBySectionAndEvent(eventId,sectionId).toString();
    }

    @GetMapping
    public List<TicketDisplayDto> findAllTicket() {
        return ticketService.findAllTicket();
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<?> findTicketById(@PathVariable("ticketId") Integer ticketId) {
        return ResponseEntity.ok(ticketService.findTicketById(ticketId));
    }

    @PostMapping("/allocate-seats")
    public ResponseEntity<?> allocateSeats(@RequestParam(value = "eventId") Integer eventId,
                                           @RequestParam(value = "sectionId") Integer sectionId,
                                           @RequestParam(value = "ticketsToPurchase") Integer ticketsToPurchase) {
        List<Ticket> ticketList;
        try{
            ticketList = ticketService.allocateSeats(eventId, sectionId, ticketsToPurchase);
        } catch(Exception e){
            return ResponseEntity.badRequest().body(generateApiResponse(null, e.getMessage()));
        }
        return ResponseEntity.ok(generateApiResponse(ticketList, String.format("%d seats successfully assigned", ticketsToPurchase)));
    }

    @DeleteMapping("/unallocate-seats")
    public ResponseEntity<?> removeTickets(@RequestParam(value = "ticketIdString")String ticketIdString
    ) {
        List<Integer> ticketIdList = Arrays.stream(ticketIdString.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());


            ticketService.removeAllTickets(ticketIdList);

        return ResponseEntity.ok(generateApiResponse(null, "Tickets Unallocated Successfully"));
    }

//    @PostMapping
//    public ResponseEntity<?> saveTicket(@RequestParam(value = "userId") Integer userId,
//                                       @RequestParam(value = "eventId") Integer eventId,
//                                       @RequestParam(value = "categoryId") Integer categoryId) {
//        Ticket savedTicket = ticketService.saveTicket(userId, eventId, categoryId);
//        return ResponseEntity.ok(savedTicket);
//    }
//
    @PostMapping
    public ResponseEntity<?> saveTicket(@RequestParam(value = "eventId") Integer eventId,
                                       @RequestParam(value = "categoryId") Integer categoryId,
                                       @RequestParam(value = "sectionId") Integer sectionId,
                                       @RequestParam(value = "rowNo") Integer rowNo,
                                       @RequestParam(value = "seatNo") Integer seatNo,
                                       @RequestParam(value = "ticketHolder") String ticketHolder) {
        Optional<Event> optionalEvent = eventRepository.findById(eventId);
        if (optionalEvent.isEmpty()) {
            throw new NonExistentException("Event", eventId);
        }
        Event event = optionalEvent.get();

        Optional<TicketCategory> optionalTicketCategory = ticketCategoryRepository.findById(categoryId);
        if (optionalTicketCategory.isEmpty()) {
            throw new NonExistentException("Ticket Category", categoryId);
        }
        TicketCategory ticketCategory = optionalTicketCategory.get();

        Optional<Section> optionalSection = sectionRepository.findById(sectionId);
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

        Ticket ticket = new Ticket(null, ticketPricing, section, rowNo, seatNo, ticketHolder,null);

        Ticket savedTicket = ticketService.saveTicket(ticket);
        return ResponseEntity.ok(savedTicket);
    }

//    @PutMapping
//    public ResponseEntity<?> updateTicket(@RequestParam(value = "ticketId") Integer ticketId,
//                                          @RequestParam(value = "ticketHolder") String ticketHolder) {
//        //DK
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
//        if (ticketOptional.isEmpty()) {
//            throw new NonExistentException("Ticket", ticketId);
//        }
//        Ticket ticket = ticketOptional.get();
//        Ticket savedTicket = ticketService.updateTicket(ticket);
//        return ResponseEntity.ok(savedTicket);
//    }

//    @PutMapping("/{ticketId}")
//    public String deleteTicket(@PathVariable("ticketId") Integer ticketId) {
//        ticketService.deleteTicket(ticketId);
//        return "Ticket deleted successfully";
//    }
//
//    @DeleteMapping("/{ticketId}")
//    public String removeTicket(@PathVariable("ticketId") Integer ticketId) {
//        ticketService.removeTicket(ticketId);
//        return "Ticket removed successfully.";
//    }
}
