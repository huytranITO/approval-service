package com.msb.bpm.approval.appr.model.request.cic;

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
public class SearchCodeCICRequest {

  private String customerUniqueId;

  private String customerType;

}
