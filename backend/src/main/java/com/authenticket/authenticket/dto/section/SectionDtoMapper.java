package com.authenticket.authenticket.dto.section;

import com.authenticket.authenticket.dto.order.OrderDisplayDto;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A service class for mapping and processing Section DTOs.
 * {@link SectionDisplayDto} DTOs and performing updates on section entities.
 */
@Service
public class SectionDtoMapper implements Function<Section, SectionDisplayDto> {

    private final TicketServiceImpl ticketService;

    /**
     * Constructs a new SectionDtoMapper with the specified dependencies.
     *
     * @param ticketService The TicketService implementation for retrieving ticket-related information.
     */
    @Autowired
    public SectionDtoMapper(TicketServiceImpl ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Applies the mapping function to convert a Section object to a SectionDisplayDto.
     *
     * @param section The Section object to be mapped to a SectionDisplayDto.
     * @return The SectionDisplayDto representing the section details.
     */
    @Override
    public SectionDisplayDto apply(Section section) {
        // Implementation is not provided here as it depends on your specific requirements.
        return null; // Modify this to return the mapped SectionDisplayDto.
    }

    /**
     * Applies the mapping function to convert an array of objects to a SectionTicketDetailsDto.
     *
     * @param obj An array of objects containing section ticket details.
     * @return The SectionTicketDetailsDto representing the section's ticket details.
     */
    public SectionTicketDetailsDto applySectionTicketDetailsDto(Object[] obj) {
        return new SectionTicketDetailsDto(
                (String) obj[1],           // Section ID
                (Integer) obj[2],          // Category ID
                (Integer) obj[3],          // Total Seats
                (Integer) obj[4],          // Occupied Seats
                (Integer) obj[5],          // Available Seats
                ticketService.getMaxConsecutiveSeatsForSection((Integer) obj[0], (String) obj[1]), // Max Consecutive Seats
                (String) obj[6],           // Status
                (Double) obj[7]            // Ticket Price
        );
    }

    /**
     * Maps a list of section ticket details objects to a list of SectionTicketDetailsDto objects.
     *
     * @param sectionTicketDetailsObjects The list of section ticket details as arrays of objects.
     * @return A list of SectionTicketDetailsDto representing the section ticket details.
     */
    public List<SectionTicketDetailsDto> mapSectionTicketDetailsDto(List<Object[]> sectionTicketDetailsObjects) {
        return sectionTicketDetailsObjects.stream()
                .map(this::applySectionTicketDetailsDto)
                .collect(Collectors.toList());
    }
}
