package com.msb.bpm.approval.appr.client.customer.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CusRelationForSearchResponse {
  private Long customerId;
  List<Relationship> relationships;
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Relationship {
    private Long relatedCustomerId;
    private String relatedIdentity;
    private String relatedIdentityType;
    private String relatedCustomerType;
    private String relatedCustomerName;
    private Boolean active;
    private String bpmCif;
    private String cif;
    private Integer version;
    private List<RelationshipDetail> relationshipDetails;
  }
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RelationshipDetail {
    private Long id;
    private String relationshipType;
    private List<String> position;
    private String contributionRatio;
    private String relatedDetail;
  }
}
