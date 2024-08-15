package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.DEFAULT_SOURCE;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@With
@ToString
public class FormTemplateApplicationDTO implements Serializable {

  @Size(max = 36)
  private String refId = "null";

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

  private String riskLevel = "null";

  @Size(max = 255)
  private String businessUnit;

  @Size(max = 100)
  private String partnerCode = "null";

  private LocalDateTime createdAt;

  @Size(max = 255)
  private String createdFullName;

  @Size(max = 20)
  private String createdPhoneNumber;

  @Size(max = 50)
  private String assignee;

  @Size(max = 255)
  private String proposalApprovalFullName = "null";

  @Size(max = 20)
  private String proposalApprovalPhoneNumber = "null";

  @Size(max = 10)
  private String status;

  private String generatorStatus;

  @Size(max = 100)
  private String area;

  @Size(max = 100)
  private String region;

  private String suggestedAmount;

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
  private String documentNo = "null";
}
