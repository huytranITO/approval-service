package com.msb.bpm.approval.appr.model.dto.formtemplate.response;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleTemplateResponseDTO implements Serializable {

  private static final long serialVersionUID = 3306603620862368788L;
  private String code;
  private String message;
  private RuleTemplateData data;

}
