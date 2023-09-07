package com.authenticket.authenticket.dto.artist;

import com.authenticket.authenticket.model.Artist;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ArtistDtoMapper implements Function<Artist, ArtistDisplayDto> {
    public ArtistDisplayDto apply(Artist artist){
        return new ArtistDisplayDto(
                artist.getArtistId(),
                artist.getArtistName(),
                artist.getArtistImage()
        );
    }

    public void update (Artist newArtist, Artist oldArtist){
        if(newArtist.getArtistName() != null){
            oldArtist.setArtistName(newArtist.getArtistName());
        }
    }

    public ArtistDisplayDto applyArtistDisplayDto(Object[] obj){
        return new ArtistDisplayDto(
                (Integer) obj[0],
                (String) obj[1],
                (String) obj[2]
        );
    }

    public List<ArtistDisplayDto> mapArtistDisplayDto(List<Object[]> eventObjects) {
        return eventObjects.stream()
                .map(this::applyArtistDisplayDto)
                .collect(Collectors.toList());
    }
}
