package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
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
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/ticket")
public class TicketController {
    private final TicketServiceImpl ticketService;
    @Autowired
    public TicketController(TicketServiceImpl ticketService) {
        this.ticketService = ticketService;
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
    public ResponseEntity<?> findTicketById(@PathVariable("ticketId") Integer ticketId) {
        return ResponseEntity.ok(ticketService.findTicketById(ticketId));
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

    @PutMapping("/{ticketId}")
    public String deleteTicket(@PathVariable("ticketId") Integer ticketId) {
        ticketService.deleteTicket(ticketId);
        return "Ticket deleted successfully";
    }

    @DeleteMapping("/{ticketId}")
    public String removeTicket(@PathVariable("ticketId") Integer ticketId) {
        ticketService.removeTicket(ticketId);
        return "Ticket removed successfully.";
    }
}
