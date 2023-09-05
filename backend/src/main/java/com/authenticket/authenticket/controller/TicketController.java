package com.authenticket.authenticket.controller;

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
@CrossOrigin
@RequestMapping("/api/ticket")
public class TicketController {
    @Autowired
    private TicketServiceImpl ticketService;

    @Autowired
    private TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping
    public List<TicketDisplayDto> findAllTicket() {
        return ticketService.findAllTicket();
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<?> findTicketById(@PathVariable("ticketId") Integer eventId) {
        Optional<TicketDisplayDto> ticketDisplayDtoOptional = ticketService.findTicketById(eventId);
        if(ticketDisplayDtoOptional.isPresent()){
            return ResponseEntity.ok(ticketDisplayDtoOptional.get());
        }

        throw new ApiRequestException("Ticket ID not found");
    }

    @PostMapping
    public ResponseEntity<?> saveTicket(@RequestParam(value = "userId") Integer userId,
                                       @RequestParam(value = "eventId") Integer eventId,
                                       @RequestParam(value = "categoryId") Integer categoryId) {

        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("Error Saving Ticket: User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ApiRequestException("Error Saving Ticket: Event not found"));
        TicketCategory ticketCategory = ticketCategoryRepository.findById(categoryId).orElseThrow(() -> new ApiRequestException("Error Saving Ticket: TicketCategory not found"));

        Ticket savedTicket = ticketService.saveTicket(new Ticket(null, user, event, ticketCategory));

        return ResponseEntity.ok(savedTicket);
    }

    @PutMapping
    public ResponseEntity<?> updateTicket(@RequestParam(value = "ticketId") Integer ticketId,
                                          @RequestParam(value = "userId") Integer userId,
                                          @RequestParam(value = "eventId") Integer eventId,
                                          @RequestParam(value = "categoryId") Integer categoryId) {
        TicketUpdateDto ticketUpdateDto = new TicketUpdateDto(ticketId, userId, eventId, categoryId);
        Ticket ticket = ticketService.updateTicket(ticketUpdateDto);
        if(ticket!= null){
            return ResponseEntity.ok(ticket);
        }

        throw new ApiRequestException("Ticket ID not found");
    }

//    @PutMapping("/{ticketId}")
//    public String deleteTicket(@PathVariable("ticketId") Integer ticketId) {
//        return ticketService.deleteTicket(ticketId);
//    }

    @DeleteMapping("/{ticketId}")
    public String removeTicket(@PathVariable("ticketId") Integer ticketId) {
        if (ticketService.removeTicket(ticketId)) {
            return "Ticket removed successfully.";
        }
        throw new ApiRequestException("Failed to remove ticket");
    }
}
