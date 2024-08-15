package com.msb.bpm.approval.appr.model.dto.cic;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CICDataCache {

  private String applicationBpmId;
  private boolean isSyncPdf;
  private boolean isSyncIndicator;
}
