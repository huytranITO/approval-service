package com.msb.bpm.approval.appr.model.dto.formtemplate;

import com.msb.bpm.approval.appr.model.dto.ApplicationCreditConditionsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationPhoneExpertiseDTO;
import com.msb.bpm.approval.appr.model.dto.OtherReviewDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateApplicationRepaymentDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateLimitCreditDTO;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Getter
@Setter
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
public class FormTemplateDebtInfoDTO {
  private boolean completed = Boolean.FALSE;
  private Set<FormTemplateLimitCreditDTO> limitCredits;
  private Set<FormTemplateCreditDTO> credits;
  private Set<ApplicationCreditRatingsDTO> creditRatings;
  private FormTemplateApplicationRepaymentDTO repayment;
  private Set<FormTemplateApplicationAppraisalContentDTO> specialRiskContents;
  private Set<FormTemplateApplicationAppraisalContentDTO> additionalContents;
  private Map<String, OtherReviewDTO> otherReviews;
  private Set<ApplicationCreditConditionsDTO> creditConditions;
  private ApplicationPhoneExpertiseDTO phoneExpertise;
  private Integer effectivePeriod;
  private String effectivePeriodUnit;
  private String proposalApprovalPosition; // Cấp TQ phê duyệt đề xuất
  private String loanApprovalPosition; // Cấp TQ phê duyệt khoản vay
  private Integer applicationAuthorityLevel; // Cấp độ ưu tiên của TQ phê duyệt khoản vay
  private String priorityAuthority; // Cấp độ TQ (cá nhân / hội đông) phê duyệt khoản vay
}
