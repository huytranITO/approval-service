package com.msb.bpm.approval.appr.model.dto.formtemplate.response;

import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class AsyncCreateTemplateResultDTO implements Serializable {

  private static final long serialVersionUID = 2806496721866403779L;

  private boolean uploadDocStatus;

  private String message;

  private String requestCode;

  private String phaseCode;

  private List<ChecklistMessageResponseDTO> listChecklist;
}
