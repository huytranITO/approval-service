package com.msb.bpm.approval.appr.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class CmsDocumentDTO {

  @NotBlank
  private String docCode;
  private String docName;
  @NotNull
  private List<String> files;
  @NotNull
  private Map<String, Object> metadata;
}
