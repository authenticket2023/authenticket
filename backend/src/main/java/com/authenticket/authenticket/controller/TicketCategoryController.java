package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.service.impl.TicketCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/ticket-category")
public class TicketCategoryController {
    @Autowired
    private TicketCategoryServiceImpl ticketCategoryService;

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    @GetMapping
    public List<TicketCategoryDisplayDto> findAllTicketCategory() {
        return ticketCategoryService.findAllTicketCategory();
    }

    @GetMapping("/{categoryId}")
    public Optional<TicketCategoryDisplayDto> findTicketCategoryById(@PathVariable("categoryId") Integer categoryId) {
        return ticketCategoryService.findTicketCategoryById(categoryId);
//        if(ticketCategoryDisplayDtoOptional.isPresent()){
//            return ResponseEntity.ok(ticketCategoryDisplayDtoOptional.get());
//        }
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ticket Category Not Found");
    }

    @PostMapping
    public ResponseEntity<?> saveTicketCategory(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(ticketCategoryService.saveTicketCategory(name));
    }

    @PutMapping
    public ResponseEntity<?> updateTicketCategory(@RequestParam(value = "categoryId") Integer categoryId,
                                                  @RequestParam(value = "name") String name) {
        TicketCategory ticketCategory = ticketCategoryService.updateTicketCategory(categoryId, name);

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
