package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketUpdateDto;
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
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket Not Found");
    }

    @PostMapping
    public ResponseEntity<?> saveTicket(@RequestParam(value = "userId") Long userId,
                                       @RequestParam(value = "eventId") Integer eventId,
                                       @RequestParam(value = "categoryId") Integer categoryId) {
        Ticket savedTicket;
        try {
            User user = userRepository.findById(userId).orElse(null);
            Event event = eventRepository.findById(eventId).orElse(null);
            TicketCategory ticketCategory = ticketCategoryRepository.findById(categoryId).orElse(null);

            Ticket newTicket = new Ticket(null, user, event, ticketCategory);
            savedTicket = ticketService.saveTicket(newTicket);


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving ticket");
        }

        return ResponseEntity.ok(savedTicket);
    }

    @PutMapping
    public ResponseEntity<?> updateTicket(@RequestParam(value = "ticketId") Integer ticketId,
                                          @RequestParam(value = "userId") Long userId,
                                          @RequestParam(value = "eventId") Integer eventId,
                                          @RequestParam(value = "categoryId") Integer categoryId) {
        TicketUpdateDto ticketUpdateDto = new TicketUpdateDto(ticketId, userId, eventId, categoryId);
        Ticket ticket = ticketService.updateTicket(ticketUpdateDto);
        if(ticket!= null){
            return ResponseEntity.ok(ticket);
        }
        return ResponseEntity.badRequest().body("update not successfull");
    }

//    @PutMapping("/{ticketId}")
//    public String deleteTicket(@PathVariable("ticketId") Integer ticketId) {
//        return ticketService.deleteTicket(ticketId);
//    }

    @DeleteMapping("/{ticketId}")
    public String removeTicket(@PathVariable("ticketId") Integer ticketId) {
        return ticketService.removeTicket(ticketId);
    }
}
