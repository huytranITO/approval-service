package com.msb.bpm.approval.appr.client.customer.request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 20/11/2023, Monday
 **/
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonMigrateVersionRequest implements Serializable {
  private Long customerId;
  private List<CustomerWrapRequest> customers = new ArrayList<>();

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CustomerWrapRequest implements Serializable {
    private CustomerVersionRequest customer;
    private List<IdentityMigrateVersionRequest> identityDocuments = new ArrayList<>();
    private List<AddressMigrateVersionRequest> addresses = new ArrayList<>();
  }
}
