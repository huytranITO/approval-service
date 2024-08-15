package com.msb.bpm.approval.appr.model.request.query;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.msb.bpm.approval.appr.enums.application.CustomerType;
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
public class PostQueryCICRequest {

  @NotNull
  private Set<QueryCIC> data;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @ToString
  @JsonInclude(Include.NON_NULL)
  public static class QueryCIC {

    private Long customerId;
    private Long refCustomerId;
    private String relationship;
    private CustomerType customerType;
    private String identifierCode;
    private String productCode;
  }
}
