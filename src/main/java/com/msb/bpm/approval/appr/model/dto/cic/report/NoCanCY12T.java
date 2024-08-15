package com.msb.bpm.approval.appr.model.dto.cic.report;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.msb.bpm.approval.appr.constant.Constant;
import java.util.Date;
import lombok.Data;

@Data
public class NoCanCY12T {
  private String tenTctd;

  @JsonFormat(pattern = Constant.MMM_DD_YYYY_HH_MM_SS_A_FORMAT, timezone = Constant.BANGKOK_TIME_ZONE)
  private Date ngayHoiTin;
  private Long t12vnd;
  private Long t11vnd;
  private Long t10vnd;
  private Long t9vnd;
  private Long t8vnd;
  private Long t7vnd;
  private Long t6vnd;
  private Long t5vnd;
  private Long t4vnd;
  private Long t3vnd;
  private Long t2vnd;
  private Long t1vnd;
}
