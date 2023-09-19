package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
            value = "SELECT *" +
                    "FROM " +
                    "dev.Event AS E " +
                    "WHERE E.review_status = 'approved' " +
                    "AND E.deleted_at IS NULL " +
                    "AND E.total_tickets > 0 " +
                    "ORDER BY (CAST(E.total_tickets_sold AS DECIMAL) / E.total_tickets) DESC," +
                    "E.total_tickets DESC ",
            countQuery = "SELECT count(*) FROM dev.Event AS E " +
                    "WHERE E.review_status = 'approved' " +
                    "AND E.deleted_at IS NULL " +
                    "AND E.total_tickets > 0 ")
    Page<Event> findBestSellerEvents(Pageable pageable);

    //upcoming ticket sales
    Page<Event> findAllByReviewStatusAndTicketSaleDateAfterAndDeletedAtIsNullOrderByTicketSaleDateAsc(String reviewStatus, LocalDateTime currentDate, Pageable pageable);


}
