package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.section.EventSectionDisplayDto;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;

import java.util.List;

public interface SectionService {
    List<EventSectionDisplayDto> getSectionDetailsForEvent(Integer eventId);
    Section saveSection (Section Section);
    int[][] getCurrentSeatMatrix(Integer eventId, Section section);
    List<Ticket> seatAllocate(Integer sectionId, Integer eventId, Integer ticketsToPurchase);

    void seatUnallocate(Integer orderId);

    List<Ticket> findAdjacentSeatsOf(Integer eventId, Section section, Integer ticketCount);

    String[] getSeatCombinationRank(Integer ticketCount);



}
