package com.authenticket.authenticket.controller;

import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.dto.artist.ArtistDisplayDto;
import com.authenticket.authenticket.model.Artist;
import com.authenticket.authenticket.repository.ArtistRepository;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import com.authenticket.authenticket.service.impl.ArtistServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**This is the ticket category controller class and the base path for this controller's endpoint is api/v2/ticket-category.*/

@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.PUT, RequestMethod.POST},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping(path = "/api/v2/artist")
public class ArtistController extends Utility {
    private final ArtistRepository artistRepository;

    private final ArtistServiceImpl artistService;

    private final AmazonS3ServiceImpl amazonS3Service;

    @Autowired
    public ArtistController(ArtistRepository artistRepository,
                            ArtistServiceImpl artistService,
                            AmazonS3ServiceImpl amazonS3Service) {
        this.artistRepository = artistRepository;
        this.artistService = artistService;
        this.amazonS3Service = amazonS3Service;
    }

    @GetMapping("/test")
    public String test() {
        return "test successful";
    }

    /**
     * Retrieve a list of all artists.
     *
     * @return A ResponseEntity with a GeneralApiResponse containing a list of artists if they exist, or a message
     *         indicating that no artists were found.
     */
    @GetMapping
    public ResponseEntity<GeneralApiResponse<Object>> findAllArtist() {

        List<ArtistDisplayDto> artistList = artistService.findAllArtists();
        if(artistList.isEmpty()){
            return ResponseEntity.ok(generateApiResponse(artistList, "No artists found."));
        } else{
            return ResponseEntity.ok(generateApiResponse(artistList, "Artists successfully returned."));
        }
    }


    /**
     * Retrieve an artist by their unique identifier.
     *
     * @param artistId The unique identifier of the artist to retrieve.
     * @return A ResponseEntity with a GeneralApiResponse containing the artist's data if found, or an error message
     *         if the artist does not exist.
     */
    @GetMapping("/{artistId}")
    public ResponseEntity<GeneralApiResponse<Object>> findArtistById(@PathVariable("artistId") Integer artistId) {
        Optional<ArtistDisplayDto> artistDisplayDto = artistService.findByArtistId(artistId);
        if(artistDisplayDto.isPresent()){
            return ResponseEntity.status(200).body(generateApiResponse(artistDisplayDto.get(), "User found"));
        }
        return ResponseEntity.status(400).body(generateApiResponse(null, "User does not exist"));
    }

    /**
     * Create a new artist with the provided name.
     *
     * @param name The name of the new artist.
     * @return A ResponseEntity with a GeneralApiResponse indicating the success of artist creation.
     */
    @PostMapping
    public ResponseEntity<GeneralApiResponse<Object>> saveArtist(@RequestParam("name") String name) {
        Artist newArtist = new Artist(null, name, null, null);
        Artist saveArtist = artistService.saveArtist(newArtist);
        return ResponseEntity.ok(generateApiResponse(saveArtist,"Artist created successfully"));
    }

    /**
     * Delete an artist by their unique identifier.
     *
     * @param artistId The unique identifier of the artist to delete.
     * @return A ResponseEntity with a GeneralApiResponse indicating the success of artist deletion.
     */

    @PutMapping("/delete/{artistId}")
    public ResponseEntity<GeneralApiResponse<Object>> deleteUser(@PathVariable("artistId") Integer artistId) {
        artistService.deleteArtist(artistId);
        return ResponseEntity.ok(generateApiResponse(null, String.format("Artist %d Deleted Successfully", artistId)));

    }

    /**
     * Update an artist's image with the provided image file and image name.
     *
     * @param artistImage The new artist image file.
     * @param imageName The name to associate with the artist image.
     * @param artistId The unique identifier of the artist for whom to update the image.
     * @return A ResponseEntity with a GeneralApiResponse indicating the success or failure of the image update.
     */

    @PutMapping("/image")
    public ResponseEntity<GeneralApiResponse<Object>> updateArtistImage(@RequestParam("artistImage") MultipartFile artistImage,
                                                                         @RequestParam("imageName") String imageName,
                                                                         @RequestParam("artistId") Integer artistId) {
        try{
            if(artistRepository.findById(artistId).isPresent()){
                amazonS3Service.uploadFile(artistImage, imageName, "artist_image");
                return ResponseEntity.ok(generateApiResponse(artistService.updateArtistImage(imageName, artistId),"Artist Image Uploaded Successfully."));
            } else {
                return ResponseEntity.status(400).body(generateApiResponse(artistService.updateArtistImage(imageName, artistId),"Artist Image Uploaded Failed."));
            }

        } catch (AmazonS3Exception e) {
            String errorCode = e.getErrorCode();
            if ("AccessDenied".equals(errorCode)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(generateApiResponse(null, "Access Denied to Amazon."));
            } else if ("NoSuchBucket".equals(errorCode)) {

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(generateApiResponse(null, "S3 bucket not found."));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(generateApiResponse(null, "An error occurred during S3 interaction."));
            }
        }
    }
}
