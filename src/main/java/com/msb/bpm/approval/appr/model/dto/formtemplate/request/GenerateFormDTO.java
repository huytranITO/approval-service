package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import java.io.Serializable;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GenerateFormDTO implements Serializable {

  private static final long serialVersionUID = 1589071094170607980L;

  @NotNull(message = "requestCode is not null")
  private String requestCode;
//  @NotNull(message = "jsonData is not null")
//  private String jsonData;
  private String phaseCode;
  @NotNull(message = "listChecklist is not null")
  private List<FormTemplateChecklistRequestDTO> listChecklist;
}
