package com.msb.bpm.approval.appr.model.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationRepaymentEntity extends BaseEntity {

  @Basic
  @Column(name = "total_repay", table = "application_repayment")
  private BigDecimal totalRepay;

  @Basic
  @Column(name = "dti", table = "application_repayment")
  private Double dti;

  @Basic
  @Column(name = "dsr", table = "application_repayment")
  private Double dsr;

  @Basic
  @Column(name = "mue", table = "application_repayment")
  private BigDecimal mue;

  @Basic
  @Column(name = "evaluate", table = "application_repayment")
  private String evaluate;
}
