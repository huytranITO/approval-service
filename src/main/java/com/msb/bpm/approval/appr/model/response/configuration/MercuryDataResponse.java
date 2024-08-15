package com.msb.bpm.approval.appr.model.response.configuration;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class MercuryDataResponse {

  private String category;
  private Integer code;
  private String message;
  private String subCategory;
  private Long transactionTime;
  private List<Value> value;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Value {
    private String id;
    private String code;
    private String name;
  }
}
