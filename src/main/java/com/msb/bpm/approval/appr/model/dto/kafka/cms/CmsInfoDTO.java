package com.msb.bpm.approval.appr.model.dto.kafka.cms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CmsInfoDTO {

  private String bpmId;

  private String status;

  private String landingPageId;

  private String note;

  @JsonProperty(value = "packages")
  private PackageDTO packageObj;
}
