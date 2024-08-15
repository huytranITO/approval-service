package com.msb.bpm.approval.appr.model.response.cic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.msb.bpm.approval.appr.enums.cic.ResponseCodeCIC;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CICIntegrationResponse {

  private Integer code;

  private Long transactionTime;

  @JsonProperty("value")
  private List<CICData> value;

  private String message;

  private String category;

  public boolean isInvalidResponse() {
    return ResponseCodeCIC.isInvalid(code);
  }
}
