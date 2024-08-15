package com.msb.bpm.approval.appr.model.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Entity
@Table(name = "individual_enterprise_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class IndividualEnterpriseIncomeEntity extends AddressEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpBusinessId;

  @Basic
  @Column(name = "business_registration_number")
  private String businessRegistrationNumber;

  @Basic
  @Column(name = "company_name")
  private String companyName;

  @Basic
  @Column(name = "main_business_sector")
  private String mainBusinessSector;

  @Basic
  @Column(name = "capital_contribution_rate")
  private Double capitalContributionRate;

  @Column(name = "business_place_ownership")
  private String businessPlaceOwnership;

  @Column(name = "business_place_ownership_value")
  private String businessPlaceOwnershipValue;

  @Basic
  @Column(name = "production_process")
  private String productionProcess;

  @Basic
  @Column(name = "record_method")
  private String recordMethod;

  @Basic
  @Column(name = "input")
  private String input;

  @Basic
  @Column(name = "output")
  private String output;

  @Basic
  @Column(name = "business_scale")
  private String businessScale;

  @Basic
  @Column(name = "inventory")
  private String inventory;

  @Basic
  @Column(name = "evaluation_period")
  private String evaluationPeriod;

  @Basic
  @Column(name = "evaluation_period_value")
  private String evaluationPeriodValue;

  @Basic
  @Column(name = "income_monthly")
  private BigDecimal incomeMonthly;

  @Basic
  @Column(name = "expense_monthly")
  private BigDecimal expenseMonthly;

  @Basic
  @Column(name = "profit_monthly")
  private BigDecimal profitMonthly;

  @Basic
  @Column(name = "profit_margin")
  private Double profitMargin;

  @Basic
  @Column(name = "evaluate_result")
  private String evaluateResult;

  @Basic
  @Column(name = "customer_id")
  private Long customerId;

  @Basic
  @Column(name = "ref_customer_id")
  private Long refCustomerId;

  @Basic
  @Column(name = "income_owner")
  private String incomeOwner;

  @Basic
  @Column(name = "income_owner_value")
  private String incomeOwnerValue;

  @Basic
  @Column(name = "income_owner_name")
  private String incomeOwnerName;

  @Basic
  @Column(name = "income_type")
  private String incomeType;

  @Basic
  @Column(name = "income_type_value")
  private String incomeTypeValue;

  @Basic
  @Column(name = "address_link_id")
  private String addressLinkId;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @Basic
  @Column(name = "business_experience")
  private Integer businessExperience;

  @ManyToMany(mappedBy = "individualEnterpriseIncomes", cascade = CascadeType.ALL)
  private Set<ApplicationIncomeEntity> applicationIncomes = new HashSet<>();

  @Transient
  private String refCusId;      // ID khách hàng từ Landing Page

  @Transient
  private Long oldId;

  @Basic
  @Column(name = "business_ref_customer_id")
  private Long businessRefCustomerId;
}