package com.authenticket.authenticket.controller;

import com.authenticket.authenticket.controller.response.GeneralApiResponse;
import com.authenticket.authenticket.service.Utility;
import com.authenticket.authenticket.service.impl.AmazonS3ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**This is the amazon s3 controller class and the base path for this controller's endpoint is api/v2/aws.*/

@RestController
@CrossOrigin(
        origins = {
                "${authenticket.frontend-production-url}",
                "${authenticket.frontend-dev-url}",
                "${authenticket.loadbalancer-url}"
        },
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE},
        allowedHeaders = {"Authorization", "Cache-Control", "Content-Type"},
        allowCredentials = "true"
)
@RequestMapping("/api/v2/aws")
public class AmazonController extends Utility {

    private final AmazonS3ServiceImpl service;

    @Autowired
    public AmazonController(AmazonS3ServiceImpl service) {
        this.service = service;
    }

    /**
     * Upload a file and associate it with the provided image name and file type.
     *
     * @param file The file to be uploaded.
     * @param imageName The name to associate with the uploaded file.
     * @param fileType The type of the file being uploaded (e.g., image, document).
     * @return A ResponseEntity with a GeneralApiResponse indicating the success or failure of the file upload.
     */
    @PostMapping("/upload-file")
    public ResponseEntity<GeneralApiResponse<Object>>  fileUpload(@RequestParam(value = "file") MultipartFile file,
                                                          @RequestParam(value = "imageName") String imageName,
                                                          @RequestParam(value = "fileType") String fileType){
        return ResponseEntity.status(200).body(generateApiResponse(null, service.uploadFile(file, imageName, fileType)));
    }

    /**
     * Delete a file based on its image name and file type.
     *
     * @param imageName The name of the file to be deleted.
     * @param fileType The type of the file to be deleted.
     * @return A ResponseEntity with a GeneralApiResponse indicating the success or failure of the file deletion.
     */
    @DeleteMapping("/delete-file")
    public ResponseEntity<GeneralApiResponse<Object>> fileDelete(@RequestParam(value = "imageName") String imageName,
                                             @RequestParam(value = "file-type") String fileType) {
        return ResponseEntity.status(200).body(generateApiResponse( null, service.deleteFile(imageName, fileType)));
    }

    /**
     * Retrieve the URL for displaying a file based on its image name and file type.
     *
     * @param imageName The name of the file for which to generate the display URL.
     * @param fileType The type of the file for which to generate the display URL.
     * @return A ResponseEntity with a GeneralApiResponse containing the generated file display URL.
     */
    @GetMapping("/display-file")
    public ResponseEntity<GeneralApiResponse<Object>>  fileDisplay(@RequestParam(value = "imageName") String imageName,
                                              @RequestParam(value = "file-type") String fileType){
        return ResponseEntity.status(200).body(generateApiResponse( service.displayFile(imageName, fileType), "url generated"));
    }
}
