package com.msb.bpm.approval.appr.service.cic;

import com.msb.bpm.approval.appr.enums.report.CICReportType;

public interface ReportService {
  byte[] getCicPdfReport(CICReportType CICReportType, Object data);
}
