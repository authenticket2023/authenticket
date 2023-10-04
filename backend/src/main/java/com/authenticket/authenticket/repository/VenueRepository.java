package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.User;
import com.authenticket.authenticket.model.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VenueRepository extends JpaRepository<Venue, Integer> {
    Optional<Venue> findByVenueName(String venueName);


    @Query(nativeQuery = true,
            value = "select SUM(s.no_of_rows * s.no_of_seats_per_row)" +
                    "from dev.section s JOIN dev.venue v on s.venue_id = v.venue_id AND v.venue_id = :venueId group by v.venue_id")
    Integer findNoOfSeatsByVenue(@Param("venueId") Integer venueId);
}
