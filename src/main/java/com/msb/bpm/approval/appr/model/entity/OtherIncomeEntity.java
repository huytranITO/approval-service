package com.msb.bpm.approval.appr.model.entity;

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
@Table(name = "other_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@ToString
public class OtherIncomeEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpOtherId;

  @Basic
  @Column(name = "income_future")
  private boolean incomeFuture;

  @Basic
  @Column(name = "income_info")
  private String incomeInfo;

  @Basic
  @Column(name = "income_detail")
  private String incomeDetail;

  @Basic
  @Column(name = "income_detail_value")
  private String incomeDetailValue;

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
  @Column(name = "income_owner_name")
  private String incomeOwnerName;

  @Basic
  @Column(name = "income_owner_value")
  private String incomeOwnerValue;

  @Basic
  @Column(name = "income_type")
  private String incomeType;

  @Basic
  @Column(name = "income_type_value")
  private String incomeTypeValue;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @ManyToMany(mappedBy = "otherIncomes", cascade = CascadeType.ALL)
  private Set<ApplicationIncomeEntity> applicationIncomes = new HashSet<>();

  @Transient
  private String refCusId;      // ID khách hàng từ Landing Page

  @Transient
  private Long oldId;
}
