package com.msb.bpm.approval.appr.model.dto.cic;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class CicGroupDetail {

  private Long refCustomerId;

  private String deftGroupCurrent;

  private String deftGroupLast12;

  private String deftGroupLast24;

  private BigDecimal totalLoanCollateral;

  private BigDecimal totalLoanCollateralUSD;

  private BigDecimal totalUnsecureLoan;

  private BigDecimal totalUnsecureLoanUSD;

  private BigDecimal totalCreditCardLimit;

  private BigDecimal totalDebtCardLimit;

  private String customerType;

  private String productCode;

  private String statusCode;

  private String identifierCode;

  private String subject;

}
