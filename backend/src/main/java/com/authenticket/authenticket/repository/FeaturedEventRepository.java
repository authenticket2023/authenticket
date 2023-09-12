package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.FeaturedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeaturedEventRepository extends JpaRepository<FeaturedEvent, Integer> {
    List<FeaturedEvent> findTop5FeaturedEventsByStartDateBeforeAndEndDateAfter(LocalDateTime currentDateTime, LocalDateTime currentDateTime2);

}
