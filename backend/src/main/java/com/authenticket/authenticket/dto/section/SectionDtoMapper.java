package com.authenticket.authenticket.dto.section;

import com.authenticket.authenticket.dto.admin.AdminDtoMapper;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.dto.artist.ArtistDtoMapper;
import com.authenticket.authenticket.dto.eventOrganiser.EventOrganiserDtoMapper;
import com.authenticket.authenticket.dto.eventticketcategory.EventTicketCategoryDtoMapper;
import com.authenticket.authenticket.dto.venue.VenueDtoMapper;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Venue;
import com.authenticket.authenticket.repository.AdminRepository;
import com.authenticket.authenticket.repository.EventRepository;
import com.authenticket.authenticket.repository.EventTypeRepository;
import com.authenticket.authenticket.service.impl.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SectionDtoMapper implements Function<Section, SectionDisplayDto> {

    private final TicketServiceImpl ticketService;


    @Autowired
    public SectionDtoMapper(TicketServiceImpl ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public SectionDisplayDto apply(Section section) {
        return null;
    }

    public SectionTicketDetailsDto applySectionTicketDetailsDto(Object[] obj){

        return new SectionTicketDetailsDto(
                (String) obj[1],
                (Integer) obj[2],
                (Integer) obj[3],
                (Integer) obj[4],
                (Integer) obj[5],
                ticketService.getMaxConsecutiveSeatsForSection((Integer)obj[0],(String) obj[1]),
                (String) obj[6],
                (Double) obj[7]
        );
    }

    public List<SectionTicketDetailsDto> mapSectionTicketDetailsDto(List<Object[]> sectionTicketDetailsObjects) {
        return sectionTicketDetailsObjects.stream()
                .map(this::applySectionTicketDetailsDto)
                .collect(Collectors.toList());
    }
}
