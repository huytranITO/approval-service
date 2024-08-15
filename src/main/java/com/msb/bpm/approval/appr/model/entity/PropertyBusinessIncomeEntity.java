package com.msb.bpm.approval.appr.model.entity;

import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.FetchType;
import javax.persistence.CascadeType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity
@Table(name = "property_business_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class PropertyBusinessIncomeEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpPropertyBusinessId;

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
  @Column(name = "business_experience")
  private Integer businessExperience;

  @Basic
  @Column(name = "experience_time")
  private String experienceTime;

  @Basic
  @Column(name = "accumulate_asset")
  private String accumulateAsset;

  @Basic
  @Column(name = "business_scale")
  private String businessScale;

  @Basic
  @Column(name = "income_base")
  private String incomeBase;

  @Basic
  @Column(name = "basis_income")
  private String basisIncome;

  @Basic
  @Column(name = "income_assessment")
  private String incomeAssessment;

  @Basic
  @Column(name = "business_plan")
  private String businessPlan;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @Transient
  private Long oldId;

  @OneToMany(mappedBy = "propertyBusinessIncome", cascade = {CascadeType.MERGE, CascadeType.PERSIST,
          CascadeType.REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<CustomerTransactionIncomeEntity> customerTransactionIncomes = new HashSet<>();

  @ManyToMany(mappedBy = "propertyBusinessIncomes", cascade = CascadeType.ALL)
  private Set<ApplicationIncomeEntity> applicationIncomes = new HashSet<>();

  public void addCustomerTransactionIncome(Set<CustomerTransactionIncomeEntity> customerTransactionIncomes) {
    customerTransactionIncomes.forEach(customerTransactionIncome -> {
      this.customerTransactionIncomes.add(customerTransactionIncome);
      customerTransactionIncome.setPropertyBusinessIncome(this);
    });
  }

  public void updateCustomerTransactionIncome(Set<CustomerTransactionIncomeEntity> customerTransactionIncomes) {
    Map<Long, CustomerTransactionIncomeEntity> customerTransactionIncomeMap = customerTransactionIncomes
            .stream()
            .collect(Collectors.toMap(CustomerTransactionIncomeEntity::getId, Function.identity()));

    Set<CustomerTransactionIncomeEntity> customerTransactionIncomeRemoveSet = new HashSet<>();
    this.customerTransactionIncomes.forEach( customerTransactionIncome-> {
      if (customerTransactionIncomeMap.containsKey(customerTransactionIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.referenceCustomerTransactionIncomeEntity(customerTransactionIncome,
                customerTransactionIncomeMap.get(customerTransactionIncome.getId()));
        customerTransactionIncome.setPropertyBusinessIncome(this);
      } else {
        customerTransactionIncomeRemoveSet.add(customerTransactionIncome);
      }
    });

    if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(customerTransactionIncomeRemoveSet)) {
      removeCustomerTransactionIncome(customerTransactionIncomeRemoveSet);
    }
  }

  public void removeCustomerTransactionIncome(Set<CustomerTransactionIncomeEntity> customerTransactionIncomes) {
    customerTransactionIncomes.forEach(customerTransactionIncome -> {
      this.customerTransactionIncomes.remove(customerTransactionIncome);
      customerTransactionIncome.setPropertyBusinessIncome(null);
    });
  }
}
