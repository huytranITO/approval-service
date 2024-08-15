package com.msb.bpm.approval.appr.model.dto;

import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@With
public class ApplicationApprovalHistoryDTO {

  private Long id;

  private String fullName;

  private String username;

  private LocalDateTime executedAt;

  private String userRole;

  private Set<String> reasons;

  private String stepCode;

  private String stepDescription;

  private String proposalApprovalReception;

  private String proposalApprovalUser;

  private String fromUserRole;
}
