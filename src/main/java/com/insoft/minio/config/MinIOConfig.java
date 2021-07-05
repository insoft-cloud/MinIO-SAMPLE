package com.insoft.minio.config;

import kr.go.smes.fileservice.MinioFileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 빈 생성 클래스
 *
 * @author hrjin
 * @version 1.0
 * @since 2021.07.02
 **/
@Configuration
public class MinIOConfig {

    @Value("${minio.endPoint}")
    private String endPoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;

    @Value("${minio.bucketName}")
    private String bucketName;

    @Bean
    public MinioFileService minioFileService() {
        return new MinioFileService(endPoint, accessKey, secretKey, bucketName);
    }
}
