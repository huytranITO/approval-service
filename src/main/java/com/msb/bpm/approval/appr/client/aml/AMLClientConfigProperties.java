package com.msb.bpm.approval.appr.client.aml;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.aml")
public class AMLClientConfigProperties {

  private String baseUrl;

  private Map<String, AMLEndpointProperties> endpoint;

}
