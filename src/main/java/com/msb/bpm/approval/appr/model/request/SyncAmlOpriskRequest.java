package com.msb.bpm.approval.appr.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import javax.validation.constraints.NotNull;

@With
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncAmlOpriskRequest {

  @NotNull
  private String maDinhDanh;
  @NotNull
  private String statusId;

}
