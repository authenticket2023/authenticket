package com.authenticket.authenticket.service;

import com.amazonaws.HttpMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface AmazonS3Service {
    String uploadFile(MultipartFile file, String user, String fileType);
    String deleteFile(String imageName, String user, String fileType);
    String displayFile(String imageName, String user, String fileType);
}
