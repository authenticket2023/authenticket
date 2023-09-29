package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllByEventEventIdAndSectionSectionId(Integer eventId, Integer sectionId);

    List<Ticket> findAllByEventEventIdAndSectionSectionIdAndRowNo(Integer eventId, Integer sectionId,Integer rowNo);
    Integer countByEventEventIdAndSectionSectionIdAndRowNo(Integer eventId, Integer sectionId, Integer rowNo);
    Integer countByEventEventIdAndSectionSectionId(Integer eventId, Integer sectionId);

}
