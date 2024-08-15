package com.msb.bpm.approval.appr.client.legacy;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.legacy-css")
public class LegacyClientConfigProperties {

  private String baseUrl;

  private String userAuthen;

  private String password;

  private Map<String, LegacyEndpointProperties> endpoint;
}
