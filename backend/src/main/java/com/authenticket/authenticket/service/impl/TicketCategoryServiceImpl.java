package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDtoMapper;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.AlreadyExistsException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.service.TicketCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class provides the implementation of the `TicketCategoryService` interface, which manages ticket categories.
 */

@Service
public class TicketCategoryServiceImpl implements TicketCategoryService {

    private final TicketCategoryRepository ticketCategoryRepository;

    private final TicketCategoryDisplayDtoMapper ticketCategoryDisplayDtoMapper;

    @Autowired
    public TicketCategoryServiceImpl(TicketCategoryRepository ticketCategoryRepository,
                                     TicketCategoryDisplayDtoMapper ticketCategoryDisplayDtoMapper) {
        this.ticketCategoryRepository = ticketCategoryRepository;
        this.ticketCategoryDisplayDtoMapper = ticketCategoryDisplayDtoMapper;
    }

    /**
     * Retrieve a list of all ticket categories.
     *
     * @return A list of `TicketCategoryDisplayDto` objects representing all ticket categories.
     */
    @Override
    public List<TicketCategoryDisplayDto> findAllTicketCategory() {
        return ticketCategoryRepository.findAll()
                .stream()
                .map(ticketCategoryDisplayDtoMapper)
                .collect(Collectors.toList());
    }

    /**
     * Find a ticket category by its unique identifier (ID).
     *
     * @param categoryId The unique identifier of the ticket category.
     * @return An `Optional` containing a `TicketCategoryDisplayDto` if found, or empty if the ticket category does not exist.
     */
    @Override
    public Optional<TicketCategoryDisplayDto> findTicketCategoryById(Integer categoryId) {
        return ticketCategoryRepository.findById(categoryId).map(ticketCategoryDisplayDtoMapper);
    }

    /**
     * Create and save a new ticket category with the given name.
     *
     * @param name The name of the new ticket category.
     * @return The saved `TicketCategory` entity.
     * @throws AlreadyExistsException If a ticket category with the same name already exists.
     */
    @Override
    public TicketCategory saveTicketCategory(String name) {
        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findByCategoryName(name);
        if (ticketCategoryOptional.isPresent()) {
            throw new AlreadyExistsException("Ticket Category with name '" + name + "' already exists");
        }
        TicketCategory ticketCategory = new TicketCategory(null, name);
        return ticketCategoryRepository.save(ticketCategory);
    }

    /**
     * Update an existing ticket category with a new name.
     *
     * @param categoryId The unique identifier of the ticket category to update.
     * @param name      The new name for the ticket category.
     * @return The updated `TicketCategory` entity.
     * @throws AlreadyExistsException If a ticket category with the new name already exists.
     * @throws NonExistentException  If the ticket category to update does not exist.
     */
    @Override
    public TicketCategory updateTicketCategory(Integer categoryId, String name) {
        TicketCategoryUpdateDto ticketCategoryUpdateDto = new TicketCategoryUpdateDto(categoryId, name);

        if (ticketCategoryRepository.findByCategoryName(name).isPresent()) {
            throw new AlreadyExistsException("Ticket Category with name '" + name + "' already exists");
        }

        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findById(ticketCategoryUpdateDto.categoryId());

        if (ticketCategoryOptional.isPresent()) {
            TicketCategory existingTicketCategory = ticketCategoryOptional.get();
            ticketCategoryDisplayDtoMapper.update(ticketCategoryUpdateDto, existingTicketCategory);
            ticketCategoryRepository.save(existingTicketCategory);
            return existingTicketCategory;
        }

        throw new NonExistentException("Ticket Category not found");
    }

    /**
     * Delete a ticket category by marking it as deleted.
     *
     * @param categoryId The unique identifier of the ticket category to delete.
     * @throws AlreadyDeletedException If the ticket category is already deleted.
     * @throws NonExistentException   If the ticket category does not exist.
     */
    @Override
    public void deleteTicket(Integer categoryId) {
        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findById(categoryId);

        if (ticketCategoryOptional.isPresent()) {
            TicketCategory ticketCategory = ticketCategoryOptional.get();
            if(ticketCategory.getDeletedAt()!=null){
                throw new AlreadyDeletedException("Ticket Category already deleted");
            }

            ticketCategory.setDeletedAt(LocalDateTime.now());
            ticketCategoryRepository.save(ticketCategory);
        } else {
            throw new NonExistentException("Ticket Category does not exist");
        }
    }

    /**
     * Remove a ticket category permanently by its unique identifier (ID).
     *
     * @param categoryId The unique identifier of the ticket category to remove.
     * @throws NonExistentException If the ticket category does not exist.
     */
    @Override
    public void removeTicketCategory(Integer categoryId) {
        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findById(categoryId);

        if (ticketCategoryOptional.isPresent()) {
            ticketCategoryRepository.deleteById(categoryId);
        } else {
            throw new NonExistentException("Ticket Category does not exist");
        }
    }
}
