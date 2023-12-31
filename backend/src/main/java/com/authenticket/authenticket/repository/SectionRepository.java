package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Section;
import com.authenticket.authenticket.model.VenueSectionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SectionRepository extends JpaRepository<Section, VenueSectionId> {

}
