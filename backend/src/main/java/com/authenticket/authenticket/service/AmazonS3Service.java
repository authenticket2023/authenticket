package com.authenticket.authenticket.service;

import org.springframework.web.multipart.MultipartFile;

public interface AmazonS3Service {
    String uploadFile(MultipartFile file, String imageName, String fileType);
    String deleteFile(String imageName, String fileType);
    String displayFile(String imageName, String fileType);
}
