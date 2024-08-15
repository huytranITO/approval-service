package com.msb.bpm.approval.appr.model.request.t24;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class T24Request {

  private CommonInfo commonInfo;

  private String cifNumber;
  @Data
  public static class CommonInfo {}

}
