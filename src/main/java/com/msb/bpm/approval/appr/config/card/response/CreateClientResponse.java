package com.msb.bpm.approval.appr.config.card.response;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientResponse {
  private Date timestamp;
  private Integer code;
  private String message;
  private Object data;
}
