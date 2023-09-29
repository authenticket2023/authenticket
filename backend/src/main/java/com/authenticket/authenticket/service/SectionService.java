package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.section.EventSectionDisplayDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;

import java.util.List;

public interface SectionService {
    Section saveSection (Section Section);
    List<Ticket> seatAllocate(Integer sectionId, Integer eventId, Integer ticketsToPurchase);
    List<EventSectionDisplayDto> getSectionDetailsForEvent(Integer eventId);
    int[][] getCurrentSeatMatrix(Event event, Section section);
    List<Ticket> findAdjacentSeatsOf(Event event, Section section, Integer ticketCount);
    String[] getSeatCombinationRank(Integer ticketCount);
    Integer getNoOfAvailableSeatsBySectionForEvent(Event event, Section section);

}
