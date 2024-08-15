package com.msb.bpm.approval.appr.model.dto.kafka.cms;

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
public class CmsInfoBaseDTO {

  private String tenantId;

  private String processId;

  private String status;

  private LocalDateTime timestamp;

  private CmsInfoDTO data;
}