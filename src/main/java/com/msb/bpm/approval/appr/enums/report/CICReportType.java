package com.msb.bpm.approval.appr.enums.report;

import com.msb.bpm.approval.appr.model.dto.cic.report.blank.CICReportBlankData;
import com.msb.bpm.approval.appr.model.dto.cic.report.tnpn2.CICReportTNPN2Data;
import com.msb.bpm.approval.appr.model.dto.cic.report.tsbd.CICReportTSBDData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CICReportType {
  CIC_REPORT_BLANK("cic_report/rptBlank.jrxml", CICReportBlankData.class),
  CIC_REPORT_TNPN2("cic_report/rptTNPN2.jrxml", CICReportTNPN2Data.class),
  CIC_REPORT_TSBD("cic_report/rptTSBD.jrxml", CICReportTSBDData.class),
  ;

  private final String templatePath;
  private final Class dataClassType;


}
