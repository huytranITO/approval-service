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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "application_limit_credit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationLimitCreditEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "loan_limit")
  private String loanLimit;

  @Basic
  @Column(name = "loan_limit_value")
  private String loanLimitValue;

  @Basic
  @Column(name = "loan_product_collateral")
  private BigDecimal loanProductCollateral;

  @Basic
  @Column(name = "other_loan_product_collateral")
  private BigDecimal otherLoanProductCollateral;

  @Basic
  @Column(name = "unsecure_product")
  private BigDecimal unsecureProduct;

  @Basic
  @Column(name = "other_unsecure_product")
  private BigDecimal otherUnsecureProduct;

  @Basic
  @Column(name = "total")
  private BigDecimal total;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;
}
