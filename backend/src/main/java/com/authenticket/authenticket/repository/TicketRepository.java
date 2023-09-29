package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.dto.section.SectionTicketDetailsDto;
import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllByEventEventIdAndSectionSectionId(Integer eventId, Integer sectionId);
    List<Ticket> findAllByEventEventIdAndSectionSectionIdAndRowNo(Integer eventId, Integer sectionId,Integer rowNo);
    Integer countByEventEventIdAndSectionSectionIdAndRowNo(Integer eventId, Integer sectionId, Integer rowNo);
    Integer countByEventEventIdAndSectionSectionId(Integer eventId, Integer sectionId);

    @Query(
            nativeQuery = true, value="SELECT " +
            "t.section_id, " +
            "t.category_id, " +
            "(s.no_of_rows * s.no_of_seats_per_row), " +
            "CAST(COUNT(t) AS INTEGER),  " +
            "(s.no_of_rows * s.no_of_seats_per_row - CAST(COUNT(t) AS INTEGER)), " +
            "CASE " +
            "WHEN COUNT(t) = (s.no_of_rows * s.no_of_seats_per_row) THEN 'Sold Out'" +
            "WHEN COUNT(t) >= 0.8 * (s.no_of_rows * s.no_of_seats_per_row) THEN 'Selling Fast' " +
            "ELSE 'Available' " +
            "END " +
            "FROM dev.ticket t " +
            "JOIN dev.section s ON t.section_id = s.section_id " +
            "WHERE t.event_id = :eventId " +
            "GROUP BY t.section_id, s.no_of_rows, s.no_of_seats_per_row, t.category_id")
    List<Object[]> findAllTicketDetailsBySectionForEvent(@Param("eventId") Integer eventId);

}
