package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
}
