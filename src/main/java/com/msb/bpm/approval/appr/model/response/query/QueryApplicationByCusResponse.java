package com.msb.bpm.approval.appr.model.response.query;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import java.time.LocalDateTime;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryApplicationByCusResponse {

  private String idBpm;
  private String cif;
  private String customerName;
  private String processingStep;
  private String status;
  @CreatedDate
  private LocalDateTime createdAt;
  private String regulatoryCode;
  private String proposalApprovalFullName;
  private String proposalApprovalUser;
  private String identity;
  private String processingRole;
}
