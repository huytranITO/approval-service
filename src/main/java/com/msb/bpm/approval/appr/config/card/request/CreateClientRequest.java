package com.msb.bpm.approval.appr.config.card.request;

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
public class CreateClientRequest {
  private Integer cifNumber;
  private String regNumber;
  private Integer type;
}
