package com.authenticket.authenticket.dto.section;

import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.model.Section;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SectionDtoMapper implements Function<Section, SectionDisplayDto> {
    @Override
    public SectionDisplayDto apply(Section section) {
        return null;
    }

    public SectionTicketDetailsDto applySectionTicketDetailsDto(Object[] obj){
        return new SectionTicketDetailsDto(
                (Integer) obj[0],
                (Integer) obj[1],
                (Integer) obj[2],
                (Integer) obj[3],
                (Integer) obj[4],
                (String) obj[5]
        );
    }

    public List<SectionTicketDetailsDto> mapSectionTicketDetailsDto(List<Object[]> sectionTicketDetailsObjects) {
        return sectionTicketDetailsObjects.stream()
                .map(this::applySectionTicketDetailsDto)
                .collect(Collectors.toList());
    }
}
