package com.msb.bpm.approval.appr.model.entity;

import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Entity
@Table(name = "application_credit_loan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationCreditLoanEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpLoanId;

  @Basic
  @Column(name = "product_code")
  private String productCode;

  @Basic
  @Column(name = "product_name")
  private String productName;

  @Basic
  @Column(name = "payback")
  private boolean payback;

  @Basic
  @Column(name = "loan_purpose")
  private String loanPurpose;

  @Basic
  @Column(name = "loan_purpose_value")
  private String loanPurposeValue;

  @Basic
  @Column(name = "loan_purpose_explanation")
  private String loanPurposeExplanation;

  @Basic
  @Column(name = "loan_period")
  private Integer loanPeriod;

  @Basic
  @Column(name = "kunn_period")
  private Integer kunnPeriod;

  @Basic
  @Column(name = "original_period")
  private Integer originalPeriod;

  @Basic
  @Column(name = "total_capital")
  private BigDecimal totalCapital;

  @Basic
  @Column(name = "equity_capital")
  private BigDecimal equityCapital;

  @Basic
  @Column(name = "ltd")
  private Integer ltd;

  @Basic
  @Column(name = "credit_form")
  private String creditForm;

  @Basic
  @Column(name = "credit_form_value")
  private String creditFormValue;

  @Basic
  @Column(name = "disburse_frequency")
  private String disburseFrequency;

  @Basic
  @Column(name = "disburse_frequency_value")
  private String disburseFrequencyValue;

  @Basic
  @Column(name = "debt_pay_method")
  private String debtPayMethod;

  @Basic
  @Column(name = "debt_pay_method_value")
  private String debtPayMethodValue;

  @Basic
  @Column(name = "disburse_method")
  private String disburseMethod;

  @Basic
  @Column(name = "disburse_method_value")
  private String disburseMethodValue;

  @Basic
  @Column(name = "disburse_method_explanation")
  private String disburseMethodExplanation;

  @Basic
  @Column(name = "principal_pay_period")
  private Integer principalPayPeriod;

  @Basic
  @Column(name = "principal_pay_unit")
  private String principalPayUnit;

  @Basic
  @Column(name = "principal_pay_unit_value")
  private String principalPayUnitValue;

  @Basic
  @Column(name = "interest_pay_period")
  private Integer interestPayPeriod;

  @Basic
  @Column(name = "interest_pay_unit")
  private String interestPayUnit;

  @Basic
  @Column(name = "interest_pay_unit_value")
  private String interestPayUnitValue;

  @Basic
  @Column(name = "acf_no")
  private String acfNo;

  @Basic
  @Column(name = "account_no")
  private String accountNo;

  @Basic
  @Column(name = "status")
  private String status;

  @Basic
  @Column(name = "product_info_code")
  private String productInfoCode;

  @Basic
  @Column(name = "product_info_name")
  private String productInfoName;
}
