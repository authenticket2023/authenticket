package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryDisplayDtoMapper;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryUpdateDto;
import com.authenticket.authenticket.dto.ticketcategory.TicketCategoryUpdateDtoMapper;
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
    private TicketCategoryUpdateDtoMapper ticketCategoryUpdateDtoMapper;

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

    public TicketCategory saveTicketCategory(TicketCategory ticketCategory) {
        return ticketCategoryRepository.save(ticketCategory);
    }

    public TicketCategory updateTicketCategory(TicketCategoryUpdateDto ticketCategoryUpdateDto) {
        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findById(ticketCategoryUpdateDto.categoryId());

        if (ticketCategoryOptional.isPresent()) {
            TicketCategory existingTicketCategory = ticketCategoryOptional.get();
            ticketCategoryUpdateDtoMapper.apply(ticketCategoryUpdateDto, existingTicketCategory);
            ticketCategoryRepository.save(existingTicketCategory);
            return existingTicketCategory;
        }

        return null;
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

    public Boolean removeTicketCategory(Integer categoryId) {

        Optional<TicketCategory> ticketCategoryOptional = ticketCategoryRepository.findById(categoryId);

        if (ticketCategoryOptional.isPresent()) {
            ticketCategoryRepository.deleteById(categoryId);
            return true;
        }
        return false;
    }
}
