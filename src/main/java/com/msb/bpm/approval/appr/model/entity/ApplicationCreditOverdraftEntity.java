package com.msb.bpm.approval.appr.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
@Table(name = "application_credit_overdraft")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationCreditOverdraftEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpOverdraftId;

  @Basic
  @Column(name = "interest_rate_code")
  private String interestRateCode;

  @Basic
  @Column(name = "product_name")
  private String productName;

  @Basic
  @Column(name = "loan_purpose")
  private String loanPurpose;

  @Basic
  @Column(name = "loan_purpose_value")
  private String loanPurposeValue;

  @Basic
  @Column(name = "limit_sustentive_period")
  private Integer limitSustentivePeriod;

  @Basic
  @Column(name = "debt_pay_method")
  private String debtPayMethod;

  @Basic
  @Column(name = "debt_pay_method_value")
  private String debtPayMethodValue;

  @Basic
  @Column(name = "info_additional")
  private String infoAdditional;

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
