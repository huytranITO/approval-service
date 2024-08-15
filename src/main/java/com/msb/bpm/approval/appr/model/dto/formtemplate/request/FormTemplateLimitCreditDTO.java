package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import java.math.BigDecimal;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FormTemplateLimitCreditDTO {

  private Long id;

  private String loanLimit;

  private String loanLimitValue;

  private String loanProductCollateral;

  private String otherLoanProductCollateral;

  private String unsecureProduct;

  private String otherUnsecureProduct;

  private String total;

  private String orderDisplay;
}
