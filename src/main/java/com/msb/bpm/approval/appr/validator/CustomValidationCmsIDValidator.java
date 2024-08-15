package com.msb.bpm.approval.appr.validator;

import static com.msb.bpm.approval.appr.enums.application.CreditType.CARD;

import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseIncomeItemDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerAddressDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerIdentityDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCustomerRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsEnterpriseRelationDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsSubCardDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CustomerRelationDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 27/8/2023, Sunday
 **/
public class CustomValidationCmsIDValidator implements ConstraintValidator<CmsIDConstraint, Object> {

  private String object;

  @Override
  public void initialize(CmsIDConstraint constraintAnnotation) {
    this.object = constraintAnnotation.object();
  }

  @Override
  public boolean isValid(Object o, ConstraintValidatorContext context) {
    switch (object) {
      case "customer":
        return validCountForCustomer(o, context) < 1;
      case "customerRelations":
        return validCountForCustomerRelations(o, context) < 1;
      case "enterpriseRelations":
        return validCountForEnterpriseRelations(o, context) < 1;
      case "applicationIncomes":
        return validCountForIncomes(o, context) < 1;
      case "applicationCredits":
        return validCountForCredits(o, context) < 1;
      default:
        break;
    }
    return false;
  }

  private int validCountForCustomer(Object o, ConstraintValidatorContext context) {
    CmsCustomerDTO customer = (CmsCustomerDTO) new BeanWrapperImpl(o).getPropertyValue(object);
    if (customer == null) {
      return 0;
    }

    List<CmsCustomerIdentityDTO> identities = customer.getIdentities();
    List<CmsCustomerAddressDTO> addresses = customer.getAddresses();

    int countFail = validCountIdentities(object, identities, null, context);
    countFail += validCountAddress(object, addresses, null, context);

    return countFail;
  }

  private int validCountForCustomerRelations(Object o, ConstraintValidatorContext context) {
    List<CmsCustomerRelationDTO> customerRelations = (List<CmsCustomerRelationDTO>) new BeanWrapperImpl(
        o).getPropertyValue(object);

    if (CollectionUtils.isEmpty(customerRelations)) {
      return 0;
    }

    int countFail = 0;
    int index = 0;

    CmsCustomerDTO customer = (CmsCustomerDTO) new BeanWrapperImpl(o).getPropertyValue("customer");

    String parentText = object + "[%s]";

    List<String> refCusIds = new ArrayList<>();

    for (CustomerRelationDTO customerRelation : customerRelations) {
      assert customer != null;
      if (customerRelation.getRefCusId().equals(customer.getRefCusId())) {
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
            .addPropertyNode(object + "[" + index + "].refCusId")
            .addConstraintViolation();
        countFail++;
      }

      if (refCusIds.contains(customerRelation.getRefCusId())) {
        context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
            .addPropertyNode(object + "[" + index + "].refCusId")
            .addConstraintViolation();
        countFail++;
      }

      refCusIds.add(customerRelation.getRefCusId());

      countFail += validCountIdentities(String.format(parentText, index),
          customerRelation.getIdentities(), customer.getIdentities(), context);

      countFail += validCountAddress(String.format(parentText, index),
          customerRelation.getAddresses(), customer.getAddresses(), context);

      index++;
    }

    return countFail;
  }

  private int validCountForEnterpriseRelations(Object o, ConstraintValidatorContext context) {
    List<CmsEnterpriseRelationDTO> enterpriseRelations = (List<CmsEnterpriseRelationDTO>) new BeanWrapperImpl(
        o).getPropertyValue(object);

    if (CollectionUtils.isEmpty(enterpriseRelations)) {
      return 0;
    }

    int countFail = 0;
    int index = 0;

    List<String> refEnterpriseId = new ArrayList<>();
    for (CmsEnterpriseRelationDTO enterpriseRelation : enterpriseRelations) {
      if (refEnterpriseId.contains(enterpriseRelation.getRefEnterpriseId())) {
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate())
            .addPropertyNode(object + "[" + index + "].refEnterpriseId")
            .addConstraintViolation();
        countFail++;
      }
      refEnterpriseId.add(enterpriseRelation.getRefEnterpriseId());
      index++;
    }

