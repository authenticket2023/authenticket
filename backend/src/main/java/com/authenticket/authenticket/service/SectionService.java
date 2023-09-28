package com.authenticket.authenticket.service;

import com.authenticket.authenticket.dto.section.EventSectionDisplayDto;
import com.authenticket.authenticket.dto.section.SeatAllocationDto;
import com.authenticket.authenticket.model.Section;

import java.util.List;

public interface SectionService {
    int[][] getSeatMatrix(Integer eventId, Section section);
    List<EventSectionDisplayDto> getSectionDetailsForEvent(Integer eventId);
    Section saveSection (Section Section);
    List<SeatAllocationDto> seatAllocation(Integer sectionId, Integer eventId, Integer ticketsToPurchase);



}
