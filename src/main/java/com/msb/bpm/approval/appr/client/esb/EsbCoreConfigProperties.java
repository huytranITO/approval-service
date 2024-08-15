package com.msb.bpm.approval.appr.client.esb;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.esb-core")
@RefreshScope
public class EsbCoreConfigProperties {

  private String baseUrl;

  private Map<String, EsbCoreEndpointProperties> endpoint;

}
