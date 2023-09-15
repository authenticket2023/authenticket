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

    public List<TicketCategoryDisplayDto> findAllTicketCategory() {
        return ticketCategoryRepository.findAll()
                .stream()
                .map(ticketCategoryDisplayDtoMapper)
                .collect(Collectors.toList());
    }

    public Optional<TicketCategoryDisplayDto> findTicketCategoryById(Integer categoryId) {
        return ticketCategoryRepository.findById(categoryId).map(ticketCategoryDisplayDtoMapper);
    }

    public TicketCategory saveTicketCategory(String name) {
        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findByCategoryName(name);
        if (ticketCategoryOptional.isPresent()) {
            throw new AlreadyExistsException("Ticket Category with name '" + name + "' already exists");
        }
        TicketCategory ticketCategory = new TicketCategory(null, name);
        return ticketCategoryRepository.save(ticketCategory);
    }

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

    public void removeTicketCategory(Integer categoryId) {
        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findById(categoryId);

        if (ticketCategoryOptional.isPresent()) {
            ticketCategoryRepository.deleteById(categoryId);
        } else {
            throw new NonExistentException("Ticket Category does not exist");
        }
    }
}
