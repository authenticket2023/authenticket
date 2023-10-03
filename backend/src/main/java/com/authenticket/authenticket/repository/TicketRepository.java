package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Order;
import com.authenticket.authenticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findAllByTicketPricingEventEventIdAndSectionSectionId(Integer eventId, String sectionId);
    List<Ticket> findAllByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(Integer eventId, String sectionId, Integer rowNo);
    Integer countByTicketPricingEventEventIdAndSectionSectionIdAndRowNo(Integer eventId, String sectionId, Integer rowNo);

    @Query(
            nativeQuery = true, value="SELECT " +
            "(s.no_of_rows * s.no_of_seats_per_row - CAST(COUNT(t) AS INTEGER)) " +
            "FROM dev.section s " +
            "JOIN dev.ticket t ON s.section_id = t.section_id AND t.event_id = :eventId AND t.section_id = :sectionId " +
            "GROUP BY s.section_id, s.no_of_rows, s.no_of_seats_per_row, s.category_id")
    Integer findNoOfAvailableTicketsBySectionAndEvent(@Param("eventId") Integer eventId, @Param("sectionId") String sectionId);
    @Query(
            nativeQuery = true, value="SELECT e.event_id, " +
            "s.section_id, " +
            "s.category_id, " +
            "(s.no_of_rows * s.no_of_seats_per_row), " +
            "CAST(COUNT(t) AS INTEGER),  " +
            "(s.no_of_rows * s.no_of_seats_per_row - CAST(COUNT(t) AS INTEGER)), " +
            "CASE " +
            "WHEN COUNT(t) = (s.no_of_rows * s.no_of_seats_per_row) THEN 'Sold Out'" +
            "WHEN COUNT(t) >= 0.8 * (s.no_of_rows * s.no_of_seats_per_row) THEN 'Selling Fast' " +
            "ELSE 'Available' " +
            "END " +
            "FROM dev.event e "+
           "JOIN dev.venue v ON e.venue_id = v.venue_id "+
            "JOIN dev.section s ON v.venue_id = s.venue_id "+
            "LEFT JOIN dev.ticket t ON s.section_id = t.section_id AND e.event_id = t.event_id "+
            "WHERE e.event_id = :eventId "+
            "GROUP BY e.event_id, s.section_id, s.no_of_rows, s.no_of_seats_per_row, s.category_id ")
    List<Object[]> findAllTicketDetailsBySectionForEvent(@Param("eventId") Integer eventId);

    @Query(
            nativeQuery = true, value="SELECT e.event_id, " +
            "s.section_id, " +
            "s.category_id, " +
            "(s.no_of_rows * s.no_of_seats_per_row), " +
            "CAST(COUNT(t) AS INTEGER),  " +
            "(s.no_of_rows * s.no_of_seats_per_row - CAST(COUNT(t) AS INTEGER)), " +
            "CASE " +
            "WHEN COUNT(t) = (s.no_of_rows * s.no_of_seats_per_row) THEN 'Sold Out'" +
            "WHEN COUNT(t) >= 0.8 * (s.no_of_rows * s.no_of_seats_per_row) THEN 'Selling Fast' " +
            "ELSE 'Available' " +
            "END " +
            "FROM dev.event e "+
            "JOIN dev.venue v ON e.venue_id = v.venue_id "+
            "JOIN dev.section s ON v.venue_id = s.venue_id "+
            "LEFT JOIN dev.ticket t ON s.section_id = t.section_id AND e.event_id = t.event_id "+
            "WHERE e.event_id = :eventId AND s.section_id = :sectionId "+
            "GROUP BY e.event_id, s.section_id, s.no_of_rows, s.no_of_seats_per_row, s.category_id ")
    List<Object[]> findTicketDetailsForSection(@Param("eventId") Integer eventId,@Param("sectionId") String sectionId );

    Integer countAllByTicketPricingEventEventId(Integer eventId);

    List<Ticket> findAllByTicketPricing_Event_EventId(Integer eventId);
    List<Ticket> findAllByOrder(Order order);

    List<Ticket> findAllByOrderIn(List<Order> order);

    @Query(nativeQuery = true,value = "select * from dev.ticket t where t.order_id = :orderId ")
    List<Ticket> customFindAllByOrderId(@Param("orderId") Integer orderId);
}
