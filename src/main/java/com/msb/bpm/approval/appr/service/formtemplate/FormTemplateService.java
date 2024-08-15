package com.msb.bpm.approval.appr.service.formtemplate;

import com.msb.bpm.approval.appr.model.dto.formtemplate.response.RuleTemplateResponseDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationEntity;

public interface FormTemplateService {

  void generateFormTemplate(
      ApplicationEntity entity, String phaseCode, RuleTemplateResponseDTO ruleTemplateResponse, String purpose);

  void updateTemplateChecklist(String generateResult);
}
