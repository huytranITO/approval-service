package com.msb.bpm.approval.appr.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.oprisk")
public class OpriskPersonConfig {

  private String authorizer;
  private String password;
  private String srvperson;
  private String srvlegal;
  private String regApp;
  private String url;
  private Asset asset;
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Asset {
    private String authorizerOprAsset;
    private String passwordOprAsset;
    private String srvLegalOprAsset;
    private String regAppOprAsset;
  }
}
