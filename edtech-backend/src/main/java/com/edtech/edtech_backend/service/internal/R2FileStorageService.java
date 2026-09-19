package com.edtech.edtech_backend.service.internal;

import com.edtech.edtech_backend.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URI;

@Service
@Primary
public class R2FileStorageService implements StorageService {

    private final S3Client s3;
    private final String bucket;

    public R2FileStorageService(
            @Value("${cloudflare.r2.access-key-id}") String accessKey,
            @Value("${cloudflare.r2.secret-access-key}") String secretKey,
            @Value("${cloudflare.r2.endpoint}") String endpoint,
            @Value("${cloudflare.r2.bucket-name}") String bucketName,
            @Value("${cloudflare.r2.region:auto}") String region
    ) {

        if (accessKey == null || accessKey.isBlank()
                || secretKey == null || secretKey.isBlank()
                || endpoint == null || endpoint.isBlank()
                || bucketName == null || bucketName.isBlank()) {

            throw new RuntimeException(
                    "R2 configuration missing. Check cloudflare.r2 properties."
            );
        }

        AwsBasicCredentials credentials =
                AwsBasicCredentials.create(accessKey, secretKey);

        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(credentials)
                )
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .endpointOverride(URI.create(endpoint))
                .build();

        this.bucket = bucketName;
    }

    @Override
    public String storeFile(MultipartFile file, String subFolder) {

        try {
            String originalFilename = file.getOriginalFilename();

            String safeName =
                    System.currentTimeMillis()
                            + "_"
                            + (originalFilename != null
                            ? originalFilename.replaceAll(
                            "[^a-zA-Z0-9.\\-_]",
                            "_"
                    )
                            : "file");

            String key =
                    (subFolder == null || subFolder.isBlank())
                            ? safeName
                            : subFolder + "/" + safeName;

            PutObjectRequest putObjectRequest =
                    PutObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .contentType(file.getContentType())
                            .build();

            s3.putObject(
                    putObjectRequest,
                    RequestBody.fromBytes(file.getBytes())
            );

            return key;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read file for upload",
                    e
            );
        }
    }
}