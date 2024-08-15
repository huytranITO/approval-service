package com.msb.bpm.approval.appr.model.response.customer;

import lombok.Data;

@Data
public class AddressResponse {

  private Long id;
  private String addressType;
  private boolean isHKTT;
  //private String countryCode;
  private String cityCode;
  //private String cityName;
  private String districtCode;
  //private String districtName;
  private String wardCode;
  //private String wardName;
  private String addressLine;
  //private String fullAddress;
}

