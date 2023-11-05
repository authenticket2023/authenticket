package com.authenticket.authenticket.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.authenticket.authenticket.exception.NonExistentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceImplTest {

    @Mock
    private AmazonS3 amazonS3;

    private AmazonS3ServiceImpl underTest;

    @BeforeEach
    void setUp() {
        underTest = new AmazonS3ServiceImpl(amazonS3);
    }

    @Test
    void testUploadEventFile() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("testfile.png");
        when(multipartFile.getBytes()).thenReturn("Test content".getBytes());

        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.uploadFile(multipartFile, "testfile.png", "event_images");

        assertEquals("File upload: event_images/testfile.png", result);
    }

    @Test
    void testUploadUserFile() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("testfile.png");
        when(multipartFile.getBytes()).thenReturn("Test content".getBytes());

        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.uploadFile(multipartFile, "testfile.png", "user_profile");

        assertEquals("File upload: user_profile/testfile.png", result);
    }

    @Test
    void testUploadEventOrgFile() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("testfile.png");
        when(multipartFile.getBytes()).thenReturn("Test content".getBytes());

        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.uploadFile(multipartFile, "testfile.png", "event_organiser_profile");

        assertEquals("File upload: event_organiser_profile/testfile.png", result);
    }

    @Test
    void testUploadVenueFile() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("testfile.png");
        when(multipartFile.getBytes()).thenReturn("Test content".getBytes());

        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.uploadFile(multipartFile, "testfile.png", "venue_image");

        assertEquals("File upload: venue_image/testfile.png", result);
    }

    @Test
    void testUploadArtistFile() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(multipartFile.getOriginalFilename()).thenReturn("testfile.png");
        when(multipartFile.getBytes()).thenReturn("Test content".getBytes());

        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.uploadFile(multipartFile, "testfile.png", "artist_image");

        assertEquals("File upload: artists/testfile.png", result);
    }

    @Test
    void testUploadInvalidFileType() {
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);
        assertThrows(NonExistentException.class,
                () -> underTest.uploadFile(multipartFile, "testfile.png", "invalidFileType"));
    }

    @Test
    void testUploadFileWithInvalidBucket() {
        MultipartFile multipartFile = mock(MultipartFile.class);

        when(amazonS3.doesBucketExistV2(any())).thenReturn(false);

        assertThrows(NonExistentException.class,
                () -> underTest.uploadFile(multipartFile, "testfile.png", "invalidFileType"));
    }

    @Test
    void testDeleteEventFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);
        when(amazonS3.doesObjectExist(any(), any())).thenReturn(true);

        String result = underTest.deleteFile("testfile.png", "event_images");

        assertEquals("File deleted: event_images/testfile.png", result);
    }

    @Test
    void testDeleteUserFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);
        when(amazonS3.doesObjectExist(any(), any())).thenReturn(true);

        String result = underTest.deleteFile("testfile.png", "user_profile");

        assertEquals("File deleted: user_profile/testfile.png", result);
    }
    @Test
    void testDeleteEventOrgFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);
        when(amazonS3.doesObjectExist(any(), any())).thenReturn(true);

        String result = underTest.deleteFile("testfile.png", "event_organiser_profile");

        assertEquals("File deleted: event_organiser_profile/testfile.png", result);
    }

    @Test
    void testDeleteVenueFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);
        when(amazonS3.doesObjectExist(any(), any())).thenReturn(true);

        String result = underTest.deleteFile("testfile.png", "venue_image");

        assertEquals("File deleted: venue_image/testfile.png", result);
    }

    @Test
    void testDeleteArtistFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);
        when(amazonS3.doesObjectExist(any(), any())).thenReturn(true);

        String result = underTest.deleteFile("testfile.png", "artist_image");

        assertEquals("File deleted: artists/testfile.png", result);
    }

    @Test
    void testDeleteFileWithInvalidBucket() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(false);

        assertThrows(NonExistentException.class,
                () -> underTest.deleteFile("testfile.png", "invalidFileType"));
    }

    @Test
    void testDeleteFileWithInvalidFileType() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        assertThrows(NonExistentException.class,
                () -> underTest.deleteFile("testfile.png", "invalidFileType"));
    }

    @Test
    void testDeleteFileWithNonExistentFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);
        when(amazonS3.doesObjectExist(any(), any())).thenReturn(false);

        assertThrows(NonExistentException.class,
                () -> underTest.deleteFile("nonExistentFile.png", "event_images"));
    }

    @Test
    void testDisplayEventFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.displayFile("testfile.png", "event_images");

        assertEquals("https://authenticket.s3.ap-southeast-1.amazonaws.com/event_images/testfile.png", result);
    }

    @Test
    void testDisplayUserFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.displayFile("testfile.png", "user_profile");

        assertEquals("https://authenticket.s3.ap-southeast-1.amazonaws.com/user_profile/testfile.png", result);
    }

    @Test
    void testDisplayVenueFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.displayFile("testfile.png", "venue_image");

        assertEquals("https://authenticket.s3.ap-southeast-1.amazonaws.com/venue_image/testfile.png", result);
    }

    @Test
    void testDisplayArtistFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.displayFile("testfile.png", "artist_image");

        assertEquals("https://authenticket.s3.ap-southeast-1.amazonaws.com/artists/testfile.png", result);
    }

    @Test
    void testDisplayEventOrgFile() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        String result = underTest.displayFile("testfile.png", "event_organiser_profile");

        assertEquals("https://authenticket.s3.ap-southeast-1.amazonaws.com/event_organiser_profile/testfile.png", result);
    }

    @Test
    void testDisplayFileWithInvalidBucket() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(false);

        assertThrows(NonExistentException.class,
                () -> underTest.displayFile("testfile.png", "invalidFileType"));
    }

    @Test
    void testDisplayFileWithInvalidFileType() {
        when(amazonS3.doesBucketExistV2(any())).thenReturn(true);

        assertThrows(NonExistentException.class,
                () -> underTest.displayFile("testfile.png", "invalidFileType"));
    }
}