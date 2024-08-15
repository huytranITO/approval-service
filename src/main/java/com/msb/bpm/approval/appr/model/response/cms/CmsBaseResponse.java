package com.msb.bpm.approval.appr.model.response.cms;

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
public class CmsBaseResponse {
  private String bpmId;
  private String status;
  private Map<String, String> noted = new HashMap<>();
  private Map<String, Object> metadata = new HashMap<>();
}