package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import com.msb.bpm.approval.appr.model.dto.ApplicationFieldInformationDTO;
import java.io.Serializable;
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
public class FormTemplateFieldInforDTO implements Serializable {
  private Set<FormTemplateApplicationFieldInformationDTO> fieldInformations;
}
