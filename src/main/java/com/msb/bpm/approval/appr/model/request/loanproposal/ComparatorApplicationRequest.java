package com.msb.bpm.approval.appr.model.request.loanproposal;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComparatorApplicationRequest implements Serializable {

  private Map<String, Object> data = new HashMap<>();

}
