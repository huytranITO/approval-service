package com.msb.bpm.approval.appr.model.entity;

import java.time.LocalDate;
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
import lombok.ToString;
import lombok.With;

@Entity
@Table(name = "salary_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@ToString
public class SalaryIncomeEntity extends AddressEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpSalaryId;

  @Basic
  @Column(name = "tax_code")
  private String taxCode;

  @Basic
  @Column(name = "social_insurance_code")
  private String socialInsuranceCode;

  @Basic
  @Column(name = "rank_type")
  private String rankType;

  @Basic
  @Column(name = "rank_type_value")
  private String rankTypeValue;

  @Basic
  @Column(name = "kpi_rating")
  private String kpiRating;

  @Basic
  @Column(name = "kpi_rating_value")
  private String kpiRatingValue;

  @Basic
  @Column(name = "pay_type")
  private String payType;

  @Basic
  @Column(name = "pay_type_value")
  private String payTypeValue;

  @Basic
  @Column(name = "labor_type")
  private String laborType;

  @Basic
  @Column(name = "labor_type_value")
  private String laborTypeValue;

  @Basic
  @Column(name = "business_registration_number")
  private String businessRegistrationNumber;

  @Basic
  @Column(name = "group_of_working")
  private String groupOfWorking;

  @Basic
  @Column(name = "company_name")
  private String companyName;

  @Basic
  @Column(name = "position")
  private String position;

  @Basic
  @Column(name = "start_working_day")
  private LocalDate startWorkingDay;

  @Basic
  @Column(name = "explanation")
  private String explanation;

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
  @Column(name = "phone_work")
  private String phoneWork;

  @ManyToMany(mappedBy = "salaryIncomes", cascade = CascadeType.ALL)
  private Set<ApplicationIncomeEntity> applicationIncomes = new HashSet<>();

  @Transient
  private String refCusId;      // ID khách hàng từ Landing Page

  @Transient
  private Long oldId;
}
