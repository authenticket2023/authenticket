package com.authenticket.authenticket.dto.artist;

import java.time.LocalDateTime;

public record ArtistDisplayDto(
        Integer artistId,
        String artistName,
        String artistImage,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime deletedAt) {
}



