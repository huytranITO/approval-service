package com.msb.bpm.approval.appr.client.cic;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.cic")
public class CICClientConfigProperties {

  private String baseUrl;

  private String baseUrlIntegration;

  private Map<String, CICEndpointProperties> endpoint;

}
