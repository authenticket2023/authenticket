package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDtoMapper;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryUpdateDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryUpdateDtoMapper;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketCategoryServiceImpl {
    @Autowired
    private TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    private TicketCategoryDisplayDtoMapper ticketCategoryDisplayDtoMapper;

    @Autowired
    private EventRepository eventRepository;

    public List<TicketCategoryDisplayDto> findAllTicketCategory() {
        return ticketCategoryRepository.findAll()
                .stream()
                .map(ticketCategoryDisplayDtoMapper)
                .collect(Collectors.toList());
    }

    public Optional<TicketCategoryDisplayDto> findTicketCategoryById(Integer categoryId) {
        return ticketCategoryRepository.findById(categoryId).map(ticketCategoryDisplayDtoMapper);
    }

    public List<TicketCategoryDisplayDto> findTicketCategoryByEvent(Integer eventId) {
        Event event = eventRepository.findById(eventId).orElse(null);
        if (event == null) {
            return Collections.emptyList(); // Return an empty list if the event is not found
        }
        return ticketCategoryRepository.findByEvent(event)
                .stream()
                .map(ticketCategoryDisplayDtoMapper)
                .collect(Collectors.toList());
//        return ticketCategoryRepository.findByEvent(event).map(ticketCategoryDisplayDtoMapper);
    }

    public TicketCategory saveTicketCategory(Integer eventId, String name, Double price, Integer availableTickets) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ApiRequestException("Error Saving Ticket Category: Event not found"));

        TicketCategory ticketCategory = new TicketCategory(null, event, name, price, availableTickets);
        return ticketCategoryRepository.save(ticketCategory);
    }

    public TicketCategory updateTicketCategory(Integer categoryId, Integer eventId, String name, Double price, Integer availableTickets) {
        TicketCategoryUpdateDto ticketCategoryUpdateDto = new TicketCategoryUpdateDto(categoryId, eventId, name, price, availableTickets);

        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findById(ticketCategoryUpdateDto.categoryId());

        if (ticketCategoryOptional.isPresent()) {
            TicketCategory existingTicketCategory = ticketCategoryOptional.get();
            ticketCategoryDisplayDtoMapper.update(ticketCategoryUpdateDto, existingTicketCategory);
            ticketCategoryRepository.save(existingTicketCategory);
            return existingTicketCategory;
        }

        throw new ApiRequestException("Ticket Category not found");
    }


//    public String deleteTicket(Integer ticketId) {
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
//
//        if (ticketOptional.isPresent()) {
//            Ticket ticket = ticketOptional.get();
//            if(ticket.getDeletedAt()!=null){
//                return "event already deleted";
//            }
//
//            ticket.setDeletedAt(LocalDateTime.now());
//            ticketRepository.save(ticket);
//            return "event deleted successfully";
//        }
//
//        return "error: event deleted unsuccessfully, event might not exist";
//    }

    public void removeTicketCategory(Integer categoryId) {
        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findById(categoryId);

        if (ticketCategoryOptional.isPresent()) {
            ticketCategoryRepository.deleteById(categoryId);
        } else {
            throw new ApiRequestException("Failed to remove ticket category");
        }
    }
}
