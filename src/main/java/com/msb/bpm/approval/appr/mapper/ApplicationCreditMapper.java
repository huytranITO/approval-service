package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.model.dto.ApplicationCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditLoanDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditOverdraftDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCreditCardDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCreditLoanDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsCreditOverdraftDTO;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsSubCardDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditOverdraftEntity;
import com.msb.bpm.approval.appr.model.entity.SubCreditCardEntity;
import java.util.Set;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.util.CollectionUtils;

@Mapper
public interface ApplicationCreditMapper {

  ApplicationCreditMapper INSTANCE = Mappers.getMapper(ApplicationCreditMapper.class);

  @Mapping(target = "id", source = "credit.id")
  @Mapping(target = "appCreditLoanId", source = "creditLoan.id")
  ApplicationCreditLoanDTO toCreditLoanDTO(ApplicationCreditEntity credit,
      ApplicationCreditLoanEntity creditLoan);

  @Mapping(target = "id", source = "credit.id")
  @Mapping(target = "appCreditOverdraftId", source = "creditOverdraft.id")
  ApplicationCreditOverdraftDTO toCreditOverdraftDTO(ApplicationCreditEntity credit,
      ApplicationCreditOverdraftEntity creditOverdraft);

  @Mapping(target = "id", source = "credit.id")
  @Mapping(target = "appCreditCardId", source = "creditCard.id")
  @Mapping(target = "hasSubCard", expression = "java(hasSubCard(creditCard))")
  @Mapping(target = "address.addressLine", source = "creditCard.addressLine")
  @Mapping(target = "address.cityCode", source = "creditCard.cityCode")
  @Mapping(target = "address.cityValue", source = "creditCard.cityValue")
  @Mapping(target = "address.districtCode", source = "creditCard.districtCode")
  @Mapping(target = "address.districtValue", source = "creditCard.districtValue")
  @Mapping(target = "address.wardCode", source = "creditCard.wardCode")
  @Mapping(target = "address.wardValue", source = "creditCard.wardValue")
  ApplicationCreditCardDTO toCreditCardDTO(ApplicationCreditEntity credit,
      ApplicationCreditCardEntity creditCard);

  @Mapping(target = "id", source = "appCreditCardId")
  @Mapping(source = "address.addressLine", target = "addressLine")
  @Mapping(source = "address.cityCode", target = "cityCode")
  @Mapping(source = "address.cityValue", target = "cityValue")
  @Mapping(source = "address.districtCode", target = "districtCode")
  @Mapping(source = "address.districtValue", target = "districtValue")
  @Mapping(source = "address.wardCode", target = "wardCode")
  @Mapping(source = "address.wardValue", target = "wardValue")
  @Mapping(target = "contractL", ignore = true)
  @Mapping(target = "issuingContract", ignore = true)
  @Mapping(target = "contractNumber", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  ApplicationCreditCardEntity toCreditCardEntity(ApplicationCreditCardDTO dto);

  @Mapping(target = "id", source = "appCreditLoanId")
  @Mapping(target = "acfNo", ignore = true)
  @Mapping(target = "accountNo", ignore = true)
  @Mapping(target = "status", ignore = true)
  ApplicationCreditLoanEntity toCreditLoanEntity(ApplicationCreditLoanDTO dto);

  @Mapping(target = "id", source = "appCreditOverdraftId")
  @Mapping(target = "acfNo", ignore = true)
  @Mapping(target = "accountNo", ignore = true)
  @Mapping(target = "status", ignore = true)
  ApplicationCreditOverdraftEntity toCreditOverdraftEntity(ApplicationCreditOverdraftDTO dto);

  ApplicationCreditEntity toCreditEntity(ApplicationCreditDTO creditDTO);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceCreditEntity(@MappingTarget ApplicationCreditEntity e1, ApplicationCreditEntity e2);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "ldpCreditId")
  @Mapping(target = "creditType")
  @Mapping(target = "creditTypeValue")
  @Mapping(target = "guaranteeForm")
  @Mapping(target = "guaranteeFormValue")
  @Mapping(target = "loanAmount")
  @Mapping(target = "creditLoan", source = "creditLoan", qualifiedByName = "cmsReferenceCreditLoanEntity")
  @Mapping(target = "creditOverdraft", source = "creditOverdraft", qualifiedByName = "cmsReferenceCreditOverdraftEntity")
  @Mapping(target = "creditCard", source = "creditCard", qualifiedByName = "cmsReferenceCreditCardEntity")
  void cmsReferenceCreditEntity(@MappingTarget ApplicationCreditEntity e1, ApplicationCreditEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceCreditLoanEntity(@MappingTarget ApplicationCreditLoanEntity e1,
      ApplicationCreditLoanEntity e2);

  @Named("cmsReferenceCreditLoanEntity")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "totalCapital")
  @Mapping(target = "loanPeriod")
  @Mapping(target = "loanPurpose")
  @Mapping(target = "loanPurposeValue")
  @Mapping(target = "creditForm")
  @Mapping(target = "creditFormValue")
  @Mapping(target = "ldpLoanId")
  void cmsReferenceCreditLoanEntity(@MappingTarget ApplicationCreditLoanEntity e1,
      ApplicationCreditLoanEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  void referenceCreditOverdraftEntity(@MappingTarget ApplicationCreditOverdraftEntity e1,
      ApplicationCreditOverdraftEntity e2);

  @Named("cmsReferenceCreditOverdraftEntity")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "limitSustentivePeriod")
  @Mapping(target = "loanPurpose")
  @Mapping(target = "loanPurposeValue")
  @Mapping(target = "ldpOverdraftId")
  void cmsReferenceCreditOverdraftEntity(@MappingTarget ApplicationCreditOverdraftEntity e1,
      ApplicationCreditOverdraftEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "subCreditCards", ignore = true)
  void referenceCreditCardEntity(@MappingTarget ApplicationCreditCardEntity e1,
      ApplicationCreditCardEntity e2);

