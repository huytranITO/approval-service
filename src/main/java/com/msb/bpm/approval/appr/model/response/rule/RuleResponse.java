package com.msb.bpm.approval.appr.model.response.rule;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleResponse {
  @JsonProperty("data")
  private RuleDataItem ruleDataItem;
  private String code;
  private String message;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RuleDataItem {
    private Integer version;
    private TransitionResponse data;
  }
}
