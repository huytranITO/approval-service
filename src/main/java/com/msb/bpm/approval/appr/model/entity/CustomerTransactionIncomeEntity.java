package com.msb.bpm.approval.appr.model.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "customer_transaction_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class CustomerTransactionIncomeEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  @Basic
  @Column(name = "transaction_time")
  private LocalDate transactionTime;

  @Basic
  @Column(name = "asset")
  private String asset;

  @Basic
  @Column(name = "transaction_value")
  private BigDecimal transactionValue;

  @Basic
  @Column(name = "purchase_cost")
  private BigDecimal purchaseCost;

  @Basic
  @Column(name = "brokerage_cost")
  private BigDecimal brokerageCost;

  @Basic
  @Column(name = "transfer_name_cost")
  private BigDecimal transferNameCost;

  @Basic
  @Column(name = "profit")
  private BigDecimal profit;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @JoinColumn(name = "property_business_income_id", referencedColumnName = "id")
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST,
          CascadeType.REMOVE}, fetch = FetchType.LAZY)
  private PropertyBusinessIncomeEntity propertyBusinessIncome;
}
