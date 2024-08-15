package com.msb.bpm.approval.appr.model.response.t24;

import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class T24AccountResponse {
  private T24DataResponse data;
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class T24DataResponse {
    private AccountList accountList;
  }
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class AccountList {
    private List<Account> listOfCAAccounts;
    private List<Account> listOfFDAccounts;
    private List<Account> listOfSAAccounts;
    private List<Account> listOfLNAccounts;

    public List<Account> getListOfCAAccounts() {
      if (CollectionUtils.isNotEmpty(listOfCAAccounts)){
        return listOfCAAccounts;
      }
      return Collections.emptyList();
    }

    public List<Account> getListOfFDAccounts() {
      if (CollectionUtils.isNotEmpty(listOfFDAccounts)) {
        return listOfFDAccounts;
      }
      return Collections.emptyList();
    }

    public List<Account> getListOfSAAccounts() {
      if (CollectionUtils.isNotEmpty(listOfSAAccounts)) {
        return listOfSAAccounts;
      }
      return Collections.emptyList();
    }

    public List<Account> getListOfLNAccounts() {
      if (CollectionUtils.isNotEmpty(listOfLNAccounts)) {
        return listOfLNAccounts;
      }
      return Collections.emptyList();
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Account {
    private String accountNumber;
    private String accountName;
    private String acctType;
    private String productType;
    private String currency;
    private String relationShip;
    private String avaiableBalance;
    private String status;
  }
}
