package com.msb.bpm.approval.appr.client.loanproposal;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "approval-service.client.loanproposal")
public class LoanProposalConfigProperties {

  private String baseUrl;

  private Map<String, LoanProposalEndpointProperties> endpoint;

}
