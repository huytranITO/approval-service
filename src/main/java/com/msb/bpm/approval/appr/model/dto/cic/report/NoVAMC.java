package com.msb.bpm.approval.appr.model.dto.cic.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.msb.bpm.approval.appr.constant.Constant;
import java.util.Date;
import lombok.Data;

@Data
public class NoVAMC {

  private String tenTctd;
  private Long duNoDaBanVnd;

  @JsonFormat(pattern = Constant.MMM_DD_YYYY_HH_MM_SS_A_FORMAT, timezone = Constant.BANGKOK_TIME_ZONE)
  private Date ngaySl;

}
