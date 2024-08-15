package com.msb.bpm.approval.appr.model.request.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class PostQueryCreditRatingRequest {


  private Long id;
  private String profileId;
  private String customerType;
  @NotNull
  private Set<QueryCSS> list;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @JsonInclude(Include.NON_NULL)
  public static class QueryCSS {

    private String identifierCode;
  }
}
