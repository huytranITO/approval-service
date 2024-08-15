package com.msb.bpm.approval.appr.model.dto;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.DEFAULT_SOURCE;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
public class ApplicationDTO {

  @Size(max = 100)
  private String refId;

  @Size(max = 36)
  private String bpmId;

  @Size(max = 20)
  private String source = DEFAULT_SOURCE;

  @NotBlank
  @Size(max = 6)
  private String segment;

  @Size(max = 50)
  private String createdBy;

  @NotBlank
  @Size(max = 20)
  private String approvalType;

  @NotBlank
  @Size(max = 10)
  private String processFlow;

  @Size(max = 100)
  private String processFlowValue;

  @NotBlank
  @Size(max = 10)
  private String submissionPurpose;

  @Size(max = 100)
  private String submissionPurposeValue;

  private String riskLevel;

  private String businessCode;

  @Size(max = 255)
  private String businessUnit;

  @Size(max = 100)
  private String partnerCode;

  private LocalDateTime createdAt;

  @Size(max = 255)
  private String createdFullName;

  @Size(max = 20)
  private String createdPhoneNumber;

  @Size(max = 50)
  private String assignee;

  @Size(max = 255)
  private String proposalApprovalFullName;

  @Size(max = 20)
  private String proposalApprovalPhoneNumber;

  @Size(max = 10)
  private String status;

  private String generatorStatus;

  @Size(max = 100)
  private String area;

  @Size(max = 100)
  private String region;

  private BigDecimal suggestedAmount;

  @Size(max = 10)
  private String distributionForm;

  @Size(max = 4000)
  private String regulatoryCode;

  @Size(max = 20)
  private String processingRole;

  @Size(max = 50)
  private String updatedBy;

  @Size(max = 100)
  private String areaCode;

  @Size(max = 100)
  private String regionCode;

  private String dealCode;

  private boolean rmStatus;

  private boolean rmCommitStatus;

}
