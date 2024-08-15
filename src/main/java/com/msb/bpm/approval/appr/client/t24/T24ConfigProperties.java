package com.msb.bpm.approval.appr.client.t24;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.t24-integration")
public class T24ConfigProperties {
  private String baseUrl;

  private Map<String, T24EndpointProperties> endpoint;
}
