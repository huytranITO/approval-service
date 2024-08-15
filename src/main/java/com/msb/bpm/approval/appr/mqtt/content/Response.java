package com.msb.bpm.approval.appr.mqtt.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response {

  private String code;

  private String message;

  private Object data;

}
