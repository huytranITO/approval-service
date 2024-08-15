package com.msb.bpm.approval.appr.config.card.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CreateCardErrorResponse {
  private Object data;
  private Integer code;
  private String message;
  private Date timestamp;
}
