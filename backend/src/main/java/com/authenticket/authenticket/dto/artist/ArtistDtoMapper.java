package com.authenticket.authenticket.dto.artist;

import com.authenticket.authenticket.model.Artist;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A service class responsible for mapping Artist entities to ArtistDisplayDto objects
 * and vice versa.
 */
@Service
public class ArtistDtoMapper implements Function<Artist, ArtistDisplayDto> {

    /**
     * Maps an Artist entity to an ArtistDisplayDto object.
     *
     * @param artist The Artist entity to map.
     * @return An ArtistDisplayDto containing the artist's information.
     */
    public ArtistDisplayDto apply(Artist artist){
        return new ArtistDisplayDto(
                artist.getArtistId(),
                artist.getArtistName(),
                artist.getArtistImage(),
                artist.getCreatedAt(),
                artist.getUpdatedAt(),
                artist.getDeletedAt()
        );
    }

    /**
     * Updates an existing artist's information with new data.
     *
     * @param newArtist The new artist information.
     * @param oldArtist The existing artist to be updated.
     */
    public void update(Artist newArtist, Artist oldArtist){
        if(newArtist.getArtistName() != null){
            oldArtist.setArtistName(newArtist.getArtistName());
        }
    }

    /**
     * Maps an array of objects to an ArtistDisplayDto object.
     *
     * @param obj An array of objects representing artist data.
     * @return An ArtistDisplayDto containing the artist's information.
     */
    public ArtistDisplayDto applyArtistDisplayDto(Object[] obj){
        LocalDateTime deletedAtTimeStamp = null;
        if (obj[5] != null){
            deletedAtTimeStamp = ((Timestamp)obj[5]).toLocalDateTime();
        }
        return new ArtistDisplayDto(
                (Integer) obj[0],
                (String) obj[1],
                (String) obj[2],
                ((Timestamp)obj[3]).toLocalDateTime(),
                ((Timestamp)obj[4]).toLocalDateTime(),
                deletedAtTimeStamp
        );
    }

    /**
     * Maps a list of arrays of objects to a set of ArtistDisplayDto objects.
     *
     * @param artistObjects A list of arrays of objects representing artist data.
     * @return A set of ArtistDisplayDto objects containing artists' information.
     */
    public Set<ArtistDisplayDto> mapArtistDisplayDto(List<Object[]> artistObjects) {

        return artistObjects.stream()
                .map(this::applyArtistDisplayDto)
                .collect(Collectors.toSet());
    }
}
