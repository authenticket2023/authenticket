package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Event;
import com.authenticket.authenticket.model.EventType;
import com.authenticket.authenticket.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {

}
