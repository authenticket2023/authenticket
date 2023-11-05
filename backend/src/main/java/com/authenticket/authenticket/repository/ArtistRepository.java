package com.authenticket.authenticket.repository;

import com.authenticket.authenticket.model.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    List<Artist> findAllByDeletedAtIsNull();
}
