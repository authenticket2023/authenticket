package com.authenticket.authenticket.service.impl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.authenticket.authenticket.service.AmazonS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;


@Service
@Slf4j
@RequiredArgsConstructor
public class AmazonS3ServiceImpl implements AmazonS3Service {

    @Autowired
    private final AmazonS3 amazonS3;

    @Value("${authenticket.S3-bucket-name}")
    private String bucketName;

    private File convertMultiPartFileToFile (MultipartFile file){
        File convertFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        try (FileOutputStream fos = new FileOutputStream(convertFile)){
            fos.write(file.getBytes());
        } catch (IOException e){
            log.error("Error converting multipart file to file", e);
        }
        return convertFile;
    }

    public String uploadFile(MultipartFile file, String user, String fileType){
        if(!amazonS3.doesBucketExistV2(bucketName)){
            return "Bucket does not exist";
        }

        String fileName;
        if(fileType.equals("event_images")){
            fileName = "event_images/" + user + file.getOriginalFilename();
        } else if (fileType.equals("user_profile")){
            fileName = "user_profile/" + user + "_profile_pic";
        } else {
            //exception handling
            return null;
        }

        File fileObj = convertMultiPartFileToFile(file);

        amazonS3.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
        fileObj.delete();
        return "File upload: " +fileName;
    }

    public String deleteFile(String imageName, String user, String fileType) {
        if(!amazonS3.doesBucketExistV2(bucketName)){
            return "Bucket does not exist";
        }
        String fileName;
        if(fileType.equals("event_images")){
            fileName = "event_images/" + user + imageName;
        } else if (fileType.equals("user_profile")){
            fileName = "user_profile/" + user + "_profile_pic";
        } else {
            //exception handling
            return null;
        }
        amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
        return "File deleted: " +fileName;
    }

    public String displayFile(String imageName, String user, String fileType) {
        if(!amazonS3.doesBucketExistV2(bucketName)){
            return "Bucket does not exist";
        }
        String fileName;
        if(fileType.equals("event_images")){
            fileName = "event_images/" + user + imageName;
        } else if (fileType.equals("user_profile")){
            fileName = "user_profile/" + user + "_profile_pic";
        } else {
            //exception handling
            return null;
        }
        return "https://authenticket.s3.ap-southeast-1.amazonaws.com/" +fileName;
    }
}
