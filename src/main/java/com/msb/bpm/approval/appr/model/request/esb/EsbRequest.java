package com.msb.bpm.approval.appr.model.request.esb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EsbRequest {

  private CommonInfoRequest commonInfo;

  private String cif;
}
