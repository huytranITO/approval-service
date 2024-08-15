package com.msb.bpm.approval.appr.model.dto.cic.report.tsbd;

import com.msb.bpm.approval.appr.model.dto.cic.report.CICReportData;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CICReportTSBDData extends CICReportData {

  private ListData lstData;

  @Override
  public Map<String, Object> parameters() {
    Map<String, Object> result = super.parameters();

    if (lstData != null) {
      result.put("lstData", lstData.getData());
    }

    return result;
  }
}
