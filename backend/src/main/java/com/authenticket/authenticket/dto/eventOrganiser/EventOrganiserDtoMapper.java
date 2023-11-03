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

/**
 * A mapper class responsible for mapping between EventOrganiser entities and EventOrganiserDisplayDto objects.
 * {@link EventOrganiserDisplayDto} DTOs and performing updates on event organiser entities.
 */
@Service
public class EventOrganiserDtoMapper implements Function<EventOrganiser, EventOrganiserDisplayDto> {
    private final PasswordEncoder passwordEncoder;

    private final AdminDtoMapper adminDtoMapper;

    /**
     * Constructs an EventOrganiserDtoMapper with the specified dependencies.
     *
     * @param passwordEncoder The password encoder for encoding and decoding passwords.
     * @param adminDtoMapper The mapper for Admin entities to AdminDisplayDto objects.
     */
    @Autowired
    public EventOrganiserDtoMapper(PasswordEncoder passwordEncoder, AdminDtoMapper adminDtoMapper) {
        this.passwordEncoder = passwordEncoder;
        this.adminDtoMapper = adminDtoMapper;
    }

    /**
     * Maps an EventOrganiser entity to an EventOrganiserDisplayDto object.
     *
     * @param organiser The EventOrganiser entity to be mapped.
     * @return An EventOrganiserDisplayDto representing the mapped data.
     */
    public EventOrganiserDisplayDto apply(EventOrganiser organiser) {
        AdminDisplayDto adminDisplayDto = null;

        if (organiser.getAdmin() != null) {
            adminDisplayDto = adminDtoMapper.apply(organiser.getAdmin());
        }

        return new EventOrganiserDisplayDto(
                organiser.getOrganiserId(),
                organiser.getName(),
                organiser.getEmail(),
                organiser.getDescription(),
                organiser.getLogoImage(),
                EventOrganiser.getRole(),
                organiser.getReviewStatus(),
                organiser.getReviewRemarks(),
                adminDisplayDto,
                organiser.getEnabled(),
                organiser.getCreatedAt(),
                organiser.getUpdatedAt(),
                organiser.getDeletedAt()
        );
    }

    /**
     * Updates an EventOrganiser entity with the information provided in the EventOrganiserUpdateDto.
     *
     * @param updateDto The EventOrganiserUpdateDto containing the updated information.
     * @param organiser The EventOrganiser entity to be updated.
     */
    public void update(EventOrganiserUpdateDto updateDto, EventOrganiser organiser) {
        if (updateDto.name() != null) {
            organiser.setName(updateDto.name());
        }
        if (updateDto.description() != null) {
            organiser.setDescription(updateDto.description());
        }
        if (updateDto.password() != null) {
            organiser.setPassword(passwordEncoder.encode(updateDto.password()));
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

    /**
     * Maps a list of EventOrganiser entities to a list of EventOrganiserDisplayDto objects.
     *
     * @param organiserList The list of EventOrganiser entities to be mapped.
     * @return A list of EventOrganiserDisplayDto objects representing the mapped data.
     */
    public List<EventOrganiserDisplayDto> map(List<EventOrganiser> organiserList) {
        return organiserList.stream()
                .map(this::apply)
                .collect(Collectors.toList());
    }
}