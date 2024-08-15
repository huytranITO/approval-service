package com.msb.bpm.approval.appr.client.customer.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddOrUpdateCusRelationshipRequest {
  private Long customerId;
  private Long relatedCustomerId;
  private List<RelationshipDetail> relationships;
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class RelationshipDetail {
    private Long id;
    private List<String> positions;
    private String relatedType;
    private String contributionRatio;
    private String relatedDetail;
  }
}
