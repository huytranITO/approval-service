package com.msb.bpm.approval.appr.client.customer.response;

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
public class CommonMigrateVersionResponse implements Serializable {

  private CommonWrapVersionResponse data;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CommonWrapVersionResponse {
    private List<CustomerVersionResponse> customers = new ArrayList<>();
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CustomerVersionResponse {
    private Long refCustomerId;
    private Integer version;
  }
}
