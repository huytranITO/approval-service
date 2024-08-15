package com.msb.bpm.approval.appr.model.response.bpm.operation;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponse {
  private String code;
  private String message;
  private Object data;
}