package com.authenticket.authenticket.service.impl;

import com.authenticket.authenticket.dto.ticket.TicketDisplayDto;
import com.authenticket.authenticket.dto.ticket.TicketDisplayDtoMapper;
import com.authenticket.authenticket.dto.ticket.TicketUpdateDto;
import com.authenticket.authenticket.exception.AlreadyDeletedException;
import com.authenticket.authenticket.exception.ApiRequestException;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.model.*;
import com.authenticket.authenticket.repository.*;
import com.authenticket.authenticket.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements TicketService {


    private final TicketRepository ticketRepository;


    private final TicketDisplayDtoMapper ticketDisplayDtoMapper;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository,
                             TicketDisplayDtoMapper ticketDisplayDtoMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketDisplayDtoMapper = ticketDisplayDtoMapper;
    }

    public List<TicketDisplayDto> findAllTicket() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketDisplayDtoMapper)
                .collect(Collectors.toList());
    }

    public TicketDisplayDto findTicketById(Integer ticketId) {
        Optional<TicketDisplayDto> ticketDisplayDtoOptional = ticketRepository.findById(ticketId).map(ticketDisplayDtoMapper);
        if (ticketDisplayDtoOptional.isPresent()) {
            return ticketDisplayDtoOptional.get();
        }

        throw new ApiRequestException("Ticket not found");
    }

    public Ticket saveTicket(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

//    public Ticket updateTicket(TicketUpdateDto ticketUpdateDto) {
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketUpdateDto.ticketId());
//
//        if (ticketOptional.isPresent()) {
//            Ticket existingTicket = ticketOptional.get();
//            ticketDisplayDtoMapper.update(ticketUpdateDto, existingTicket);
//            ticketRepository.save(existingTicket);
//            return existingTicket;
//        }
//
//        throw new NonExistentException("Error Updating Ticket: Ticket not found");
//    }
//
//
//    public void deleteTicket(Integer ticketId) {
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
//
//        if (ticketOptional.isPresent()) {
//            Ticket ticket = ticketOptional.get();
//            if (ticket.getDeletedAt() != null) {
//                throw new AlreadyDeletedException("Ticket already deleted");
//            }
//
//            ticket.setDeletedAt(LocalDateTime.now());
//            ticketRepository.save(ticket);
//        } else {
//            throw new NonExistentException("Ticket does not exist");
//        }
//    }
//
//    public void removeTicket(Integer ticketId) {
//        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);
//
//        if (ticketOptional.isPresent()) {
//            ticketRepository.deleteById(ticketId);
//        } else {
//            throw new NonExistentException("Ticket does not exist");
//        }
//    }
}
