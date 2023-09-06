package com.authenticket.authenticket.dto.artist;

import com.authenticket.authenticket.model.Artist;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ArtistDtoMapper implements Function<Artist, ArtistDisplayDto> {
    public ArtistDisplayDto apply(Artist artist){
        return new ArtistDisplayDto(
                artist.getArtistName(),
                artist.getArtistImage()
        );
    }

    public void update (Artist newArtist, Artist oldArtist){
        if(newArtist.getArtistName() != null){
            oldArtist.setArtistName(newArtist.getArtistName());
        }
    }
}
