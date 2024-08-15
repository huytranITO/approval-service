package com.msb.bpm.approval.appr.client.asset;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "approval-service.client.asset")
public class AssetClientConfigProperties {

  private String baseUrl;

  private Map<String, AssetEndpointProperties> endpoint;

}
