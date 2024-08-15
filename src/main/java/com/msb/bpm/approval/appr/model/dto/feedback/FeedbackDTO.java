package com.msb.bpm.approval.appr.model.dto.feedback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.msb.bpm.approval.appr.model.dto.kafka.cms.CmsLoanApplicationApprovedDTO;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FeedbackDTO implements Serializable {

  private String bpmId;
  private String refId;
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
  Object data;
}
