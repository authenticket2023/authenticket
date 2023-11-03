package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.FeaturedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FeaturedEventRepository extends JpaRepository<FeaturedEvent, Integer> {
    Page<FeaturedEvent> findAllFeaturedEventsByStartDateBeforeAndEndDateAfter(LocalDateTime currentDateTime, LocalDateTime currentDateTime2, Pageable pageable);

}
