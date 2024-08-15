package com.msb.bpm.approval.appr.model.dto.authority;

import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationLimitCreditDTO;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 31/5/2023, Wednesday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class AuthorityRequestMapDTO {

  private Long applicationId;
  private String applicationBpmId;
  private String loanApprovalPosition;
  private String customerSegment;           // Đối tượng khách hàng
  private String approvalType;
  private String segment;                   // Phân khúc khách hàng
  private Boolean creditRiskType;
  private String customerType;
  private String submissionPurpose;
  private Set<ApplicationLimitCreditDTO> limitCredits;
  private Set<ApplicationAppraisalContentDTO> specialRiskContents;
  private Set<ApplicationAppraisalContentDTO> additionalContents;
}
