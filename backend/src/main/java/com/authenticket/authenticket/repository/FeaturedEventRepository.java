package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.FeaturedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FeaturedEventRepository extends JpaRepository<FeaturedEvent, Integer> {
    Page<FeaturedEvent> findAllFeaturedEventsByStartDateBeforeAndEndDateAfter(LocalDateTime currentDateTime, LocalDateTime currentDateTime2, Pageable pageable);

}
