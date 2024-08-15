package com.msb.bpm.approval.appr.model.request.css;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonLegacyRequest {

  private String channel;

  private String requestTime;

  private String userAuthen;

  private String password;


}
