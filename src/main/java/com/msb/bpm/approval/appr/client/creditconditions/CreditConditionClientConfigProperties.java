package com.msb.bpm.approval.appr.client.creditconditions;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.creditcondition")
public class CreditConditionClientConfigProperties {
	private String urlBase;

	private String urlCreditCondition;

	private String urlDeleteFlag;

	private String urlUpdateFlag;
	
	private String urlUpdateSource;
}
