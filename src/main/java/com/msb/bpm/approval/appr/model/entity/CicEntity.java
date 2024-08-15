package com.msb.bpm.approval.appr.model.entity;

import java.util.Map;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class CicEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "customer_id")
  private Long customerId;

  @Basic
  @Column(name = "ref_customer_id")
  private Long refCustomerId;

  @Basic
  @Column(name = "relationship")
  private String relationship;

  @Basic
  @Column(name = "deft_group_current")
  private String deftGroupCurrent;

  @Basic
  @Column(name = "deft_group_last_12")
  private String deftGroupLast12;

  @Basic
  @Column(name = "deft_group_last_24")
  private String deftGroupLast24;

  @Basic
  @Column(name = "total_loan_collateral")
  private BigDecimal totalLoanCollateral;

  @Basic
  @Column(name = "total_loan_collateral_usd")
  private BigDecimal totalLoanCollateralUSD;

  @Basic
  @Column(name = "total_unsecure_loan")
  private BigDecimal totalUnsecureLoan;

  @Basic
  @Column(name = "total_unsecure_loan_usd")
  private BigDecimal totalUnsecureLoanUSD;

  @Basic
  @Column(name = "total_credit_card_limit")
  private BigDecimal totalCreditCardLimit;

  @Basic
  @Column(name = "total_debt_card_limit")
  private BigDecimal totalDebtCardLimit;

  @Basic
  @Column(name = "status_code")
  private String statusCode;

  @Basic
  @Column(name = "status_description")
  private String statusDescription;

  @Basic
  @Column(name = "explanation")
  private String explanation;

  @Basic
  @Column(name = "query_at")
  private LocalDateTime queryAt;

  @Basic
  @Column(name = "subject")
  private String subject;

  @Basic
  @Column(name = "identifier_code")
  private String identifierCode;

  @Basic
  @Column(name = "cic_code")
  private String cicCode;

  @Basic
  @Column(name = "client_question_id")
  private Long clientQuestionId;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @Basic
  @Column(name = "pdf_data")
  private String pdfDate;

  @Basic
  @Column(name = "customer_type")
  private String customerType;

  @Basic
  @Column(name = "product_code")
  private String productCode;

  @Basic
  @Column(name = "cic_customer_name")
  private String cicCustomerName;

  @Basic
  @Column(name = "pdf_status")
  private boolean pdfStatus;

  @Basic
  @Column(name = "cic_indicator_status")
  private boolean cicIndicatorStatus;

  @Basic
  @Column(name = "error_code_sbv")
  private String errorCodeSBV;

}
