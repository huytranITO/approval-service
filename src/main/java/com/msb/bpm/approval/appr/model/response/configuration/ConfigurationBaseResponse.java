package com.msb.bpm.approval.appr.model.response.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurationBaseResponse<T> {
  private String code;
  @JsonProperty("data")
  private T data;
}
