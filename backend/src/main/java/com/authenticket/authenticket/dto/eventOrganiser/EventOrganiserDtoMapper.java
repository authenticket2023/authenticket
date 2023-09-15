package com.authenticket.authenticket.dto.eventOrganiser;

import com.authenticket.authenticket.dto.admin.AdminDisplayDto;
import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.model.EventOrganiser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class EventOrganiserDtoMapper implements Function<EventOrganiser, EventOrganiserDisplayDto> {
    private final PasswordEncoder passwordEncoder;

    private final AdminDtoMapper adminDtoMapper;

    @Autowired
    public EventOrganiserDtoMapper(PasswordEncoder passwordEncoder, AdminDtoMapper adminDtoMapper) {
        this.passwordEncoder = passwordEncoder;
        this.adminDtoMapper = adminDtoMapper;
    }

    public EventOrganiserDisplayDto apply(EventOrganiser organiser) {
        AdminDisplayDto adminDisplayDto = null;

        if (organiser.getAdmin() != null) {
            adminDisplayDto = adminDtoMapper.apply(organiser.getAdmin());
        }

        return new EventOrganiserDisplayDto(
                organiser.getOrganiserId(), organiser.getName(), organiser.getEmail(),
                organiser.getDescription(), organiser.getLogoImage(),
                EventOrganiser.getRole(), organiser.getReviewStatus(), organiser.getReviewRemarks(),
                adminDisplayDto, organiser.getEnabled(), organiser.getCreatedAt(), organiser.getUpdatedAt(), organiser.getDeletedAt());

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
        if (updateDto.enabled() != null) {
            organiser.setEnabled(updateDto.enabled());
        }
        if (updateDto.reviewStatus() != null) {
            organiser.setReviewStatus(updateDto.reviewStatus());
        }
        if (updateDto.reviewRemarks() != null) {
            organiser.setReviewRemarks(updateDto.reviewRemarks());
        }
        if (updateDto.reviewedBy() != null) {
            organiser.setAdmin(updateDto.reviewedBy());
        }
    }

    public List<EventOrganiserDisplayDto> map (List<EventOrganiser> organiserList){
        return organiserList.stream()
                .map(this::apply)
                .collect(Collectors.toList());
    }
}

