package com.authenticket.authenticket.dto.ticket;

import com.authenticket.authenticket.model.Ticket;
import com.authenticket.authenticket.repository.TicketCategoryRepository;
import com.authenticket.authenticket.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TicketUpdateDtoMapper {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TicketCategoryRepository categoryRepository;

    public void apply(TicketUpdateDto dto, Ticket ticket) {
        if (dto.userId() != null) {
            ticket.setUser(userRepository.findById(dto.userId()).orElse(null));
            ticket.setCategory(categoryRepository.findById(dto.categoryId()).orElse(null));
        }
    }
}
