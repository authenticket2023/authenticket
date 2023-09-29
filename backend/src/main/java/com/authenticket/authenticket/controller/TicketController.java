package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;

import java.util.Arrays;
import java.util.List;
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
    private final TicketServiceImpl ticketService;
    private final TicketRepository ticketRepository;
    @Autowired
    public TicketController(TicketServiceImpl ticketService, TicketRepository ticketRepository) {
        this.ticketService = ticketService;
    this.ticketRepository = ticketRepository;
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
//    @PutMapping
//    public ResponseEntity<?> updateTicket(@RequestParam(value = "ticketId") Integer ticketId,
//                                          @RequestParam(value = "userId") Integer userId) {
//        Ticket ticket = ticketService.updateTicket(ticketId, userId);
//        return ResponseEntity.ok(ticket);
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