    return countFail;
  }

  private int validCountForIncomes(Object o, ConstraintValidatorContext context) {
    List<CmsIncomeDTO> incomes = (List<CmsIncomeDTO>) new BeanWrapperImpl(
        o).getPropertyValue(object);

    if (CollectionUtils.isEmpty(incomes)) {
      return 0;
    }

    int countFail = 0;
    int index = 0;

    List<String> refIncomeId = new ArrayList<>();
    List<PairKey> pairKeys = new ArrayList<>();
    for (CmsIncomeDTO income : incomes) {
      if (refIncomeId.contains(income.getRefIncomeId())) {
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate())
            .addPropertyNode(object + "[" + index + "].refIncomeId")
            .addConstraintViolation();
        countFail++;
      }

      int childIndex = 0;
      for (CmsBaseIncomeItemDTO incomeItem : income.getIncomeItems()) {
        if (!isValidCountIncomeItemId(pairKeys, incomeItem)) {
          context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
              .addPropertyNode(object + "[" + index + "].incomeItems[" + childIndex + "].refIncomeItemId")
              .addConstraintViolation();
        }
        pairKeys.add(new PairKey(incomeItem.getIncomeType(), incomeItem.getRefIncomeItemId()));
        childIndex++;
      }

      refIncomeId.add(income.getRefIncomeId());
      index++;
    }

    return countFail;
  }

  private int validCountForCredits(Object o, ConstraintValidatorContext context) {
    List<CmsBaseCreditDTO> credits = (List<CmsBaseCreditDTO>) new BeanWrapperImpl(
        o).getPropertyValue(object);

    if (CollectionUtils.isEmpty(credits)) {
      return 0;
    }

    int countFail = 0;
    int index = 0;

    List<String> refCreditId = new ArrayList<>();
    for (CmsBaseCreditDTO credit : credits) {
      if (refCreditId.contains(credit.getRefCreditId())) {
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate())
            .addPropertyNode(object + "[" + index + "].refCreditId")
            .addConstraintViolation();
        countFail++;
      }
      refCreditId.add(credit.getRefCreditId());

      countFail += validCountSubCard(credit, index, refCreditId, context);

      index++;
    }

    return countFail;
  }

  private int validCountSubCard(CmsBaseCreditDTO credit, int parentIndex, List<String> refCreditId,
      ConstraintValidatorContext context) {

    if (!CARD.getCode().equals(credit.getCreditType()) || CollectionUtils.isEmpty(
        ((CmsCreditCardDTO) credit).getSubCard())) {
      return 0;
    }

    int countFail = 0;
    int index = 0;

    for (CmsSubCardDTO sub : ((CmsCreditCardDTO) credit).getSubCard()) {
      if (refCreditId.contains(sub.getSubCardId())) {
        context.buildConstraintViolationWithTemplate(
                context.getDefaultConstraintMessageTemplate())
            .addPropertyNode(object + "[" + parentIndex + "].subCard[" + index + "].subCardId")
            .addConstraintViolation();
        countFail++;
      }
      refCreditId.add(sub.getSubCardId());
      index++;
    }

    return countFail;
  }

  private int validCountIdentities(String parentText, List<CmsCustomerIdentityDTO> identities,
      List<CmsCustomerIdentityDTO> otherIdentities, ConstraintValidatorContext context) {
    int index = 0;
    int countFail = 0;
    if (CollectionUtils.isNotEmpty(identities)) {
      List<String> ldpIdentityId = new ArrayList<>();
      List<String> indentity = new ArrayList<>();

      List<String> otherLdpIdentityIds = new ArrayList<>();
      if (CollectionUtils.isNotEmpty(otherIdentities)) {
        otherLdpIdentityIds = otherIdentities.stream().map(CmsCustomerIdentityDTO::getLdpIdentityId).collect(Collectors.toList());
      }


      for (CmsCustomerIdentityDTO identity : identities) {
        if (ldpIdentityId.contains(identity.getLdpIdentityId())) {
          context.buildConstraintViolationWithTemplate(
                  context.getDefaultConstraintMessageTemplate())
              .addPropertyNode(parentText + ".identities[" + index + "].ldpIdentityId")
              .addConstraintViolation();
          countFail++;
        }

        // check duplicate LdpIdentityId
        if (otherLdpIdentityIds.contains(identity.getLdpIdentityId())) {
          context.buildConstraintViolationWithTemplate(
                  context.getDefaultConstraintMessageTemplate())
              .addPropertyNode(parentText + ".identities[" + index + "].ldpIdentityId")
              .addConstraintViolation();
          countFail++;
        }

        // check duplicate IdentifierNumber
        if (indentity.contains(identity.getIdentifierNumber())) {
          context.buildConstraintViolationWithTemplate(
                  context.getDefaultConstraintMessageTemplate())
              .addPropertyNode(parentText + ".identities[" + index + "].identifierNumber")
              .addConstraintViolation();
          countFail++;
        }

        indentity.add(identity.getIdentifierNumber());
        ldpIdentityId.add(identity.getLdpIdentityId());
        index++;
      }
    }

    return countFail;
  }

  private int validCountAddress(String parentText, List<CmsCustomerAddressDTO> addresses,
      List<CmsCustomerAddressDTO> otherAddresses,
      ConstraintValidatorContext context) {
    int index = 0;
    Integer countFail = 0;
    if (CollectionUtils.isNotEmpty(addresses)) {
      List<String> ldpAddressId = new ArrayList<>();
      List<String> addressType = new ArrayList<>();

      List<String> otherLdpAddressIdIds = new ArrayList<>();
      if (CollectionUtils.isNotEmpty(otherAddresses)) {
        otherLdpAddressIdIds = otherAddresses.stream()
            .map(CmsCustomerAddressDTO::getLdpAddressId)
            .collect(Collectors.toList());
      }

      for (CmsCustomerAddressDTO address : addresses) {
        if (ldpAddressId.contains(address.getLdpAddressId())) {
          context.buildConstraintViolationWithTemplate(
                  context.getDefaultConstraintMessageTemplate())
              .addPropertyNode(parentText + ".addresses[" + index + "].ldpAddressId")
              .addConstraintViolation();
          countFail++;
        }

        if (otherLdpAddressIdIds.contains(address.getLdpAddressId())) {
          context.buildConstraintViolationWithTemplate(
                  context.getDefaultConstraintMessageTemplate())
              .addPropertyNode(parentText + ".addresses[" + index + "].ldpAddressId")
              .addConstraintViolation();
          countFail++;
        }

        if (addressType.contains(address.getAddressType()) && address.getAddressType().equals("V001")) {
          context.buildConstraintViolationWithTemplate(
                  context.getDefaultConstraintMessageTemplate())
              .addPropertyNode(parentText + ".addresses[" + index + "].addressType")
              .addConstraintViolation();
          countFail++;
        }

        addressType.add(address.getAddressType());
        ldpAddressId.add(address.getLdpAddressId());
        index++;
      }
    }

    return countFail;
  }

  private boolean isValidCountIncomeItemId(List<PairKey> pairKeys,
      CmsBaseIncomeItemDTO incomeItem) {
    if (CollectionUtils.isEmpty(pairKeys)) {
      return true;
    }

    for (PairKey pairKey : pairKeys) {
      if (pairKey.getIncomeType().equals(incomeItem.getIncomeType()) && pairKey.getRefIncomeItemId()
          .equals(incomeItem.getRefIncomeItemId())) {
        return false;
      }
    }
    return true;
  }

  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  private static class PairKey {
    private String incomeType;
    private String refIncomeItemId;
  }
}
