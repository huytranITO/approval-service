package com.msb.bpm.approval.appr.model.dto.formtemplate;

import com.msb.bpm.approval.appr.model.dto.CustomerAndRelationPersonDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.request.FormTemplateApplicationDTO;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;

@Getter
@Setter
@ToString
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FormTemplateInitializeInfoDTO {
  private boolean completed = Boolean.FALSE;
  private FormTemplateApplicationDTO application;
  private CustomerAndRelationPersonDTO customerAndRelationPerson;
  private Set<FormTemplateIncomesDTO> incomes;
  private String totalIncomes;
}
