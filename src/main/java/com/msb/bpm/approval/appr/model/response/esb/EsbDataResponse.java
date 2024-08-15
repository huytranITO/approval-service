package com.msb.bpm.approval.appr.model.response.esb;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EsbDataResponse {

  private ListOfCAAccountsResponse listOfCAAccounts;

  private ListOfFDAccountsResponse listOfFDAccounts;

  private ListOfSAAccountsResponse listOfSAAccounts;

  private ListOfLNAccountsResponse listOfLNAccounts;
}
