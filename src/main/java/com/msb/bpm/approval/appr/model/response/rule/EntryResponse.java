package com.msb.bpm.approval.appr.model.response.rule;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntryResponse {
  private String type;
  private String value;
  private Object valueInfo;
}
