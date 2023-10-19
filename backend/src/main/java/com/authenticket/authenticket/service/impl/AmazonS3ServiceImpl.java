package com.authenticket.authenticket.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.authenticket.authenticket.exception.NonExistentException;
import com.authenticket.authenticket.service.AmazonS3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * This class provides the implementation of the `AmazonS3Service` interface, which is responsible
 * for handling file storage and retrieval using Amazon S3.
 */

@Service
@Slf4j
public class AmazonS3ServiceImpl implements AmazonS3Service {

    private final AmazonS3 amazonS3;

    @Autowired
    public AmazonS3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Value("${authenticket.S3-bucket-name}")
    private String bucketName;

//    public String generateUrl(String fileName, HttpMethod http){
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.MINUTE, 1);
//        URL url = amazonS3.generatePresignedUrl(bucketName, fileName, cal.getTime(), http);
//        return url.toString();
//    }

    private File convertMultiPartFileToFile(MultipartFile file) {
        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            log.error("Error converting multipart file to file", e);
        }
        return convertFile;
    }

    /**
     * Upload a file to Amazon S3 with the given `imageName` and `fileType`.
     *
     * @param file     The file to be uploaded.
     * @param imageName The name of the image.
     * @param fileType The type of file (e.g., "event_images").
     * @return A message indicating the success of the file upload.
     * @throws NonExistentException if the specified `fileType` does not exist or if the S3 bucket does not exist.
     */
    @Override
    public String uploadFile(MultipartFile file, String imageName, String fileType) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            throw new NonExistentException("Bucket does not exist");
        }


        String fileName;
        if (fileType.equals("event_images")) {
            fileName = "event_images/" + imageName;
        } else if (fileType.equals("user_profile")) {
            fileName = "user_profile/" + imageName;
        } else if (fileType.equals("event_organiser_profile")) {
            fileName = "event_organiser_profile/" + imageName;
        } else if (fileType.equals("venue_image")) {
            fileName = "venue_image/" + imageName;
        } else if (fileType.equals("artist_image")) {
            fileName = "artists/" + imageName;
        } else {
            //exception handling
            throw new NonExistentException("File type input does not exist");
        }

        File fileObj = convertMultiPartFileToFile(file);

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();
        return "File upload: " + fileName;
    }

    /**
     * Delete a file from Amazon S3 with the given `imageName` and `fileType`.
     *
     * @param imageName The name of the image.
     * @param fileType   The type of file (e.g., "event_images").
     * @return A message indicating the success of the file deletion.
     * @throws NonExistentException if the specified `fileType` does not exist, the file does not exist, or if the S3 bucket does not exist.
     */
    @Override
    public String deleteFile(String imageName, String fileType) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            throw new NonExistentException("Bucket does not exist");
        }
        String fileName;
        if (fileType.equals("event_images")) {
            fileName = "event_images/" + imageName;
        } else if (fileType.equals("user_profile")) {
            fileName = "user_profile/" + imageName;
        } else if (fileType.equals("event_organiser_profile")) {
            fileName = "event_organiser_profile/" + imageName;
        } else if (fileType.equals("venue_image")) {
            fileName = "venue_image/" + imageName;
        } else if (fileType.equals("artist_image")) {
            fileName = "artists/" + imageName;
        } else {
            //exception handling
            throw new NonExistentException("File type input does not exist");
        }
        if (!amazonS3.doesObjectExist(bucketName, fileName)) {
            throw new NonExistentException("File type input does not exist");
        }
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "File deleted: " + fileName;
    }

    /**
     * Display a file from Amazon S3 with the given `imageName` and `fileType`.
     *
     * @param imageName The name of the image.
     * @param fileType   The type of file (e.g., "event_images").
     * @return The URL of the displayed file.
     * @throws NonExistentException if the specified `fileType` does not exist or if the S3 bucket does not exist.
     */
    @Override
    public String displayFile(String imageName, String fileType) {
        if (!amazonS3.doesBucketExistV2(bucketName)) {
            throw new NonExistentException("Bucket does not exist");
        }
        String fileName;
        if (fileType.equals("event_images")) {
            fileName = "event_images/" + imageName;
        } else if (fileType.equals("user_profile")) {
            fileName = "user_profile/" + imageName;
        } else if (fileType.equals("venue_image")) {
            fileName = "venue_image/" + imageName;
        } else if (fileType.equals("artist_image")) {
            fileName = "artists/" + imageName;
        } else {
            //exception handling
            throw new NonExistentException("File type input does not exist");
        }
        return "https://authenticket.s3.ap-southeast-1.amazonaws.com/" + fileName;
    }
}
