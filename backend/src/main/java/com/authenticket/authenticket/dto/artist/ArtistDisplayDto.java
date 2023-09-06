package com.authenticket.authenticket.dto.artist;

import com.authenticket.authenticket.model.Event;

import java.util.Set;

public record ArtistDisplayDto(String artistName,
                               String artistImage,

                               Set<Event> eventList) {
}



