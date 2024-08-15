package com.msb.bpm.approval.appr.client.general;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.general")
public class GeneralInfoConfigProperties {

    private String baseUrl;

    private Map<String, GeneralInfoEndpointProperties> endpoint;

}
