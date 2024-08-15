package com.msb.bpm.approval.appr.mapper.formtemplate;

import com.msb.bpm.approval.appr.model.dto.ApplicationLimitCreditDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateLimitCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationLimitCreditEntity;
import java.util.List;import java.util.Set;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;import org.mapstruct.Mapping;

@Mapper(
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = FormTemplateApplicationDtoMapper.class)
public interface FormTemplateLimitCreditMapper {

  Set<FormTemplateLimitCreditDTO> toLimitCredits(List<ApplicationLimitCreditEntity> limitCredits);

  @Mapping(target = "loanProductCollateral", source = "loanProductCollateral", qualifiedByName = "toNumberFormat")
  @Mapping(target = "otherLoanProductCollateral", source = "otherLoanProductCollateral", qualifiedByName = "toNumberFormat")
  @Mapping(target = "unsecureProduct", source = "unsecureProduct", qualifiedByName = "toNumberFormat")
  @Mapping(target = "otherUnsecureProduct", source = "otherUnsecureProduct", qualifiedByName = "toNumberFormat")
  FormTemplateLimitCreditDTO toFormtemplateLimitCredit(ApplicationLimitCreditEntity entity);
}
