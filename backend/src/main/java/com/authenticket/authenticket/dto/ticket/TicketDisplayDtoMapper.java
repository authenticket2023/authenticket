package com.authenticket.authenticket.dto.ticket;

import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TicketDisplayDtoMapper implements Function<Ticket, TicketDisplayDto> {
//    private final UserRepository userRepository;
//
//    private final TicketCategoryRepository categoryRepository;
//
//    @Autowired
//    public TicketDisplayDtoMapper(UserRepository userRepository, TicketCategoryRepository categoryRepository) {
//        this.userRepository = userRepository;
//        this.categoryRepository = categoryRepository;
//    }
//
    public TicketDisplayDto apply(Ticket ticket) {
        return new TicketDisplayDto(
                ticket.getTicketId());
    }
//
//    public void update(TicketUpdateDto dto, Ticket ticket) {
//        if (dto.userId() != null) {
//            User user = userRepository.findById(dto.userId()).orElse(null);
//            ticket.setUser(user);
//        }
//    }


}
