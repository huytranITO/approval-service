package com.msb.bpm.approval.appr.config.card.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class CreateCardResponse {
  private Data data;
  private Integer code;
  private String message;
  private Date timestamp;

  @Getter
  @Setter
  public static class Data {
    private String liabilityContract;
    private String issueContract;
    private String contractNumber;
    private String policyCode;
    private Date createdDate;
  }

}
