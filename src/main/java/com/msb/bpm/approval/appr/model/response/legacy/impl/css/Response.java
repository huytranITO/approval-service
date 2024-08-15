package com.msb.bpm.approval.appr.model.response.legacy.impl.css;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@NoArgsConstructor
public class Response {

  private String code;

  private String scoringTime;

  private String message;

  private String timestamp;

  private String status;

  private String statusCode;
}
