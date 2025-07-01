package com.openisle.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ImageUploader implementation using Tencent Cloud COS.
 */
@Service
public class CosImageUploader extends ImageUploader {

    private final COSClient cosClient;
    private final String bucketName;
    private final String baseUrl;
    private final ExecutorService executor = Executors.newFixedThreadPool(2,
            new CustomizableThreadFactory("cos-upload-"));

    @org.springframework.beans.factory.annotation.Autowired
    public CosImageUploader(
            @Value("${cos.secret-id:}") String secretId,
            @Value("${cos.secret-key:}") String secretKey,
            @Value("${cos.region:ap-guangzhou}") String region,
            @Value("${cos.bucket-name:}") String bucketName,
            @Value("${cos.base-url:https://example.com}") String baseUrl) {
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);
        ClientConfig config = new ClientConfig(new Region(region));
        this.cosClient = new COSClient(cred, config);
        this.bucketName = bucketName;
        this.baseUrl = baseUrl;
    }

    // for tests
    CosImageUploader(COSClient cosClient, String bucketName, String baseUrl) {
        this.cosClient = cosClient;
        this.bucketName = bucketName;
        this.baseUrl = baseUrl;
    }

    @Override
    public CompletableFuture<String> upload(byte[] data, String filename) {
        return CompletableFuture.supplyAsync(() -> {
            String ext = "";
            int dot = filename.lastIndexOf('.');
            if (dot != -1) {
                ext = filename.substring(dot);
            }
            String randomName = UUID.randomUUID().toString().replace("-", "") + ext;

            ObjectMetadata meta = new ObjectMetadata();
            meta.setContentLength(data.length);
            PutObjectRequest req = new PutObjectRequest(
                    bucketName,
                    randomName,
                    new ByteArrayInputStream(data),
                    meta);
            cosClient.putObject(req);
            return baseUrl + "/" + randomName;
        }, executor);
    }
}
