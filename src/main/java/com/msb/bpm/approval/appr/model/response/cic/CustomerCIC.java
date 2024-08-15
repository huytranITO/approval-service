package com.msb.bpm.approval.appr.model.response.cic;

import lombok.Data;

@Data
public class CustomerCIC {

  private String businessRegNum;

  private String cicCode;

  private String address;

  private String taxCode;

  private String customerName;

  private String uniqueId;

}
