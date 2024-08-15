package com.msb.bpm.approval.appr.config;

import com.msb.bpm.approval.appr.config.properties.MinioProperties;
import io.minio.MinioAsyncClient;
import io.minio.MinioClient;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioConfig {

  private final MinioProperties minioProperties;

  @Bean
  public MinioClient minioClient() throws MalformedURLException {
    return MinioClient.builder()
        .endpoint(new URL(minioProperties.getMinio().getUrl()))
        .credentials(minioProperties.getMinio().getAccessKey(), minioProperties.getMinio()
        .getSecretKey())
        .build();
  }

  @Bean
  public MinioAsyncClient minioAsyncClient() throws MalformedURLException {
    return MinioAsyncClient.builder()
        .endpoint(new URL(minioProperties.getMinio().getUrl()))
        .credentials(minioProperties.getMinio().getAccessKey(),
            minioProperties.getMinio().getSecretKey())
        .build();
  }
}
