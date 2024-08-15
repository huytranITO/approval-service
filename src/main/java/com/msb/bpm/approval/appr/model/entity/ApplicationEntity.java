package com.msb.bpm.approval.appr.model.entity;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.DEFAULT_SOURCE;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.OPR_ASSET_TAG;

import com.msb.bpm.approval.appr.enums.application.ProcessingRole;
import com.msb.bpm.approval.appr.enums.common.SourceApplication;
import com.msb.bpm.approval.appr.mapper.ApplicationAppraisalContentMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditConditionMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationFieldInformationMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationLimitCreditMapper;
import com.msb.bpm.approval.appr.mapper.ApplicationPhoneExpertiseMapper;
import com.msb.bpm.approval.appr.model.dto.AmlOprDTO.AmlOprDtl;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditRatingsDTO;
import com.msb.bpm.approval.appr.model.dto.collateral.ApplicationAssetAllocationDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

@Entity
@Table(name = "application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
@SecondaryTable(name = "application_repayment", pkJoinColumns = @PrimaryKeyJoinColumn(name = "application_id", referencedColumnName = "id"))
public class ApplicationEntity extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "bpm_id")
  private String bpmId;

  @Column(name = "ref_id")
  private String refId;

  @Column(name = "total_income")
  private BigDecimal totalIncome;

  @Column(name = "suggested_amount")
  private BigDecimal suggestedAmount;

  @Column(name = "approval_type")
  private String approvalType;

  @Column(name = "process_flow")
  private String processFlow;

  @Column(name = "process_flow_value")
  private String processFlowValue;

  @Column(name = "submission_purpose")
  private String submissionPurpose;

  @Column(name = "submission_purpose_value")
  private String submissionPurposeValue;

  @Column(name = "segment")
  private String segment;

  @Column(name = "assignee")
  private String assignee;

  @Column(name = "risk_level")
  private String riskLevel;

  @Column(name = "business_unit_code")
  private String businessCode;

  @Column(name = "business_unit")
  private String businessUnit;

  @Column(name = "branch_code")
  private String branchCode;

  @Column(name = "branch_name")
  private String branchName;

  @Column(name = "region_code")
  private String regionCode;

  @Column(name = "region")
  private String region;

  @Column(name = "source")
  private String source = DEFAULT_SOURCE;

  @Column(name = "status")
  private String status;

  @Column(name = "processing_step_code")
  private String processingStepCode;

  private String generatorStatus;

  @Column(name = "processing_step")
  private String processingStep;

  @Column(name = "processing_role")
  private String processingRole;

  @Column(name = "previous_role")
  private String previousRole;

  @Column(name = "give_back_role")
  @Enumerated(EnumType.STRING)
  private ProcessingRole givebackRole;

  @Column(name = "regulatory_code")
  private String regulatoryCode;

  @Column(name = "distribution_form_code")
  private String distributionFormCode;

  @Column(name = "distribution_form")
  private String distributionForm;

  @Column(name = "effective_period")
  private Integer effectivePeriod;

  @Column(name = "effective_period_unit")
  private String effectivePeriodUnit;

  @Column(name = "proposal_approval_position")
  private String proposalApprovalPosition;            // Cấp TQ phê duyệt đề xuất

  @Column(name = "proposal_approval_user")
  private String proposalApprovalUser;                // CB tiếp nhận yêu cầu

  @Column(name = "loan_approval_position")
  private String loanApprovalPosition;                // Cấp TQ phê duyệt khoản vay

  @Column(name = "loan_approval_position_value")
  private String loanApprovalPositionValue;

  @Column(name = "partner_code")
  private String partnerCode;

  @Column(name = "created_full_name")
  private String createdFullName;

  @Column(name = "created_phone_number")
  private String createdPhoneNumber;

  @Column(name = "proposal_approval_full_name")
  private String proposalApprovalFullName;            // Họ & tên CB tiếp nhận phê duyệt hồ sơ

  @Column(name = "proposal_approval_phone_number")
  private String proposalApprovalPhoneNumber;         // SĐT CB tiếp nhận phê duyệt hồ sơ

  @Column(name = "area_code")
  private String areaCode;

  @Column(name = "area")
  private String area;

  @Column(name = "application_authority_level")
  private Integer applicationAuthorityLevel;          // Cấp độ ưu tiên của TQ phê duyệt khoản vay

  @Column(name = "priority_authority")
  private String priorityAuthority;                   // Cấp độ TQ (cá nhân | hội đông) của TQ phê duyệt khoản vay


  @Column(name = "approval_document_no")
  private String approvalDocumentNo;

  @Column(name = "sale_code")
  private String saleCode;

  @Column(name = "deal_code")
  private String dealCode;

  @Column(name = "transaction_id")
  private String transactionId;

  @Column(name = "reception_at")
  private LocalDateTime receptionAt;

  @Column(name = "ldp_status")
  private String ldpStatus;

  @Basic
  @Column(name = "insurance")
  private boolean insurance;

  @Basic
  @Column(name = "rm_status")
  private boolean rmStatus;

  @Basic
  @Column(name = "rm_commit_status")
  private boolean rmCommitStatus;

  @Transient
  private Long oldId;

  @Basic
  @Column(name = "reference_request_code")
  private String referenceRequestCode; //RequestCode của hồ sơ được chỉnh sửa

  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE})
  @JoinColumn(name = "process_instance_id", referencedColumnName = "id")
  private ProcessInstanceEntity processInstance;

  @OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE})
  @JoinColumn(name = "customer_id", referencedColumnName = "id")
  private CustomerEntity customer;

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationHistoricIntegration> applicationHistoricIntegration = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<AmlOprEntity> amlOprs = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationAppraisalContentEntity> appraisalContents = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationCreditConditionsEntity> creditConditions = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationCreditEntity> credits = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationCreditRatingsEntity> creditRatings = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationExtraDataEntity> extraDatas = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationFieldInformationEntity> fieldInformations = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationHistoryApprovalEntity> historyApprovals = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationHistoryFeedbackEntity> historyFeedbacks = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationIncomeEntity> incomes = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationLimitCreditEntity> limitCredits = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationPhoneExpertiseEntity> phoneExpertises = new HashSet<>();

  @Embedded
  private ApplicationRepaymentEntity repayment;

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<ApplicationContactEntity> contact = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE,
      CascadeType.REMOVE}, orphanRemoval = true)
  private Set<CicEntity> cics = new HashSet<>();

  @OneToMany(mappedBy = "application", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  private Set<RuleVersionMappingEntity> ruleVersionMappings = new HashSet<>();

  @Embedded
  private Set<ApplicationAssetAllocationDTO> assetAllocations = new HashSet<>(); // Phan bo tai san

  public void addApplicationIncomes(Set<ApplicationIncomeEntity> addIncomes) {
    addIncomes.forEach(income -> {
      income.addPropertyBusinessIncome(income.getPropertyBusinessIncomes());
      income.setApplication(this);
      this.incomes.add(income);
    });
  }

  public void updateApplicationIncomes(Set<ApplicationIncomeEntity> updateIncomes) {
    Map<Long, ApplicationIncomeEntity> incomeEntityMap = updateIncomes
        .stream()
        .collect(Collectors.toMap(ApplicationIncomeEntity::getId, Function.identity()));

    Set<ApplicationIncomeEntity> incomeRemoveSet = new HashSet<>();
    this.incomes.forEach(income -> {
      if (incomeEntityMap.containsKey(income.getId())) {

        // Mapping base income info
        ApplicationIncomeEntity incomeEntity = incomeEntityMap.get(income.getId());
        ApplicationIncomeMapper.INSTANCE.referenceApplicationIncomeEntity(income, incomeEntity);

        // Mapping salary income
        Set<SalaryIncomeEntity> createSalaries = incomeEntity.getSalaryIncomes()
            .stream()
            .filter(salary -> Objects.isNull(salary.getId()))
            .collect(Collectors.toSet());
        Set<SalaryIncomeEntity> updateSalaries = incomeEntity.getSalaryIncomes()
            .stream()
            .filter(salary -> Objects.nonNull(salary.getId()))
            .collect(Collectors.toSet());
        income.updateSalaryIncome(updateSalaries);
        income.addSalaryIncome(createSalaries);

        // Mapping rental income
        Set<RentalIncomeEntity> createRentals = incomeEntity.getRentalIncomes()
            .stream()
            .filter(rental -> Objects.isNull(rental.getId()))
            .collect(Collectors.toSet());
        Set<RentalIncomeEntity> updateRentals = incomeEntity.getRentalIncomes()
            .stream()
            .filter(rental -> Objects.nonNull(rental.getId()))
            .collect(Collectors.toSet());
        income.updateRentalIncome(updateRentals);
        income.addRentalIncome(createRentals);

        // Mapping individual or enterprise business income
        Set<IndividualEnterpriseIncomeEntity> createIndividualEnterprises = incomeEntity.getIndividualEnterpriseIncomes()
            .stream()
            .filter(individualEnterprise -> Objects.isNull(individualEnterprise.getId()))
            .collect(Collectors.toSet());
        Set<IndividualEnterpriseIncomeEntity> updateIndividualEnterprises = incomeEntity.getIndividualEnterpriseIncomes()
            .stream()
            .filter(individualEnterprise -> Objects.nonNull(individualEnterprise.getId()))
            .collect(Collectors.toSet());
        income.updateIndividualEnterpriseIncome(updateIndividualEnterprises);
        income.addIndividualEnterpriseIncome(createIndividualEnterprises);

        // Mapping other income
        Set<OtherIncomeEntity> createOthers = incomeEntity.getOtherIncomes()
            .stream()
            .filter(other -> Objects.isNull(other.getId()))
            .collect(Collectors.toSet());
        Set<OtherIncomeEntity> updateOthers = incomeEntity.getOtherIncomes()
            .stream()
            .filter(other -> Objects.nonNull(other.getId()))
            .collect(Collectors.toSet());
        income.updateOtherIncome(updateOthers);
        income.addOtherIncome(createOthers);

        // property business income
        Set<PropertyBusinessIncomeEntity> propertyBusinessCreate =
                incomeEntity.getPropertyBusinessIncomes()
                        .stream()
                        .filter(propertyBusiness -> Objects.isNull(propertyBusiness.getId()))
                        .collect(Collectors.toSet());
        Set<PropertyBusinessIncomeEntity> propertyBusinessUpdate=
                incomeEntity.getPropertyBusinessIncomes()
                        .stream()
                        .filter(propertyBusiness -> Objects.nonNull(propertyBusiness.getId()))
                        .collect(Collectors.toSet());
        income.updatePropertyBusinessIncome(propertyBusinessUpdate);
        income.addPropertyBusinessIncome(propertyBusinessCreate);
        // Mapping income evaluation
        if (ObjectUtils.isNotEmpty(income.getIncomeEvaluation()) &&
            ObjectUtils.isNotEmpty(incomeEntity.getIncomeEvaluation())) {
          income.updateIncomeEvaluation(incomeEntity.getIncomeEvaluation());
        } else if (ObjectUtils.isEmpty(income.getIncomeEvaluation()) &&
            ObjectUtils.isNotEmpty(incomeEntity.getIncomeEvaluation())) {
          income.setIncomeEvaluation(incomeEntity.getIncomeEvaluation());
        } else if (ObjectUtils.isNotEmpty(income.getIncomeEvaluation()) &&
        ObjectUtils.isEmpty(incomeEntity.getIncomeEvaluation())) {
          income.setIncomeEvaluation(null);
        }

        income.setApplication(this);
      } else {
        incomeRemoveSet.add(income);
      }
    });

    if (CollectionUtils.isNotEmpty(incomeRemoveSet)) {
      removeApplicationIncomes(incomeRemoveSet);
    }
  }

  public void cmsUpdateApplicationIncomes(Set<ApplicationIncomeEntity> updateIncomes) {
    Map<Long, ApplicationIncomeEntity> incomeEntityMap = updateIncomes
        .stream()
        .collect(Collectors.toMap(ApplicationIncomeEntity::getId, Function.identity()));

    Set<ApplicationIncomeEntity> incomeRemoveSet = new HashSet<>();
    this.incomes.forEach(income -> {
      if (incomeEntityMap.containsKey(income.getId())) {

        // Mapping base income info
        ApplicationIncomeEntity incomeEntity = incomeEntityMap.get(income.getId());
        ApplicationIncomeMapper.INSTANCE.cmsReferenceApplicationIncomeEntity(income, incomeEntity);

        // Mapping salary income
        Set<SalaryIncomeEntity> createSalaries = incomeEntity.getSalaryIncomes()
            .stream()
            .filter(salary -> Objects.isNull(salary.getId()))
            .collect(Collectors.toSet());
        Set<SalaryIncomeEntity> updateSalaries = incomeEntity.getSalaryIncomes()
            .stream()
            .filter(salary -> Objects.nonNull(salary.getId()))
            .collect(Collectors.toSet());
        income.cmsUpdateSalaryIncome(updateSalaries);
        income.addSalaryIncome(createSalaries);

        // Mapping rental income
        Set<RentalIncomeEntity> createRentals = incomeEntity.getRentalIncomes()
            .stream()
            .filter(rental -> Objects.isNull(rental.getId()))
            .collect(Collectors.toSet());
        Set<RentalIncomeEntity> updateRentals = incomeEntity.getRentalIncomes()
            .stream()
            .filter(rental -> Objects.nonNull(rental.getId()))
            .collect(Collectors.toSet());
        income.cmsUpdateRentalIncome(updateRentals);
        income.addRentalIncome(createRentals);

        // Mapping individual or enterprise business income
        Set<IndividualEnterpriseIncomeEntity> createIndividualEnterprises = incomeEntity.getIndividualEnterpriseIncomes()
            .stream()
            .filter(individualEnterprise -> Objects.isNull(individualEnterprise.getId()))
            .collect(Collectors.toSet());
        Set<IndividualEnterpriseIncomeEntity> updateIndividualEnterprises = incomeEntity.getIndividualEnterpriseIncomes()
            .stream()
            .filter(individualEnterprise -> Objects.nonNull(individualEnterprise.getId()))
            .collect(Collectors.toSet());
        income.cmsUpdateIndividualEnterpriseIncome(updateIndividualEnterprises);
        income.addIndividualEnterpriseIncome(createIndividualEnterprises);

        // Mapping other income
        Set<OtherIncomeEntity> createOthers = incomeEntity.getOtherIncomes()
            .stream()
            .filter(other -> Objects.isNull(other.getId()))
            .collect(Collectors.toSet());
        Set<OtherIncomeEntity> updateOthers = incomeEntity.getOtherIncomes()
            .stream()
            .filter(other -> Objects.nonNull(other.getId()))
            .collect(Collectors.toSet());
        income.cmsUpdateOtherIncome(updateOthers);
        income.addOtherIncome(createOthers);

        // property business income
        Set<PropertyBusinessIncomeEntity> propertyBusinessCreate =
            incomeEntity.getPropertyBusinessIncomes()
                .stream()
                .filter(propertyBusiness -> Objects.isNull(propertyBusiness.getId()))
                .collect(Collectors.toSet());
        Set<PropertyBusinessIncomeEntity> propertyBusinessUpdate=
            incomeEntity.getPropertyBusinessIncomes()
                .stream()
                .filter(propertyBusiness -> Objects.nonNull(propertyBusiness.getId()))
                .collect(Collectors.toSet());
        income.cmsUpdatePropertyBusinessIncome(propertyBusinessUpdate);
        income.addPropertyBusinessIncome(propertyBusinessCreate);

        income.setApplication(this);
      } else {
        incomeRemoveSet.add(income);
      }
    });

    if (CollectionUtils.isNotEmpty(incomeRemoveSet)) {
      removeApplicationIncomes(incomeRemoveSet);
    }
  }

  public void removeApplicationIncomes(Set<ApplicationIncomeEntity> removeIncomes) {
    removeIncomes.forEach(income -> income.setApplication(null));
    this.incomes.removeAll(removeIncomes);
  }

  public void addApplicationAppraisalContents(
      Set<ApplicationAppraisalContentEntity> appraisalContents) {
    appraisalContents.forEach(appraisalContent -> {
      appraisalContent.setApplication(this);
      this.appraisalContents.add(appraisalContent);
    });
  }

  public void updateApplicationAppraisalContents(
      Set<ApplicationAppraisalContentEntity> appraisalContents) {
    Map<Long, ApplicationAppraisalContentEntity> appraisalContentMap = appraisalContents
        .stream()
        .collect(Collectors.toMap(ApplicationAppraisalContentEntity::getId, Function.identity()));

    Set<ApplicationAppraisalContentEntity> removeAppraisalContents = new HashSet<>();

    this.appraisalContents.forEach(appraisalContent -> {
      if (appraisalContentMap.containsKey(appraisalContent.getId())) {
        ApplicationAppraisalContentMapper.INSTANCE.referenceAppraisalContentEntity(appraisalContent,
            appraisalContentMap.get(appraisalContent.getId()));
        appraisalContent.setApplication(this);
      } else {
        removeAppraisalContents.add(appraisalContent);
      }
    });

    if (CollectionUtils.isNotEmpty(removeAppraisalContents)) {
      removeApplicationAppraisalContents(removeAppraisalContents);
    }
  }

  public void removeApplicationAppraisalContents(
      Set<ApplicationAppraisalContentEntity> appraisalContents) {
    appraisalContents.forEach(appraisalContent -> {
      appraisalContent.setApplication(null);
      this.appraisalContents.remove(appraisalContent);
    });
  }

  public void addCreditConditionsEntities(
      Set<ApplicationCreditConditionsEntity> createCreditConditionsEntities) {
    createCreditConditionsEntities.forEach(creditCond -> {
      creditCond.setApplication(this);
      this.creditConditions.add(creditCond);
    });
  }

  public void addContact(Set<ApplicationContactEntity> contactList) {
    removeAllContact();
    if (CollectionUtils.isNotEmpty(contactList)) {
      contactList.forEach(item -> this.contact.add(item));
    }
  }

  public void removeAllContact() {
    this.contact.clear();
  }

  public void updateCreditConditionsEntities(
      Set<ApplicationCreditConditionsEntity> updateCreditConditionsEntities) {
    Map<Long, ApplicationCreditConditionsEntity> creditConditionsEntityMap = updateCreditConditionsEntities
        .stream()
        .collect(Collectors.toMap(ApplicationCreditConditionsEntity::getId, Function.identity()));

    Set<ApplicationCreditConditionsEntity> removeCreditConditions = new HashSet<>();

    this.creditConditions.forEach(creditConditionsEntity -> {
      if (creditConditionsEntityMap.containsKey(creditConditionsEntity.getId())) {
        ApplicationCreditConditionMapper.INSTANCE.referenceCreditConditionsEntity(
            creditConditionsEntity, creditConditionsEntityMap.get(creditConditionsEntity.getId()));
        creditConditionsEntity.setApplication(this);
      } else {
        removeCreditConditions.add(creditConditionsEntity);
      }
    });

    if (CollectionUtils.isNotEmpty(removeCreditConditions)) {
      removeCreditConditionsEntities(removeCreditConditions);
    }
  }

  public void removeCreditConditionsEntities(
      Set<ApplicationCreditConditionsEntity> removeCreditConditionsEntities) {
    removeCreditConditionsEntities.forEach(creditConditionsEntity -> {
      creditConditionsEntity.setApplication(null);
      this.creditConditions.remove(creditConditionsEntity);
    });
  }

  public void addCreditEntities(Set<ApplicationCreditEntity> creditEntities) {
    creditEntities.forEach(creditEntity -> {
      creditEntity.setApplication(this);
      this.credits.add(creditEntity);
    });
  }

  public void updateCreditEntities(Set<ApplicationCreditEntity> creditEntities) {
    Map<Long, ApplicationCreditEntity> creditEntityMap = creditEntities
        .stream()
        .collect(Collectors.toMap(ApplicationCreditEntity::getId, Function.identity()));

    Set<ApplicationCreditEntity> removeCredits = new HashSet<>();

    this.credits.forEach(credit -> {
      if (creditEntityMap.containsKey(credit.getId())) {
        ApplicationCreditEntity creditEntity = creditEntityMap.get(credit.getId());
        ApplicationCreditMapper.INSTANCE.referenceCreditEntity(credit, creditEntity);

        if (Objects.nonNull(credit.getCreditCard())) {
          Set<SubCreditCardEntity> creates = creditEntity.getCreditCard().getSubCreditCards()
              .stream().filter(sub -> Objects.isNull(sub.getId())).collect(Collectors.toSet());
          Set<SubCreditCardEntity> updates = creditEntity.getCreditCard().getSubCreditCards()
              .stream().filter(sub -> Objects.nonNull(sub.getId())).collect(Collectors.toSet());
          credit.getCreditCard().updateSubCreditCardEntities(updates);
          credit.getCreditCard().addSubCreditCardEntities(creates);
        }

        credit.setApplication(this);
      } else {
        removeCredits.add(credit);
      }
    });

    if (CollectionUtils.isNotEmpty(removeCredits)) {
      removeCreditEntities(removeCredits);
    }
  }

  public void cmsUpdateCreditEntities(Set<ApplicationCreditEntity> creditEntities) {
    Map<Long, ApplicationCreditEntity> creditEntityMap = creditEntities
        .stream()
        .collect(Collectors.toMap(ApplicationCreditEntity::getId, Function.identity()));

    Set<ApplicationCreditEntity> removeCredits = new HashSet<>();

    this.credits.forEach(credit -> {
      if (creditEntityMap.containsKey(credit.getId())) {
        ApplicationCreditEntity creditEntity = creditEntityMap.get(credit.getId());
        ApplicationCreditMapper.INSTANCE.cmsReferenceCreditEntity(credit, creditEntity);

        if (Objects.nonNull(credit.getCreditCard())) {
          Set<SubCreditCardEntity> creates = creditEntity.getCreditCard().getSubCreditCards()
              .stream().filter(sub -> Objects.isNull(sub.getId())).collect(Collectors.toSet());
          Set<SubCreditCardEntity> updates = creditEntity.getCreditCard().getSubCreditCards()
              .stream().filter(sub -> Objects.nonNull(sub.getId())).collect(Collectors.toSet());
          credit.getCreditCard().cmsUpdateSubCreditCardEntities(updates);
          credit.getCreditCard().addSubCreditCardEntities(creates);
        }

        credit.setApplication(this);
      } else {
        removeCredits.add(credit);
      }
    });

    if (CollectionUtils.isNotEmpty(removeCredits)) {
      removeCreditEntities(removeCredits);
    }
  }

  public void removeCreditEntities(Set<ApplicationCreditEntity> removeCredits) {
    removeCredits.forEach(credit -> {
      credit.setApplication(null);
      this.credits.remove(credit);
    });
  }

  public void addPhoneExpertiseEntities(Set<ApplicationPhoneExpertiseEntity> phoneExpertises) {
    phoneExpertises.forEach(phoneExpertise -> {
      phoneExpertise.setApplication(this);
      this.phoneExpertises.add(phoneExpertise);
    });
  }

  public void updatePhoneExpertiseEntities(Set<ApplicationPhoneExpertiseEntity> phoneExpertises) {
    Map<Long, ApplicationPhoneExpertiseEntity> phoneExpertiseEntityMap = phoneExpertises
        .stream()
        .collect(Collectors.toMap(ApplicationPhoneExpertiseEntity::getId, Function.identity()));

    Set<ApplicationPhoneExpertiseEntity> removePhoneExpertises = new HashSet<>();

    this.phoneExpertises.forEach(phoneExpertise -> {
      if (phoneExpertiseEntityMap.containsKey(phoneExpertise.getId())) {
        ApplicationPhoneExpertiseMapper.INSTANCE.referencePhoneExpertiseEntity(phoneExpertise,
            phoneExpertiseEntityMap.get(phoneExpertise.getId()));
        phoneExpertise.setApplication(this);
      } else {
        removePhoneExpertises.add(phoneExpertise);
      }
    });

    if (CollectionUtils.isNotEmpty(removePhoneExpertises)) {
      removePhoneExpertiseEntities(removePhoneExpertises);
    }
  }

  public void removePhoneExpertiseEntities(Set<ApplicationPhoneExpertiseEntity> phoneExpertises) {
    phoneExpertises.forEach(phoneExpertise -> {
      phoneExpertise.setApplication(null);
      this.phoneExpertises.remove(phoneExpertise);
    });
  }

  public void addLimitCreditEntities(Set<ApplicationLimitCreditEntity> createLimitCreditEntities) {
    createLimitCreditEntities.forEach(limitCredit -> {
      limitCredit.setApplication(this);
      this.limitCredits.add(limitCredit);
    });
  }

  public void updateLimitCreditEntities(Set<ApplicationLimitCreditEntity> updateLimitCreditEntities) {
    Map<Long, ApplicationLimitCreditEntity> limitCreditEntityMap = updateLimitCreditEntities
        .stream()
        .collect(Collectors.toMap(ApplicationLimitCreditEntity::getId, Function.identity()));

    Set<ApplicationLimitCreditEntity> removeLimitCredits = new HashSet<>();

    this.limitCredits.forEach(limitCredit -> {
      if (limitCreditEntityMap.containsKey(limitCredit.getId())) {
        ApplicationLimitCreditMapper.INSTANCE.referenceLimitCreditEntity(limitCredit, limitCreditEntityMap.get(limitCredit.getId()));
        limitCredit.setApplication(this);
      } else {
        removeLimitCreditEntities(removeLimitCredits);
      }
    });
  }

  public void removeLimitCreditEntities(Set<ApplicationLimitCreditEntity> removeLimitCredits) {
    removeLimitCredits.forEach(limitCredit -> {
      limitCredit.setApplication(null);
      this.limitCredits.remove(limitCredit);
    });
  }

  public void addCicEntities(Set<CicEntity> cics) {
    cics.forEach(cic -> {
      cic.setApplication(this);
      this.cics.add(cic);
    });
  }

  public void updateCicEntities(Set<CicEntity> cics) {
    Map<Long, CicEntity> cicEntityMap = cics
        .stream()
        .collect(Collectors.toMap(CicEntity::getId, Function.identity()));

    Set<CicEntity> removeCics = new HashSet<>();
    this.cics.forEach(cic -> {
      if (cicEntityMap.containsKey(cic.getId())) {
        cic.setExplanation(cicEntityMap.get(cic.getId()).getExplanation());
        cic.setApplication(this);
      } else {
        removeCics.add(cic);
      }
    });

    if (CollectionUtils.isNotEmpty(removeCics)) {
      removeCicEntities(removeCics);
    }
  }

  private void removeCicEntities(Set<CicEntity> cics) {
    cics.forEach(cic -> {
      cic.setApplication(null);
      this.cics.remove(cic);
    });
  }

  public void removeAmlOprEntities(Set<AmlOprDtl> amlOprDtls) {
    Map<Long, AmlOprDtl> amlOprMap = amlOprDtls
        .stream()
        .collect(Collectors.toMap(AmlOprDtl::getId, Function.identity()));

    Set<AmlOprEntity> removeAmlOprEntities = new HashSet<>();
    this.amlOprs.stream()
        .filter(a -> !OPR_ASSET_TAG.equals(a.getQueryType()))
        .forEach(amlOpr -> {
          if (!amlOprMap.containsKey(amlOpr.getId())) {
            removeAmlOprEntities.add(amlOpr);
          }
    });

    if (CollectionUtils.isNotEmpty(removeAmlOprEntities)) {
      removeAmlOprEntities.forEach(amlOpr -> {
        amlOpr.setApplication(null);
        this.amlOprs.remove(amlOpr);
      });
    }
  }

  public void removeCreditRatingsEntities(Set<ApplicationCreditRatingsDTO> applicationCreditRatings) {
    Map<Long, ApplicationCreditRatingsDTO> creditRatingsMap = applicationCreditRatings
        .stream()
        .collect(Collectors.toMap(ApplicationCreditRatingsDTO::getId, Function.identity()));

    Set<ApplicationCreditRatingsEntity> removeCreditRatingsEntities = new HashSet<>();
    this.creditRatings.forEach(compareCreditRatings -> {
      if (!creditRatingsMap.containsKey(compareCreditRatings.getId())) {
        removeCreditRatingsEntities.add(compareCreditRatings);
      }
    });

    if (CollectionUtils.isNotEmpty(removeCreditRatingsEntities)) {
      removeCreditRatingsEntities.forEach(removeCreditRatings -> {
        removeCreditRatings.setApplication(null);
        this.creditRatings.remove(removeCreditRatings);
      });
    }
  }

  public void addFieldInformationEntities(Set<ApplicationFieldInformationEntity> fields) {
    fields.forEach(item -> {
      item.setApplication(this);
      this.fieldInformations.add(item);
    });
  }

  public void updateFieldInformationEntities(Set<ApplicationFieldInformationEntity> fields) {
    Map<Long, ApplicationFieldInformationEntity> fieldEntityMap = fields
            .stream()
            .collect(Collectors.toMap(ApplicationFieldInformationEntity::getId, Function.identity()));

    Set<ApplicationFieldInformationEntity> removeFields = new HashSet<>();

    this.fieldInformations.forEach(item -> {
      if (fieldEntityMap.containsKey(item.getId())) {
        ApplicationFieldInformationMapper.INSTANCE.referenceFieldInformationEntity(item, fieldEntityMap.get(item.getId()));
        item.setApplication(this);
      } else {
        removeFields.add(item);
      }
    });

    if (CollectionUtils.isNotEmpty(removeFields)) {
      removeFieldInformationEntities(removeFields);
    }
  }

  private void removeFieldInformationEntities(Set<ApplicationFieldInformationEntity> fields) {
    fields.forEach(item -> {
      if (item.getAddressLinkId().startsWith(SourceApplication.LDP.name())) {
        item.setApplication(null);
        this.fieldInformations.remove(item);
      }
    });
  }


  public void removeExtraData(ApplicationExtraDataEntity item) {
        item.setApplication(null);
        this.extraDatas.remove(item);
  }
}
