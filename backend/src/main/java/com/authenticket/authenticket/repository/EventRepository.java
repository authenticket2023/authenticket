package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query(nativeQuery = true,
            value = "SELECT " +
                    "A.artist_name, " +
                    "A.artist_image, " +
                    "E.organiser_id, " +
                    "E.venue_id, " +
                    "E.event_name, " +
                    "E.event_description, " +
                    "E.category_id, " +
                    "E.event_date, " +
                    "E.event_location, " +
                    "E.other_event_info, " +
                    "E.total_tickets, " +
                    "E.total_tickets_sold, " +
                    "E.event_image, " +
                    "E.ticket_sale_date, " +
                    "E.type_id " +
                    "FROM " +
                    "dev.Artist AS A " +
                    "JOIN " +
                    "dev.Artist_Event AS AE ON A.artist_id = AE.artist_id " +
                    "JOIN " +
                    "dev.Event AS E ON AE.event_id = E.event_id")
    List<Object[]> getAssignedEvent();
}
