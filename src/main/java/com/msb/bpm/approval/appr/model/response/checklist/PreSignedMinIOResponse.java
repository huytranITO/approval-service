package com.msb.bpm.approval.appr.model.response.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreSignedMinIOResponse {

  private String filePath;

  private String url;

  private String bucket;
}
