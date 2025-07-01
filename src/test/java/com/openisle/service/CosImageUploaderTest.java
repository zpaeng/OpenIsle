package com.openisle.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CosImageUploaderTest {
    @Test
    void uploadReturnsUrl() {
        COSClient client = mock(COSClient.class);
        CosImageUploader uploader = new CosImageUploader(client, "bucket", "http://cos.example.com");

        String url = uploader.upload("data".getBytes(), "img.png").join();

        verify(client).putObject(any(PutObjectRequest.class));
        assertEquals("http://cos.example.com/img.png", url);
    }
}
