package com.msb.bpm.approval.appr.model.request.customer;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class SearchByListCustomerRequest {
  private List<String> identityNumbers;
  private List<Long> ids = new ArrayList<>();
}
