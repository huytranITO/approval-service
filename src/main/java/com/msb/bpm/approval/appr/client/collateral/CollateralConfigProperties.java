package com.msb.bpm.approval.appr.client.collateral;

import com.msb.bpm.approval.appr.client.aml.AMLEndpointProperties;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.collateral")
public class CollateralConfigProperties {

    private String baseUrl;

    private Map<String, CollateralEndpointProperties> endpoint;

    private AssetAllocationConfigProperties allocationConfig;

}
