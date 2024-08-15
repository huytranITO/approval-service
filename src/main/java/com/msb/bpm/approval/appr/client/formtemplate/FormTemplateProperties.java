package com.msb.bpm.approval.appr.client.formtemplate;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "approval-service.client.formtemplate")
public class FormTemplateProperties {

  private String urlBase;

  private String urlGenerateTemplates;
  private String urlGenerateTemplateByCode;
}
