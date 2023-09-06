package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.dto.ticket.TicketUpdateDto;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.TicketCategory;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.TicketRepository;
import com.authenticket.authenticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl {
    @Autowired
    private TicketCategoryRepository ticketCategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TicketDisplayDtoMapper ticketDisplayDtoMapper;

    public List<TicketDisplayDto> findAllTicket() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketDisplayDtoMapper)
                .collect(Collectors.toList());
    }

    public TicketDisplayDto findTicketById(Integer ticketId) {
        Optional<TicketDisplayDto> ticketDisplayDtoOptional = ticketRepository.findById(ticketId).map(ticketDisplayDtoMapper);;
        if(ticketDisplayDtoOptional.isPresent()){
            return ticketDisplayDtoOptional.get();
        }

        throw new ApiRequestException("Ticket not found");
    }

    public Ticket saveTicket(Integer userId, Integer eventId, Integer categoryId) throws ApiRequestException {
        User user = userRepository.findById(userId).orElseThrow(() -> new ApiRequestException("Error Saving Ticket: User not found"));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new ApiRequestException("Error Saving Ticket: Event not found"));
        TicketCategory ticketCategory = ticketCategoryRepository.findById(categoryId).orElseThrow(() -> new ApiRequestException("Error Saving Ticket: TicketCategory not found"));

        Ticket ticket = new Ticket(null, user, event, ticketCategory);
        return ticketRepository.save(ticket);
    }

    public Ticket updateTicket(Integer ticketId, Integer userId, Integer eventId, Integer categoryId) {
        TicketUpdateDto ticketUpdateDto = new TicketUpdateDto(ticketId, userId, eventId, categoryId);
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketUpdateDto.ticketId());

        if (ticketOptional.isPresent()) {
            Ticket existingTicket = ticketOptional.get();
            ticketDisplayDtoMapper.update(ticketUpdateDto, existingTicket);
            ticketRepository.save(existingTicket);
            return existingTicket;
        }

        throw new ApiRequestException("Error Updating Ticket: Ticket not found");
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

    public void removeTicket(Integer ticketId) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);

        if (ticketOptional.isPresent()) {
            ticketRepository.deleteById(ticketId);
        } else {
            throw new ApiRequestException("Failed to remove ticket");
        }
    }
}
