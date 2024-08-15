package com.msb.bpm.approval.appr.model.dto.formtemplate.request;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FormTemplateFileRequestDTO implements Serializable {
  private static final long serialVersionUID = -5861863616946728953L;

  private Long id;
  private String code;
  private String createdBy;
  private String returnType;

}
