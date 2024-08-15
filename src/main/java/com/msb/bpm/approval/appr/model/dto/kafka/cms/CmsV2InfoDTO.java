package com.msb.bpm.approval.appr.model.dto.kafka.cms;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
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
public class CmsV2InfoDTO {
  private String bpmId;

  private String status;

  private String landingPageId;

  private String note;

  private String source;

  private String name;

  private LocalDateTime updateDate;

  private String username;

  private String email;

  private String phone;

  private String transactionId;

  @JsonProperty(value = "packages")
  private PackageDTO packageObj;
}
