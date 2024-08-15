package com.msb.bpm.approval.appr.model.request.bpm.operation;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.math.BigDecimal;
import java.util.List;

@With
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoanInfo {
  private BigDecimal amount;
  private String cif;
  private Long approvalLoanId;
  private Integer kunnTime;
  private BigDecimal limitAmount;
  private List<String> products;
  private String purpose;
  private Boolean requestDrafting;
  private Integer tenorOfDebtContract;
  private String type;
  private String typeOfCreditOffer;
  private String typeOfDisbursements;
  private Integer holdOnTime;
  private Integer approvalMinutesActiveTime;
  private Integer principalPaymentFrequency;
  private Integer interestPaymentFrequency;
  private Integer graceTime;
  private Integer index;
  private String name;
  private String disbursementsNo;
  private String purposeCode;
  private String productCode;
  private String productName;
  private String productInfo;
  private String productInfoValue;
  private String paymentMethod;
  private String paymentMethodValue;
}
