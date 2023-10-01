package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;

import java.util.List;

public interface SectionService {
    Section saveSection (Section Section);

    int[][] getCurrentSeatMatrix(Event event, Section section);

    List<SectionTicketDetailsDto> findSectionDetail(Event event, Section section);



}
