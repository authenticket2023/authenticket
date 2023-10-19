package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.service.impl.TicketCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
@RequestMapping("/api/v2/ticket-category")
public class TicketCategoryController {
    private final TicketCategoryServiceImpl ticketCategoryService;

    @Autowired
    public TicketCategoryController(TicketCategoryServiceImpl ticketCategoryService) {
        this.ticketCategoryService = ticketCategoryService;
    }

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
    }

    @PostMapping
    public ResponseEntity<TicketCategory> saveTicketCategory(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(ticketCategoryService.saveTicketCategory(name));
    }

    @PutMapping
    public ResponseEntity<TicketCategory> updateTicketCategory(@RequestParam(value = "categoryId") Integer categoryId,
                                                  @RequestParam(value = "name") String name) {
        TicketCategory ticketCategory = ticketCategoryService.updateTicketCategory(categoryId, name);

        return ResponseEntity.ok(ticketCategory);
    }
}
