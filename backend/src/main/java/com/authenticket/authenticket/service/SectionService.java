package com.authenticket.authenticket.service;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;

import java.util.List;

public interface SectionService {
    Section saveSection (Section Section);

    int[][] getCurrentSeatMatrix(Event event, Section section);


}
