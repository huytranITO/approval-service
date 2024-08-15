package com.msb.bpm.approval.appr.model.dto.kafka.cms;

import com.msb.bpm.approval.appr.model.dto.feedback.ApplicationFbDTO;
import com.msb.bpm.approval.appr.model.dto.feedback.CommentFbDTO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class CmsV2InfoBaseDTO {

  private String refId;
  private String bpmId;
  private String transactionID;
  private String source;
  private String status;
  private String name;
  private List<String> regulatoryCode = new ArrayList<>();
  private String username;
  private String fullnameRM;
  private LocalDateTime responseDate;
  private String email;
  private String phone;
  private String note;
  private List<CmsLoanApplicationApprovedDTO> loanApplicationApproved;
  private ApplicationFbDTO applicationData;
  private CommentFbDTO commentData;
  private String processingRole;
  private String previousRole;
}
