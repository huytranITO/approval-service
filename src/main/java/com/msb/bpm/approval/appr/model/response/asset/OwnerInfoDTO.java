package com.msb.bpm.approval.appr.model.response.asset;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class OwnerInfoDTO {

  private Long customerId;

  private String customerRefCode;

  private String customerName;

  private String customerType;

  private String relationshipCode;

  private String relationshipName;
}
