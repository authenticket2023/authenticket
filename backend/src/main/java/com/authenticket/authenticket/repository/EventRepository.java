package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Integer> {
    @Query(nativeQuery = true,
            value = "SELECT " +
                    "A.artist_id, " +
                    "A.artist_name, " +
                    "A.artist_image " +
                    "FROM " +
                    "dev.Artist AS A " +
                    "JOIN " +
                    "dev.Artist_Event AS AE ON A.artist_id = AE.artist_id " +
                    "JOIN " +
                    "dev.Event AS E ON AE.event_id = E.event_id " +
                    "WHERE E.event_id = :eventId")
    List<Object[]> getArtistByEventId(@Param("eventId") Integer eventId);


    //ADMIN
    //for get all for admin
    List<Event> findAllByOrderByEventIdAsc();

    //get all pending approval events for admin
    List<Event> findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtAsc(String reviewStatus);

    //HOME
    //for get all for homepage
    Page<Event> findAllByReviewStatusAndDeletedAtIsNull(String reviewStatus, Pageable pageable);

    //recently added (order by created at)
    Page<Event> findAllByReviewStatusAndDeletedAtIsNullOrderByCreatedAtDesc(String reviewStatus, Pageable pageable);

    //bestseller (order by ticket sales percentage)
    @Query(nativeQuery = true,
            value = "SELECT E.event_id, e.event_name, e.event_description, e.event_image, et.type_name, e.event_date,\n" +
                    "       v.venue_id, v.venue_name,\n" +
                    "       (subquery.total_tickets_sold / subquery.total_tickets) AS tickets_sold_ratio \n" +
                    "FROM (\n" +
                    "  SELECT E.event_id,\n" +
                    "         (\n" +
                    "           SELECT SUM(CAST((s2.no_of_rows * s2.no_of_seats_per_row) AS DECIMAL))\n" +
                    "           FROM dev.section s2\n" +
                    "           WHERE s2.venue_id = E.venue_id\n" +
                    "         ) AS total_tickets,\n" +
                    "         COUNT(t.event_id) AS total_tickets_sold\n" +
                    "  FROM dev.Event AS E\n" +
                    "  LEFT JOIN dev.ticket t ON E.event_id = t.event_id\n" +
                    "  RIGHT JOIN dev.section s ON E.venue_id = s.venue_id AND t.section_id = s.section_id\n" +
                    "  WHERE E.review_status = 'approved'\n" +
                    "    AND E.deleted_at IS NULL\n" +
                    "  GROUP BY E.event_id\n" +
                    ") AS subquery\n" +
                    "JOIN dev.Event AS E ON subquery.event_id = E.event_id \n" +
                    "JOIN dev.venue v ON E.venue_id = v.venue_id \n" +
                    "JOIN dev.event_type et ON e.type_id = et.type_id\n" +
                    "ORDER BY tickets_sold_ratio DESC, subquery.total_tickets DESC LIMIT 7")
    List<Object[]> findBestSellerEvents();

    //upcoming ticket sales
    Page<Event> findAllByReviewStatusAndTicketSaleDateAfterAndDeletedAtIsNullOrderByTicketSaleDateAsc(String reviewStatus, LocalDateTime currentDate, Pageable pageable);

    //current event (ongoing events that are NOT past)
    Page<Event> findAllByReviewStatusAndEventDateAfterAndDeletedAtIsNullOrderByEventDateAsc(String reviewStatus, LocalDateTime currentDate, Pageable pageable);

    //past event (event date past current date)
    Page<Event> findAllByReviewStatusAndEventDateBeforeAndDeletedAtIsNullOrderByEventDateDesc(String reviewStatus, LocalDateTime currentDate, Pageable pageable);

    //find all events by venue
    Page<Event> findAllByReviewStatusAndVenueVenueIdAndDeletedAtIsNullOrderByEventDateDesc(String reviewStatus, Integer venueId, Pageable pageable);

    //remove all artist for eevnt
//     @Transactional
//     @Modifying
//     @Query(nativeQuery = true,
//             value = "DELETE " +
//                     "FROM " +
//                     "dev.artist_event AS e " +
//                     "WHERE e.event_id = :eventId " )
//     void deleteAllArtistByEventId(@Param("eventId") Integer eventId);


}
