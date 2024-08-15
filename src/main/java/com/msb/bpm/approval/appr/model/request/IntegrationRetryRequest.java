package com.msb.bpm.approval.appr.model.request;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationRetryRequest {

  private List<RetryRequest> retryList;

  @Data
  public static final class RetryRequest {

    @NotNull
    private Long id;
  }
}
