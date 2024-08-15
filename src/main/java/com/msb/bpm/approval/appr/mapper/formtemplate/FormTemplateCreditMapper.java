package com.msb.bpm.approval.appr.mapper.formtemplate;

import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditCardEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditLoanEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditOverdraftEntity;
import com.msb.bpm.approval.appr.util.MathUtil;import java.util.Objects;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;import org.mapstruct.Named;

@Mapper(
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = FormTemplateApplicationDtoMapper.class)
public abstract class FormTemplateCreditMapper
    implements FormTemplateCreditExecutorMapper<FormTemplateCreditDTO> {

  public FormTemplateCreditDTO getSourceCredit(ApplicationCreditEntity entity) {
    if (!Objects.isNull(entity.getCreditLoan())) {
      return map(entity.getCreditLoan(), entity);
    } else if (!Objects.isNull(entity.getCreditOverdraft())) {
      return map(entity.getCreditOverdraft(), entity);
    } else return map(entity.getCreditCard(), entity);
  }

  @Override
  public FormTemplateCreditDTO map(ApplicationCreditCardEntity cardEntity, ApplicationCreditEntity entity) {
    return executeMap(cardEntity, entity);
  }

  @Override
  public FormTemplateCreditDTO map(ApplicationCreditLoanEntity loanEntity, ApplicationCreditEntity entity) {
    return executeMap(loanEntity, entity);
  }

  @Override
  public FormTemplateCreditDTO map(ApplicationCreditOverdraftEntity overdraftEntity, ApplicationCreditEntity entity) {
    return executeMap(overdraftEntity, entity);
  }

  @Mapping(target="id", source = "entity.id")
  @Mapping(target = "cardCreditTypeValue", source = "entity.creditTypeValue", defaultValue = "null")
  @Mapping(target = "cardDocumentName", source = "entity.documentCode")
  @Mapping(target = "cardLimitSustentivePeriod", source = "cardEntity.limitSustentivePeriod", defaultValue = "null")
  @Mapping(target = "primaryCardLimit", source = "entity.loanAmount", qualifiedByName = "toNumberFormat")
  @Mapping(target = "cardGuaranteeFormValue", source = "entity.guaranteeFormValue")
  @Mapping(target = "cardApproveResultValue", source = "entity.approveResultValue")
  @Mapping(target = "cardProductName", source = "entity.creditCard.productName")
  abstract FormTemplateCreditDTO executeMap(
      ApplicationCreditCardEntity cardEntity, ApplicationCreditEntity entity);

  @Mapping(target = "id", source = "entity.id")
  @Mapping(target = "loanCreditTypeValue", source = "entity.creditTypeValue")
  @Mapping(target = "loanDocumentName", source = "entity.documentCode")
  @Mapping(target = "loaApproveResultValue", source = "entity.approveResultValue")
  @Mapping(target = "loanTypeAmount", source = "entity.loanAmount", qualifiedByName = "toNumberFormat")
  @Mapping(target = "loanTypePurposeValue", source = "loanEntity.loanPurposeValue")
  @Mapping(target = "loanLtd", source = "loanEntity.ltd")
  @Mapping(target = "loanCreditFormValue", source = "loanEntity.creditFormValue")
  @Mapping(target = "loanDisburseFrequencyValue", source = "loanEntity.disburseFrequencyValue")
  @Mapping(target = "loanDebtPayMethodValue", source = "loanEntity.debtPayMethodValue")
  @Mapping(target = "loanTotalCapital", source = "loanEntity.totalCapital", qualifiedByName = "toNumberFormat")
  @Mapping(target = "loanEquityCapital", source = "loanEntity.equityCapital", qualifiedByName = "toNumberFormat")
  @Mapping(target = "loanPayback", source = "loanEntity.payback", qualifiedByName = "toTextLoanPayback")
  @Mapping(target = "loanOriginalPeriod", source = "loanEntity.originalPeriod")
  @Mapping(target = "loanGuaranteeFormValue", source = "entity.guaranteeFormValue")
  @Mapping(target = "loanProductName", source = "entity.creditLoan.productName")
  @Mapping(target = "loanProductInfoName", source = "entity.creditLoan.productInfoName")
  abstract FormTemplateCreditDTO executeMap(
      ApplicationCreditLoanEntity loanEntity, ApplicationCreditEntity entity);

  @Mapping(target="id", source = "entity.id")
  @Mapping(target="overdraftDocumentName", source = "entity.documentCode", defaultValue = "null")
  @Mapping(target="overdraftCreditTypeValue", source = "entity.creditTypeValue", defaultValue = "null")
  @Mapping(target="overdraftDebtPayMethodValue", source = "overdraftEntity.debtPayMethodValue", defaultValue = "null")
  @Mapping(target="overdraftAmount", source = "entity.loanAmount", qualifiedByName = "toNumberFormat")
  @Mapping(target="overdraftLimitSustentivePeriod", source = "overdraftEntity.limitSustentivePeriod", defaultValue = "null")
  @Mapping(target="overdraftPurposeValue", source = "overdraftEntity.loanPurposeValue", defaultValue = "null")
  @Mapping(target = "overdraftApproveResultValue", source = "entity.approveResultValue")
  @Mapping(target = "overdraftGuaranteeFormValue", source = "entity.guaranteeFormValue")
  @Mapping(target = "overdraftProductName", source = "entity.creditOverdraft.productName")
  @Mapping(target = "overdraftProductInfoName", source = "entity.creditOverdraft.productInfoName")
  abstract FormTemplateCreditDTO executeMap(ApplicationCreditOverdraftEntity overdraftEntity, ApplicationCreditEntity entity);

}
