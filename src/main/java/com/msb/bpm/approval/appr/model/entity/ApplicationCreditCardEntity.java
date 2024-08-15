package com.msb.bpm.approval.appr.model.entity;

import com.msb.bpm.approval.appr.mapper.ApplicationCreditMapper;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

@Entity
@Table(name = "application_credit_card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ApplicationCreditCardEntity extends AddressEntity {

  @GeneratedValue(strategy = GenerationType.AUTO)
  @Id
  @Column(name = "id")
  private Long id;

  private String ldpCardId;

  @Basic
  @Column(name = "card_policy_code")
  private String cardPolicyCode;

  @Basic
  @Column(name = "product_name")
  private String productName;

  @Basic
  @Column(name = "card_type")
  private String cardType;

  @Basic
  @Column(name = "card_type_value")
  private String cardTypeValue;

  @Basic
  @Column(name = "way4_card_code")
  private String way4CardCode;

  @Basic
  @Column(name = "way4_branch_code")
  private String way4BranchCode;

  @Basic
  @Column(name = "card_name")
  private String cardName;

  @Basic
  @Column(name = "secret_question")
  private String secretQuestion;

  @Basic
  @Column(name = "limit_sustentive_period")
  private Integer limitSustentivePeriod;

  @Basic
  @Column(name = "email")
  private String email;

  @Basic
  @Column(name = "auto_deduct_rate")
  private String autoDeductRate;

  @Basic
  @Column(name = "auto_deduct_rate_value")
  private String autoDeductRateValue;

  @Basic
  @Column(name = "deduct_account_number")
  private String deductAccountNumber;

  @Basic
  @Column(name = "card_form")
  private String cardForm;

  @Basic
  @Column(name = "card_form_value")
  private String cardFormValue;

  @Basic
  @Column(name = "card_receive_addr")
  private String cardReceiveAddress;

  @Basic
  @Column(name = "card_receive_addr_value")
  private String cardReceiveAddressValue;

  @OneToMany(mappedBy = "applicationCreditCard", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE}, orphanRemoval = true)
  private Set<SubCreditCardEntity> subCreditCards = new HashSet<>();

  public void addSubCreditCardEntities(Set<SubCreditCardEntity> subCreditCards) {
    subCreditCards.forEach(subCreditCard -> {
      subCreditCard.setApplicationCreditCard(this);
      this.subCreditCards.add(subCreditCard);
    });
  }

  public void updateSubCreditCardEntities(Set<SubCreditCardEntity> subCreditCards) {
    Map<Long, SubCreditCardEntity> subCreditCardEntityMap = subCreditCards.
        stream()
        .collect(Collectors.toMap(SubCreditCardEntity::getId, Function.identity()));

    Set<SubCreditCardEntity> removeSubCreditCards = new HashSet<>();

    this.subCreditCards.forEach(subCreditCard -> {
      if (subCreditCardEntityMap.containsKey(subCreditCard.getId())) {
        ApplicationCreditMapper.INSTANCE.referenceSubCreditCardEntity(subCreditCard, subCreditCardEntityMap.get(subCreditCard.getId()));
        subCreditCard.setApplicationCreditCard(this);
      } else {
        removeSubCreditCards.add(subCreditCard);
      }
    });

    if (CollectionUtils.isNotEmpty(removeSubCreditCards)) {
      removeSubCreditCardEntities(removeSubCreditCards);
    }
  }

  public void cmsUpdateSubCreditCardEntities(Set<SubCreditCardEntity> subCreditCards) {
    Map<Long, SubCreditCardEntity> subCreditCardEntityMap = subCreditCards.
        stream()
        .collect(Collectors.toMap(SubCreditCardEntity::getId, Function.identity()));

    Set<SubCreditCardEntity> removeSubCreditCards = new HashSet<>();

    this.subCreditCards.forEach(subCreditCard -> {
      if (subCreditCardEntityMap.containsKey(subCreditCard.getId())) {
        ApplicationCreditMapper.INSTANCE.cmsReferenceSubCreditCardEntity(subCreditCard, subCreditCardEntityMap.get(subCreditCard.getId()));
        subCreditCard.setApplicationCreditCard(this);
      } else {
        removeSubCreditCards.add(subCreditCard);
      }
    });

    if (CollectionUtils.isNotEmpty(removeSubCreditCards)) {
      removeSubCreditCardEntities(removeSubCreditCards);
    }
  }

  private void removeSubCreditCardEntities(Set<SubCreditCardEntity> removeSubCreditCards) {
    removeSubCreditCards.forEach(subCreditCard -> {
      subCreditCard.setApplicationCreditCard(null);
      this.subCreditCards.remove(subCreditCard);
    });
  }

  @Basic
  @Column(name = "contract_l")
  private String contractL;

  @Basic
  @Column(name = "issuing_contract")
  private String issuingContract;

  @Basic
  @Column(name = "contract_number")
  private String contractNumber;

  @Basic
  @Column(name = "created_date")
  private LocalDateTime createdDate;
}
