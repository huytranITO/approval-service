package com.msb.bpm.approval.appr.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
* @author: BaoNV2
* @since: 15/5/2023 12:46 PM
* @description:  
* @update:
*
* */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BodyRequest<T> {
  private T data;
  private String code;
  private Integer version;
  @JsonProperty("applicationId")
  private String applicationBpmId;
  private String group;
}
