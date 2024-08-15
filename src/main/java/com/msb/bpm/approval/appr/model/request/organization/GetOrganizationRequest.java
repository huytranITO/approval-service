package com.msb.bpm.approval.appr.model.request.organization;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetOrganizationRequest {
  private String name;
  private String specializedBank;
  private String type;
  private Filter filter;

  @Data
  @Builder
  public static class Filter {
    private  Integer page;
    private  Integer pageSize;
  }
}
