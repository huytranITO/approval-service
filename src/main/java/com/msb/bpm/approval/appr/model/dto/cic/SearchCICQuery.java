package com.msb.bpm.approval.appr.model.dto.cic;

import lombok.Data;

@Data
public class SearchCICQuery {

  private String cicCode;

  private String customerUniqueId;

  private String customerType;

  private String productCode;

  private Integer validityPeriod;

  private String address;

  private String customerName;

  private String branchCode;

  private String dealingRoomCode;

}
