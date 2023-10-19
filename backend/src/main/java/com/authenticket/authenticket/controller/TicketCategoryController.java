package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.service.impl.TicketCategoryServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**This is the ticket category controller class and the base path for this controller's endpoint is api/v2/ticket-category.*/
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

    /**
     * Retrieves a list of all ticket categories with display details.
     *
     * @return A list of TicketCategoryDisplayDto objects containing information about each ticket category.
     */
    @GetMapping
    public List<TicketCategoryDisplayDto> findAllTicketCategory() {
        return ticketCategoryService.findAllTicketCategory();
    }

    /**
     * Retrieves a ticket category by its unique identifier.
     *
     * @param categoryId The unique identifier of the ticket category.
     * @return An Optional containing a TicketCategoryDisplayDto if found, or an empty Optional if not found.
     */
    @GetMapping("/{categoryId}")
    public Optional<TicketCategoryDisplayDto> findTicketCategoryById(@PathVariable("categoryId") Integer categoryId) {
        return ticketCategoryService.findTicketCategoryById(categoryId);
    }

    /**
     * Creates a new ticket category with the provided name.
     *
     * @param name The name of the new ticket category.
     * @return A ResponseEntity containing the newly created TicketCategory.
     */
    @PostMapping
    public ResponseEntity<TicketCategory> saveTicketCategory(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(ticketCategoryService.saveTicketCategory(name));
    }

    /**
     * Updates an existing ticket category with the provided ID and name.
     *
     * @param categoryId The unique identifier of the ticket category to update.
     * @param name      The new name for the ticket category.
     * @return A ResponseEntity containing the updated TicketCategory.
     */
    @PutMapping
    public ResponseEntity<TicketCategory> updateTicketCategory(@RequestParam(value = "categoryId") Integer categoryId,
                                                  @RequestParam(value = "name") String name) {
        TicketCategory ticketCategory = ticketCategoryService.updateTicketCategory(categoryId, name);

        return ResponseEntity.ok(ticketCategory);
    }
}
