package com.msb.bpm.approval.appr.service.cic.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.msb.bpm.approval.appr.enums.report.CICReportType;
import com.msb.bpm.approval.appr.model.dto.cic.report.CICReportData;
import com.msb.bpm.approval.appr.service.cic.ReportService;
import com.msb.bpm.approval.appr.util.JsonUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {

  private final ObjectMapper objectMapper;

  @Override
  public byte[] getCicPdfReport(@NotNull CICReportType CICReportType, Object data) {
    CICReportData cicReportData = (CICReportData) JsonUtil.convertObject2Object(data,
        CICReportType.getDataClassType(), objectMapper);
    // khởi tạo data source
    JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(
        Arrays.asList(""));
    // khai báo các parameter
    Map<String, Object> parameters = cicReportData.parameters();
    return getPdfReport(CICReportType.getTemplatePath(), dataSource, parameters);
  }

  private byte[] getPdfReport(String filePath,
      JRBeanCollectionDataSource dataSource,
      Map<String, Object> parameters) {
    try {
      InputStream template = getInputStream(filePath);
      if (template == null) {
        return null;
      }
      JasperReport jasperReport = JasperCompileManager.compileReport(template);
      // compile file report cùng các tham số đã khai báo
      return JasperExportManager.exportReportToPdf(
          JasperFillManager.fillReport(jasperReport, parameters, dataSource));
    } catch (Exception ex) {
      log.error("Generate cic report is error!", ex);
      return null;
    }
  }

  private InputStream getInputStream(String filePath) {
    if (!StringUtils.hasText(filePath)) {
      return null;
    }
    try {
      return new ClassPathResource(filePath)
          .getInputStream();
    } catch (IOException e) {
      log.error("Open report template is error! filePath: " + filePath, e);
      return null;
    }
  }

}
