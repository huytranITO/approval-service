package com.msb.bpm.approval.appr.model.request.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Address {
  private Long id;
  private String addressType;
  private String cityCode;
  private boolean edit;
  private String districtCode;
  private String wardCode;
  private String addressLine;
  private Boolean isHKTT;
}
