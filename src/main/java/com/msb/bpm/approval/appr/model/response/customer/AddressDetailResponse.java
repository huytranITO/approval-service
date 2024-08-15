package com.msb.bpm.approval.appr.model.response.customer;

import lombok.Data;

@Data
public class AddressDetailResponse {

  private Long id;
  private String addressType;
  private String countryCode;
  private String cityCode;
  private String cityName;
  private String districtCode;
  private String districtName;
  private String wardCode;
  private String wardName;
  private String addressLine;
  private String fullAddress;
  private boolean hktt;
}