  @Named("cmsReferenceCreditCardEntity")
  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "cardType")
  @Mapping(target = "cardTypeValue")
  @Mapping(target = "cardName")
  @Mapping(target = "secretQuestion")
  @Mapping(target = "autoDeductRate")
  @Mapping(target = "autoDeductRateValue")
  @Mapping(target = "deductAccountNumber")
  @Mapping(target = "email")
  @Mapping(target = "cardForm")
  @Mapping(target = "cardFormValue")
  @Mapping(target = "cardReceiveAddress")
  @Mapping(target = "cardReceiveAddressValue")
  @Mapping(target = "cityCode")
  @Mapping(target = "cityValue")
  @Mapping(target = "districtCode")
  @Mapping(target = "districtValue")
  @Mapping(target = "wardCode")
  @Mapping(target = "wardValue")
  @Mapping(target = "addressLine")
  @Mapping(target = "way4BranchCode")
  @Mapping(target = "ldpCardId")
  void cmsReferenceCreditCardEntity(@MappingTarget ApplicationCreditCardEntity e1,
      ApplicationCreditCardEntity e2);

  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "applicationCreditCard", ignore = true)
  void referenceSubCreditCardEntity(@MappingTarget SubCreditCardEntity e1, SubCreditCardEntity e2);

  @BeanMapping(ignoreByDefault = true)
  @Mapping(target = "ldpSubId")
  @Mapping(target = "cardOwnerName")
  @Mapping(target = "email")
  @Mapping(target = "phoneNumber")
  @Mapping(target = "cardLimitAmount")
  void cmsReferenceSubCreditCardEntity(@MappingTarget SubCreditCardEntity e1, SubCreditCardEntity e2);

  @Mapping(target = "ldpCreditId", source = "refCreditId")
  ApplicationCreditEntity cmsCreditToEntity(CmsBaseCreditDTO creditDTO);

  @Mapping(target = "ldpLoanId", source = "refCreditId")
  ApplicationCreditLoanEntity cmsCreditLoanToEntity(CmsCreditLoanDTO creditLoanDTO);

  @Mapping(target = "refCreditId", source = "applicationCreditLoanEntity.ldpLoanId")
  @Mapping(target = "creditType", source = "applicationCreditEntity.creditType")
  @Mapping(target = "guaranteeForm", source = "applicationCreditEntity.guaranteeForm")
  @Mapping(target = "loanAmount", source = "applicationCreditEntity.loanAmount")
  CmsCreditLoanDTO entityToCmsCreditLoan(ApplicationCreditLoanEntity applicationCreditLoanEntity,
      ApplicationCreditEntity applicationCreditEntity);

  @Mapping(target = "ldpOverdraftId", source = "refCreditId")
  ApplicationCreditOverdraftEntity cmsCreditOverdraftToEntity(
      CmsCreditOverdraftDTO creditOverdraftDTO);

  @Mapping(target = "refCreditId", source = "applicationCreditOverdraftEntity.ldpOverdraftId")
  @Mapping(target = "creditType", source = "applicationCreditEntity.creditType")
  @Mapping(target = "guaranteeForm", source = "applicationCreditEntity.guaranteeForm")
  @Mapping(target = "loanAmount", source = "applicationCreditEntity.loanAmount")
  CmsCreditOverdraftDTO entityToCmsCreditOverdraft(
      ApplicationCreditOverdraftEntity applicationCreditOverdraftEntity,
      ApplicationCreditEntity applicationCreditEntity);

  @Mapping(target = "ldpCardId", source = "refCreditId")
  @Mapping(target = "way4BranchCode", source = "branchReceive")
  @Mapping(target = "subCreditCards", source = "subCard")
  @Mapping(target = "deductAccountNumber", ignore = true)
  ApplicationCreditCardEntity cmsCreditCardToEntity(CmsCreditCardDTO creditCardDTO);

  @Mapping(target = "refCreditId", source = "applicationCreditCardEntity.ldpCardId")
  @Mapping(target = "creditType", source = "applicationCreditEntity.creditType")
  @Mapping(target = "guaranteeForm", source = "applicationCreditEntity.guaranteeForm")
  @Mapping(target = "loanAmount", source = "applicationCreditEntity.loanAmount")
  @Mapping(target = "subCard", source = "applicationCreditCardEntity.subCreditCards")
  @Mapping(target = "branchReceive", source = "applicationCreditCardEntity.way4BranchCode")
  CmsCreditCardDTO entityToCmsCreditCard(ApplicationCreditCardEntity applicationCreditCardEntity,
      ApplicationCreditEntity applicationCreditEntity);

  @Mapping(target = "ldpSubId", source = "subCardId")
  @Mapping(target = "cardOwnerName", source = "subCardName")
  @Mapping(target = "email", source = "subCardEmail")
  @Mapping(target = "phoneNumber", source = "subCardPhone")
  @Mapping(target = "cardLimitAmount", source = "subCardLimit")
  SubCreditCardEntity cmsSubCreditCardToEntity(CmsSubCardDTO subCard);

  default boolean hasSubCard(ApplicationCreditCardEntity creditCard) {
    return !CollectionUtils.isEmpty(creditCard.getSubCreditCards());
  }

  @Mapping(target = "subCardId", source = "ldpSubId")
  @Mapping(target = "subCardName", source = "cardOwnerName")
  @Mapping(target = "subCardEmail", source = "email")
  @Mapping(target = "subCardPhone", source = "phoneNumber")
  @Mapping(target = "subCardLimit", source = "cardLimitAmount")
  CmsSubCardDTO entityToCmsSubCardDto(SubCreditCardEntity subCreditCard);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "application", ignore = true)
  @Mapping(target = "oldId", source = "id")
  @Mapping(target = "idDraft", ignore = true)
  @Mapping(target = "ldpCreditId", ignore = true)
  ApplicationCreditEntity copyApplicationCreditEntity(ApplicationCreditEntity applicationCreditEntity);

  Set<ApplicationCreditEntity> copyApplicationCreditEntities(Set<ApplicationCreditEntity> applicationCreditEntities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "acfNo", ignore = true)
  @Mapping(target = "accountNo", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "ldpLoanId", ignore = true)
  ApplicationCreditLoanEntity copyApplicationCreditLoanEntity(ApplicationCreditLoanEntity applicationCreditLoanEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "acfNo", ignore = true)
  @Mapping(target = "accountNo", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "ldpOverdraftId", ignore = true)
  ApplicationCreditOverdraftEntity copyApplicationCreditOverdraftEntity(ApplicationCreditOverdraftEntity applicationCreditOverdraftEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "contractL", ignore = true)
  @Mapping(target = "issuingContract", ignore = true)
  @Mapping(target = "contractNumber", ignore = true)
  @Mapping(target = "createdDate", ignore = true)
  @Mapping(target = "ldpCardId", ignore = true)
  @Mapping(target = "secretQuestion", expression = "java(com.msb.bpm.approval.appr.util.VNCharacterUtils.removeAccent(applicationCreditCardEntity.getSecretQuestion()))" )
  ApplicationCreditCardEntity copyApplicationCreditCardEntity(ApplicationCreditCardEntity applicationCreditCardEntity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdBy", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedBy", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "applicationCreditCard", ignore = true)
  @Mapping(target = "contractNumber", ignore = true)
  @Mapping(target = "ldpSubId", ignore = true)
  SubCreditCardEntity copySubCreditCardEntity(SubCreditCardEntity subCreditCard);

  Set<SubCreditCardEntity> copySubCreditCardEntities(Set<SubCreditCardEntity> subCreditCardEntities);
}
