package com.msb.bpm.approval.appr.mqtt.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateFormTemplateContent {

  private String applicationId;
  private String processingUserName;

}
