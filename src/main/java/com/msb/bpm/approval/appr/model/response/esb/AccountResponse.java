package com.msb.bpm.approval.appr.model.response.esb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class AccountResponse {

  private String accountNumber;

  private String accountName;

  private String acctType;

  private String productType;

  private String currency;

  private String relationShip;

  private String avaiableBalance;

  private String status;

}
