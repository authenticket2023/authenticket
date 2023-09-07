package com.authenticket.authenticket.controller;

import com.amazonaws.Response;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketUpdateDto;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.UserRepository;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    private TicketServiceImpl ticketService;

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

    @PostMapping
    public ResponseEntity<?> saveTicket(@RequestParam(value = "userId") Integer userId,
                                       @RequestParam(value = "eventId") Integer eventId,
                                       @RequestParam(value = "categoryId") Integer categoryId) {
        Ticket savedTicket = ticketService.saveTicket(userId, eventId, categoryId);
        return ResponseEntity.ok(savedTicket);
    }

    @PutMapping
    public ResponseEntity<?> updateTicket(@RequestParam(value = "ticketId") Integer ticketId,
                                          @RequestParam(value = "userId") Integer userId) {
        Ticket ticket = ticketService.updateTicket(ticketId, userId);
        return ResponseEntity.ok(ticket);
    }

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
