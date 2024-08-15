package com.msb.bpm.approval.appr.config.card.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSubCardResponse {
  private Data data;
  private Integer code;
  private String message;
  private Date timestamp;

  @Getter
  @Setter
  public static class Data {
    private String cardNumber;
    private Date createdDate;
  }
}
