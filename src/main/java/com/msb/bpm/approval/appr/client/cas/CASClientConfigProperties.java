package com.msb.bpm.approval.appr.client.cas;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.cas")
public class CASClientConfigProperties {

  private String baseUrl;

  private String userAuthen;

  private String password;

  private Map<String, CASEndpointProperties> endpoint;

}
