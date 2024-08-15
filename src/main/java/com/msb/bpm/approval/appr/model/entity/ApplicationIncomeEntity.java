package com.msb.bpm.approval.appr.model.entity;

import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.mapper.IncomeEvaluationMapper;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

@Entity
@Table(name = "application_income")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationIncomeEntity extends BaseEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpIncomeId;

  @ManyToOne
  @JoinColumn(name = "application_id", referencedColumnName = "id")
  private ApplicationEntity application;

  @Basic
  @Column(name = "income_recognition_method")
  private String incomeRecognitionMethod;

  @Basic
  @Column(name = "income_recognition_method_value")
  private String incomeRecognitionMethodValue;

  @Basic
  @Column(name = "recognized_income")
  private BigDecimal recognizedIncome;

  @Basic
  @Column(name = "currency")
  private String currency;

  @Basic
  @Column(name = "conversion_method")
  private String conversionMethod;

  @Basic
  @Column(name = "conversion_method_value")
  private String conversionMethodValue;

  @Basic
  @Column(name = "explanation")
  private String explanation;

  @Basic
  @Column(name = "order_display")
  private Integer orderDisplay;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "salary_income_mapping",
      joinColumns = {@JoinColumn(name = "application_income_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "salary_income_id", referencedColumnName = "id")}
  )
  @ToString.Exclude
  private Set<SalaryIncomeEntity> salaryIncomes = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "individual_enterprise_income_mapping",
      joinColumns = {@JoinColumn(name = "application_income_id", referencedColumnName = "id")},
      inverseJoinColumns = {
          @JoinColumn(name = "individual_enterprise_income_id", referencedColumnName = "id")}
  )
  @ToString.Exclude
  private Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "other_income_mapping",
      joinColumns = {@JoinColumn(name = "application_income_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "other_income_id", referencedColumnName = "id")}
  )
  @ToString.Exclude
  private Set<OtherIncomeEntity> otherIncomes = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "rental_income_mapping",
      joinColumns = {@JoinColumn(name = "application_income_id", referencedColumnName = "id")},
      inverseJoinColumns = {@JoinColumn(name = "rental_income_id", referencedColumnName = "id")}
  )
  @ToString.Exclude
  private Set<RentalIncomeEntity> rentalIncomes = new HashSet<>();

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(name = "property_business_income_mapping",
          joinColumns = {@JoinColumn(name = "application_income_id", referencedColumnName = "id")},
          inverseJoinColumns = {@JoinColumn(name = "property_business_income_id", referencedColumnName = "id")}
  )
  @ToString.Exclude
  private Set<PropertyBusinessIncomeEntity> propertyBusinessIncomes = new HashSet<>();

  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE})
  @JoinColumn(name = "income_evaluation_id", referencedColumnName = "id")
  private IncomeEvaluationEntity incomeEvaluation;

  public void addSalaryIncome(Set<SalaryIncomeEntity> salaryIncomes) {
    salaryIncomes.forEach(salaryIncome -> {
      this.salaryIncomes.add(salaryIncome);
      salaryIncome.getApplicationIncomes().add(this);
    });
  }

  public void updateSalaryIncome(Set<SalaryIncomeEntity> salaryIncomes) {
    Map<Long, SalaryIncomeEntity> salaryIncomeMap = salaryIncomes
        .stream()
        .collect(Collectors.toMap(SalaryIncomeEntity::getId, Function.identity()));

    Set<SalaryIncomeEntity> salaryIncomeRemoveSet = new HashSet<>();
    this.salaryIncomes.forEach(salaryIncome -> {
      if (salaryIncomeMap.containsKey(salaryIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.referenceSalaryIncomeEntity(salaryIncome,
            salaryIncomeMap.get(salaryIncome.getId()));
        salaryIncome.getApplicationIncomes().add(this);
      } else {
        salaryIncomeRemoveSet.add(salaryIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(salaryIncomeRemoveSet)) {
      removeSalaryIncome(salaryIncomeRemoveSet);
    }
  }

  public void cmsUpdateSalaryIncome(Set<SalaryIncomeEntity> salaryIncomes) {
    Map<Long, SalaryIncomeEntity> salaryIncomeMap = salaryIncomes
        .stream()
        .collect(Collectors.toMap(SalaryIncomeEntity::getId, Function.identity()));

    Set<SalaryIncomeEntity> salaryIncomeRemoveSet = new HashSet<>();
    this.salaryIncomes.forEach(salaryIncome -> {
      if (salaryIncomeMap.containsKey(salaryIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.cmsReferenceSalaryIncomeEntity(salaryIncome,
            salaryIncomeMap.get(salaryIncome.getId()));
        salaryIncome.getApplicationIncomes().add(this);
      } else {
        salaryIncomeRemoveSet.add(salaryIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(salaryIncomeRemoveSet)) {
      removeSalaryIncome(salaryIncomeRemoveSet);
    }
  }

  public void removeSalaryIncome(Set<SalaryIncomeEntity> salaryIncomes) {
    salaryIncomes.forEach(salaryIncome -> salaryIncome.getApplicationIncomes().remove(this));
    this.salaryIncomes.removeAll(salaryIncomes);
  }

  public void addRentalIncome(Set<RentalIncomeEntity> rentalIncomes) {
    rentalIncomes.forEach(rentalIncome -> {
      this.rentalIncomes.add(rentalIncome);
      rentalIncome.getApplicationIncomes().add(this);
    });
  }

  public void updateRentalIncome(Set<RentalIncomeEntity> rentalIncomes) {
    Map<Long, RentalIncomeEntity> rentalIncomeMap = rentalIncomes
        .stream()
        .collect(Collectors.toMap(RentalIncomeEntity::getId, Function.identity()));

    Set<RentalIncomeEntity> rentalIncomeRemoveSet = new HashSet<>();
    this.rentalIncomes.forEach(rentalIncome -> {
      if (rentalIncomeMap.containsKey(rentalIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.referenceRentalIncomeEntity(rentalIncome,
            rentalIncomeMap.get(rentalIncome.getId()));
        rentalIncome.getApplicationIncomes().add(this);
      } else {
        rentalIncomeRemoveSet.add(rentalIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(rentalIncomeRemoveSet)) {
      removeRentalIncome(rentalIncomeRemoveSet);
    }
  }

  public void cmsUpdateRentalIncome(Set<RentalIncomeEntity> rentalIncomes) {
    Map<Long, RentalIncomeEntity> rentalIncomeMap = rentalIncomes
        .stream()
        .collect(Collectors.toMap(RentalIncomeEntity::getId, Function.identity()));

    Set<RentalIncomeEntity> rentalIncomeRemoveSet = new HashSet<>();
    this.rentalIncomes.forEach(rentalIncome -> {
      if (rentalIncomeMap.containsKey(rentalIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.cmsReferenceRentalIncomeEntity(rentalIncome,
            rentalIncomeMap.get(rentalIncome.getId()));
        rentalIncome.getApplicationIncomes().add(this);
      } else {
        rentalIncomeRemoveSet.add(rentalIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(rentalIncomeRemoveSet)) {
      removeRentalIncome(rentalIncomeRemoveSet);
    }
  }

  public void removeRentalIncome(Set<RentalIncomeEntity> rentalIncomes) {
    rentalIncomes.forEach(rentalIncome -> rentalIncome.getApplicationIncomes().remove(this));
    this.rentalIncomes.removeAll(rentalIncomes);
  }

  public void addIndividualEnterpriseIncome(
      Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes) {
    individualEnterpriseIncomes.forEach(individualEnterpriseIncome -> {
      this.individualEnterpriseIncomes.add(individualEnterpriseIncome);
      individualEnterpriseIncome.getApplicationIncomes().add(this);
    });
  }

  public void updateIndividualEnterpriseIncome(
      Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes) {
    Map<Long, IndividualEnterpriseIncomeEntity> individualEnterpriseIncomeMap = individualEnterpriseIncomes
        .stream()
        .collect(Collectors.toMap(IndividualEnterpriseIncomeEntity::getId, Function.identity()));

    Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomeRemoveSet = new HashSet<>();
    this.individualEnterpriseIncomes.forEach(individualEnterpriseIncome -> {
      if (individualEnterpriseIncomeMap.containsKey(individualEnterpriseIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.referenceIndividualEnterpriseIncomeEntity(
            individualEnterpriseIncome,
            individualEnterpriseIncomeMap.get(individualEnterpriseIncome.getId()));
        individualEnterpriseIncome.getApplicationIncomes().add(this);
      } else {
        individualEnterpriseIncomeRemoveSet.add(individualEnterpriseIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(individualEnterpriseIncomeRemoveSet)) {
      removeIndividualIncome(individualEnterpriseIncomeRemoveSet);
    }
  }

  public void cmsUpdateIndividualEnterpriseIncome(
      Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes) {
    Map<Long, IndividualEnterpriseIncomeEntity> individualEnterpriseIncomeMap = individualEnterpriseIncomes
        .stream()
        .collect(Collectors.toMap(IndividualEnterpriseIncomeEntity::getId, Function.identity()));

    Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomeRemoveSet = new HashSet<>();
    this.individualEnterpriseIncomes.forEach(individualEnterpriseIncome -> {
      if (individualEnterpriseIncomeMap.containsKey(individualEnterpriseIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.cmsReferenceIndividualEnterpriseIncomeEntity(
            individualEnterpriseIncome,
            individualEnterpriseIncomeMap.get(individualEnterpriseIncome.getId()));
        individualEnterpriseIncome.getApplicationIncomes().add(this);
      } else {
        individualEnterpriseIncomeRemoveSet.add(individualEnterpriseIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(individualEnterpriseIncomeRemoveSet)) {
      removeIndividualIncome(individualEnterpriseIncomeRemoveSet);
    }
  }

  public void removeIndividualIncome(
      Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes) {
    individualEnterpriseIncomes.forEach(individualEnterpriseIncome -> individualEnterpriseIncome.getApplicationIncomes().remove(this));
    this.individualEnterpriseIncomes.removeAll(individualEnterpriseIncomes);
  }

  public void addOtherIncome(Set<OtherIncomeEntity> otherIncomes) {
    otherIncomes.forEach(otherIncome -> {
      this.otherIncomes.add(otherIncome);
      otherIncome.getApplicationIncomes().add(this);
    });
  }

  public void updateOtherIncome(Set<OtherIncomeEntity> otherIncomes) {
    Map<Long, OtherIncomeEntity> otherIncomeMap = otherIncomes
        .stream()
        .collect(Collectors.toMap(OtherIncomeEntity::getId, Function.identity()));

    Set<OtherIncomeEntity> otherIncomeRemoveSet = new HashSet<>();
    this.otherIncomes.forEach(otherIncome -> {
      if (otherIncomeMap.containsKey(otherIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.referenceOtherIncomeEntity(otherIncome,
            otherIncomeMap.get(otherIncome.getId()));
        otherIncome.getApplicationIncomes().add(this);
      } else {
        otherIncomeRemoveSet.add(otherIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(otherIncomeRemoveSet)) {
      removeOtherIncome(otherIncomeRemoveSet);
    }
  }

  public void cmsUpdateOtherIncome(Set<OtherIncomeEntity> otherIncomes) {
    Map<Long, OtherIncomeEntity> otherIncomeMap = otherIncomes
        .stream()
        .collect(Collectors.toMap(OtherIncomeEntity::getId, Function.identity()));

    Set<OtherIncomeEntity> otherIncomeRemoveSet = new HashSet<>();
    this.otherIncomes.forEach(otherIncome -> {
      if (otherIncomeMap.containsKey(otherIncome.getId())) {
        ApplicationIncomeMapper.INSTANCE.cmsReferenceOtherIncomeEntity(otherIncome,
            otherIncomeMap.get(otherIncome.getId()));
        otherIncome.getApplicationIncomes().add(this);
      } else {
        otherIncomeRemoveSet.add(otherIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(otherIncomeRemoveSet)) {
      removeOtherIncome(otherIncomeRemoveSet);
    }
  }

  public void removeOtherIncome(Set<OtherIncomeEntity> otherIncomes) {
    otherIncomes.forEach(otherIncome -> otherIncome.getApplicationIncomes().remove(this));
    this.otherIncomes.removeAll(otherIncomes);
  }

  public void addPropertyBusinessIncome(Set<PropertyBusinessIncomeEntity> propertyBusinessIncomes) {
    propertyBusinessIncomes.forEach(propertyBusinessIncome -> {
      this.propertyBusinessIncomes.add(propertyBusinessIncome);
      propertyBusinessIncome.getApplicationIncomes().add(this);
      propertyBusinessIncome.addCustomerTransactionIncome(propertyBusinessIncome.getCustomerTransactionIncomes());
    });
  }

  public void updatePropertyBusinessIncome(Set<PropertyBusinessIncomeEntity> propertyBusinessIncomes) {
    Map<Long, PropertyBusinessIncomeEntity> propertyBusinessIncomeMap = propertyBusinessIncomes
            .stream()
            .collect(Collectors.toMap(PropertyBusinessIncomeEntity::getId, Function.identity()));

    Set<PropertyBusinessIncomeEntity> propertyBusinessIncomeRemoveSet = new HashSet<>();
    this.propertyBusinessIncomes.forEach( propertyBusinessIncome-> {
      if (propertyBusinessIncomeMap.containsKey(propertyBusinessIncome.getId())) {
        // Get entity request
        PropertyBusinessIncomeEntity propertyBusinessIncomeReq = propertyBusinessIncomeMap.get(propertyBusinessIncome.getId());

        // Mapping base
        ApplicationIncomeMapper.INSTANCE.referencePropertyBusinessIncomeEntity(propertyBusinessIncome,
                propertyBusinessIncomeMap.get(propertyBusinessIncome.getId()));
        propertyBusinessIncome.getApplicationIncomes().add(this);

        Set<CustomerTransactionIncomeEntity> customerTransactionUpdate = propertyBusinessIncomeReq.getCustomerTransactionIncomes()
                .stream()
                .filter(c -> Objects.nonNull(c.getId()))
                .collect(Collectors.toSet());
        Set<CustomerTransactionIncomeEntity> customerTransactionCreate = propertyBusinessIncomeReq.getCustomerTransactionIncomes()
                .stream()
                .filter(c -> Objects.isNull(c.getId()))
                .collect(Collectors.toSet());
        propertyBusinessIncome.updateCustomerTransactionIncome(customerTransactionUpdate);
        propertyBusinessIncome.addCustomerTransactionIncome(customerTransactionCreate);
      } else {
        propertyBusinessIncomeRemoveSet.add(propertyBusinessIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(propertyBusinessIncomeRemoveSet)) {
      removePropertyBusinessIncome(propertyBusinessIncomeRemoveSet);
    }
  }

  public void cmsUpdatePropertyBusinessIncome(Set<PropertyBusinessIncomeEntity> propertyBusinessIncomes) {
    Map<Long, PropertyBusinessIncomeEntity> propertyBusinessIncomeMap = propertyBusinessIncomes
        .stream()
        .collect(Collectors.toMap(PropertyBusinessIncomeEntity::getId, Function.identity()));

    Set<PropertyBusinessIncomeEntity> propertyBusinessIncomeRemoveSet = new HashSet<>();
    this.propertyBusinessIncomes.forEach( propertyBusinessIncome-> {
      if (propertyBusinessIncomeMap.containsKey(propertyBusinessIncome.getId())) {
        // Get entity request
        PropertyBusinessIncomeEntity propertyBusinessIncomeReq = propertyBusinessIncomeMap.get(propertyBusinessIncome.getId());

        // Mapping base
        ApplicationIncomeMapper.INSTANCE.cmsReferencePropertyBusinessIncomeEntity(propertyBusinessIncome,
            propertyBusinessIncomeMap.get(propertyBusinessIncome.getId()));
        propertyBusinessIncome.getApplicationIncomes().add(this);

        Set<CustomerTransactionIncomeEntity> customerTransactionUpdate = propertyBusinessIncomeReq.getCustomerTransactionIncomes()
            .stream()
            .filter(c -> Objects.nonNull(c.getId()))
            .collect(Collectors.toSet());
        Set<CustomerTransactionIncomeEntity> customerTransactionCreate = propertyBusinessIncomeReq.getCustomerTransactionIncomes()
            .stream()
            .filter(c -> Objects.isNull(c.getId()))
            .collect(Collectors.toSet());
        propertyBusinessIncome.updateCustomerTransactionIncome(customerTransactionUpdate);
        propertyBusinessIncome.addCustomerTransactionIncome(customerTransactionCreate);
      } else {
        propertyBusinessIncomeRemoveSet.add(propertyBusinessIncome);
      }
    });

    if (CollectionUtils.isNotEmpty(propertyBusinessIncomeRemoveSet)) {
      removePropertyBusinessIncome(propertyBusinessIncomeRemoveSet);
    }
  }

  public void removePropertyBusinessIncome(Set<PropertyBusinessIncomeEntity> propertyBusinessIncomes) {
    propertyBusinessIncomes.forEach(propertyBusinessIncome -> {
      this.propertyBusinessIncomes.remove(propertyBusinessIncome);
      propertyBusinessIncome.getApplicationIncomes().remove(this);
    });
  }
  public void updateIncomeEvaluation(IncomeEvaluationEntity incomeEvaluation) {
    IncomeEvaluationMapper.INSTANCE.toEntity(this.incomeEvaluation, incomeEvaluation);
    if (CollectionUtils.isNotEmpty(incomeEvaluation.getTotalAssetIncomes())){
      Set<TotalAssetIncomeEntity> totalAssetIncomeCreate =
        incomeEvaluation.getTotalAssetIncomes()
          .stream()
          .filter(totalAssetIncome -> Objects.isNull(totalAssetIncome.getId()))
          .collect(Collectors.toSet());
      Set<TotalAssetIncomeEntity> totalAssetIncomeUpdate =
      incomeEvaluation.getTotalAssetIncomes()
          .stream()
          .filter(totalAssetIncome -> Objects.nonNull(totalAssetIncome.getId()))
          .collect(Collectors.toSet());
      this.incomeEvaluation.updateTotalAssetIncome(totalAssetIncomeUpdate);
      this.incomeEvaluation.createTotalAssetIncome(totalAssetIncomeCreate);
    }
  }
}
