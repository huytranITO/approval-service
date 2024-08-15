package com.msb.bpm.approval.appr.client.customer.request;

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
public class SearchCusRelationshipRequest {
  private Long customerId;
  private Long relatedCustomerId;
}
