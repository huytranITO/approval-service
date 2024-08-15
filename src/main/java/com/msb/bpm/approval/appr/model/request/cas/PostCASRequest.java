package com.msb.bpm.approval.appr.model.request.cas;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCASRequest {
  @NotNull
  @NotEmpty
  private String bpmId;

  private String requestId;

  @NotNull
  @NotEmpty
  private String profileId;

  private String cif;

  private String[] identityNumbers;

}
