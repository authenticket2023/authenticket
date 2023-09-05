package com.authenticket.authenticket.dto.ticket;

import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class TicketDisplayDtoMapper implements Function<Ticket, TicketDisplayDto> {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketCategoryRepository categoryRepository;

    public TicketDisplayDto apply(Ticket ticket) {
        return new TicketDisplayDto(
                ticket.getTicketId(), ticket.getUser().getUserId(), ticket.getEvent().getEventId(),
                ticket.getCategory().getCategoryId());
    }

    public void update(TicketUpdateDto dto, Ticket ticket) {
        if (dto.userId() != null) {
            ticket.setUser(userRepository.findById(dto.userId()).orElse(null));
            ticket.setCategory(categoryRepository.findById(dto.categoryId()).orElse(null));
        }
    }


}
