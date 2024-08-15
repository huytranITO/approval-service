package com.msb.bpm.approval.appr.model.response.configuration;

import java.io.Serializable;
import java.util.List;import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetListResponse implements Serializable {

  private static final long serialVersionUID=-2367144822668465425L;

  private Map<String,List<Detail>> value;

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Detail{
    private String value;
    private String code;
  }
}
