package com.authenticket.authenticket.dto.eventOrganiser;

import com.authenticket.authenticket.model.EventOrganiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EventOrganiserDtoMapper implements Function<EventOrganiser, EventOrganiserDisplayDto> {
    @Autowired
    private PasswordEncoder passwordEncoder;
    public EventOrganiserDisplayDto apply(EventOrganiser organiser) {
        Integer adminId = null;

        if(organiser.getAdmin()!=null){
            adminId = organiser.getAdmin().getAdminId();
        }

        return new EventOrganiserDisplayDto(
                organiser.getOrganiserId(), organiser.getName(), organiser.getEmail(),
                organiser.getDescription(), adminId, organiser.getLogoImage(),
                EventOrganiser.getRole(), organiser.getCreatedAt(), organiser.getUpdatedAt());
    }

    public void update(EventOrganiserUpdateDto updateDto, EventOrganiser organiser) {
        if (updateDto.name() != null) {
            organiser.setName(updateDto.name());
        }
        if (updateDto.description() != null) {
            organiser.setDescription(updateDto.description());
        }

        if (updateDto.password() != null) {
            organiser.setPassword(passwordEncoder.encode((updateDto.password())));
        }





    }
}

