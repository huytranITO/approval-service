package com.msb.bpm.approval.appr.mapper.formtemplate;

import com.msb.bpm.approval.appr.model.dto.ApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateApplicationAppraisalContentDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateApplicationDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateApplicationFieldInformationDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateApplicationRepaymentDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateSubCreditCardDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;
import com.msb.bpm.approval.appr.model.entity.ApplicationFieldInformationEntity;
import com.msb.bpm.approval.appr.model.entity.SubCreditCardEntity;
import com.msb.bpm.approval.appr.util.MathUtil;
import com.msb.bpm.approval.appr.util.SecurityContextUtil;
import java.util.Set;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface FormTemplateApplicationDtoMapper {

  Set<FormTemplateApplicationAppraisalContentDTO> toListAppraisailForm(Set<ApplicationAppraisalContentDTO> listOfAppraisal);

  @Mapping(target = "detailValue", source = "detailValue", qualifiedByName = "setToString" )
  @Mapping(target = "detail", source = "detail", qualifiedByName = "setToString" )
  FormTemplateApplicationAppraisalContentDTO toAppraisailForm(ApplicationAppraisalContentDTO appraisal);

  @Mapping(target = "proposalApprovalFullName", source = "proposalApprovalUser", qualifiedByName = "getCurrentUser")
  @Mapping(target = "suggestedAmount", source = "suggestedAmount", qualifiedByName = "toNumberFormat")
  FormTemplateApplicationDTO toFormTemplateApplicationDTO(ApplicationEntity entity);

  @Mapping(target = "cardLimitAmount", source = "cardLimitAmount", qualifiedByName = "toNumberFormat")
  FormTemplateSubCreditCardDTO toSubCreditCardDTO(SubCreditCardEntity entity);

  @Mapping(target = "totalRepay", source = "repayment.totalRepay")
  @Mapping(target = "dti", source = "repayment.dti", qualifiedByName = "toPercentage")
  @Mapping(target = "dsr", source = "repayment.dsr", qualifiedByName = "toPercentage")
  @Mapping(target = "mue", source = "repayment.mue", qualifiedByName = "toNumberFormat")
  @Mapping(target = "evaluate", source = "repayment.evaluate")
  FormTemplateApplicationRepaymentDTO toApplicationRepaymentDTO(ApplicationEntity entity);

  @Named("setToString")
  default String setToString(Set<String> value) {
    if (CollectionUtils.isNotEmpty(value)) return String.join(", ", value);
    return "";
  }

  @Named("toPercentage")
  default String toPercentage(String value) {
    return NumberUtils.isCreatable(value) ? value + "%" : "";
//    return value + "%";
  }

  @Named("toNumberFormat")
  default String toNumberFormat(Number value) {
      return MathUtil.toNumberFormat(value+"");
  }

  @Named("getCurrentUser")
  default String getCurrentUser(String value) {
    try {
      return SecurityContextUtil.getCurrentUser();
    } catch (Exception e) {
      return "";
    }
  }


  @Named("toTextLoanPayback")
  default String toTextLoanPayback(boolean payback){
    return payback ? "Hoàn vốn" : "";
  }


  Set<FormTemplateApplicationFieldInformationDTO> toSetFieldInformations(
      Set<ApplicationFieldInformationEntity> values);

//  @Mapping(target = "cityValue", source = "cityValue")
//  @Mapping(target = "wardValue", source = "wardValue")
//  @Mapping(target = "districtValue", source = "districtValue")
//  @Mapping(target = "addressLine", source = "addressLine")
  FormTemplateApplicationFieldInformationDTO toFieldInformations(ApplicationFieldInformationEntity fieldEntity);
}
