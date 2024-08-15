package com.msb.bpm.approval.appr.client.customer.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CustomerRelationResponse {

  private Long customerId;
  private List<RelationshipInfo> relationships;

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RelationshipInfo {
    private Long relatedCustomerId;
    private String bpmCif;
    private String cif;
    private Boolean active;
    private String relatedCustomerType;
    private String relatedCustomerName;
    private String relatedIdentity;
    private String relatedIdentityType;
    private Integer version;
    private List<RelationshipDetailInfo> relationshipDetails;
  }
  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RelationshipDetailInfo {
    private Long id;
    private String relationshipType;
    private String relatedDetail;
    private String relatedInverse;
    private List<String> positions;
    private String contributionRatio;
  }
}
