package com.msb.bpm.approval.appr.model.request;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationRequest {

  private int page;
  private int size;
  private String sortField;
  private int sortOrder;
  @NotNull
  private String contents;
}
