package com.msb.bpm.approval.appr.model.request.checklist;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DeleteDomainData {

  @NotBlank
  private String domainType;
  
  @NotNull
  private Long domainObjectId;
  
}
