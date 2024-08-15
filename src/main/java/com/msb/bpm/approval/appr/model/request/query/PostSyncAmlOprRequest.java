package com.msb.bpm.approval.appr.model.request.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.Set;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@ToString
public class PostSyncAmlOprRequest {

  @NotNull
  private Set<QueryAmlOpr> data;

  @Getter
  @Setter
  public static class QueryAmlOpr extends PostQueryCICRequest.QueryCIC {
    private boolean priority;
  }
}
