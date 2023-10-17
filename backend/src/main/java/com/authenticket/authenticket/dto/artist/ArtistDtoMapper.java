package com.authenticket.authenticket.dto.artist;

import com.authenticket.authenticket.model.Artist;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArtistDtoMapper implements Function<Artist, ArtistDisplayDto> {
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

    public void update (Artist newArtist, Artist oldArtist){
        if(newArtist.getArtistName() != null){
            oldArtist.setArtistName(newArtist.getArtistName());
        }
    }

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

    public Set<ArtistDisplayDto> mapArtistDisplayDto(List<Object[]> artistObjects) {

        return artistObjects.stream()
                .map(this::applyArtistDisplayDto)
                .collect(Collectors.toSet());
    }
}
