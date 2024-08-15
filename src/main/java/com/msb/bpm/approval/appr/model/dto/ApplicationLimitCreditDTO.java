package com.msb.bpm.approval.appr.model.dto;

import java.math.BigDecimal;
import java.util.Objects;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationLimitCreditDTO {

  private Long id;

  @Size(max = 45)
  private String loanLimit;

  @Size(max = 100)
  private String loanLimitValue;

  private BigDecimal loanProductCollateral;

  private BigDecimal otherLoanProductCollateral;

  private BigDecimal unsecureProduct;

  private BigDecimal otherUnsecureProduct;

  private BigDecimal total;

  private Integer orderDisplay;

  public BigDecimal calculateTotal() {
    BigDecimal newLoanProductCollateral =
        Objects.nonNull(loanProductCollateral) ? loanProductCollateral : BigDecimal.ZERO;
    BigDecimal newOtherLoanProductCollateral =
        Objects.nonNull(otherLoanProductCollateral) ? otherLoanProductCollateral : BigDecimal.ZERO;
    BigDecimal newUnsecureProduct =
        Objects.nonNull(unsecureProduct) ? unsecureProduct : BigDecimal.ZERO;
    BigDecimal newOtherUnsecureProduct =
        Objects.nonNull(otherUnsecureProduct) ? otherUnsecureProduct : BigDecimal.ZERO;
    return newLoanProductCollateral.add(newOtherLoanProductCollateral).add(newUnsecureProduct)
        .add(newOtherUnsecureProduct);
  }
}
