package com.msb.bpm.approval.appr.model.dto.cic.kafka.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Action {

  @JsonProperty("Select")
  private String select;

  @JsonProperty("Where")
  private String where;

  @JsonProperty("GroupBy")
  private String groupBy;

  @JsonProperty("Having")
  private String having;

  @JsonProperty("OrderBy")
  private String orderBy;

  @JsonProperty("Paging")
  private String paging;
}
