package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryUpdateDto;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.service.impl.TicketCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/ticket-category")
public class TicketCategoryController {
    @Autowired
    private TicketCategoryServiceImpl ticketCategoryService;

    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping
    public List<TicketCategoryDisplayDto> findAllTicketCategory() {
        return ticketCategoryService.findAllTicketCategory();
    }

    @GetMapping("/{eventId}")
    public List<TicketCategoryDisplayDto> findTicketCategoryByEvent(@PathVariable("eventId") Integer eventId) {
        List<TicketCategoryDisplayDto> ticketCategoryDisplayDtoOptional = ticketCategoryService.findTicketCategoryByEvent(eventId);
        return ticketCategoryDisplayDtoOptional;
//        if(ticketCategoryDisplayDtoOptional.isPresent()){
//            return ResponseEntity.ok(ticketCategoryDisplayDtoOptional.get());
//        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket Category Not Found");
    }

    @PostMapping
    public ResponseEntity<?> saveTicketCategory(@RequestParam(value = "eventId") Integer eventId,
                                        @RequestParam(value = "name") String name,
                                        @RequestParam(value = "price") Double price,
                                        @RequestParam(value = "availableTickets") Integer availableTickets) {
        return ResponseEntity.ok(ticketCategoryService.saveTicketCategory(eventId, name, price, availableTickets));
    }

    @PutMapping
    public ResponseEntity<?> updateTicketCategory(@RequestParam(value = "categoryId") Integer categoryId,
                                                  @RequestParam(value = "eventId") Integer eventId,
                                                  @RequestParam(value = "name") String name,
                                                  @RequestParam(value = "price") Double price,
                                                  @RequestParam(value = "availableTickets") Integer availableTickets) {
        TicketCategory ticketCategory = ticketCategoryService.updateTicketCategory(categoryId, eventId, name, price, availableTickets);

        return ResponseEntity.ok(ticketCategory);
    }

//    @PutMapping("/{ticketId}")
//    public String deleteTicket(@PathVariable("ticketId") Integer ticketId) {
//        return ticketService.deleteTicket(ticketId);
//    }

    @DeleteMapping("/{categoryId}")
    public String removeTicketCategory(@PathVariable("categoryId") Integer categoryId) {
        ticketCategoryService.removeTicketCategory(categoryId);
        return "Ticket Category removed successfully.";
    }
}
