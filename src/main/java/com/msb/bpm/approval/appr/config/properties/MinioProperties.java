package com.msb.bpm.approval.appr.config.properties;


import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(value = "storage")
public class MinioProperties {
  private Minio minio;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Minio {
    private String url;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private Map<String, Bucket> bucketExt;
    private int expiry;
    private String filePathBase;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Bucket {
    private String bucket;
    private String prefix;
  }
}
