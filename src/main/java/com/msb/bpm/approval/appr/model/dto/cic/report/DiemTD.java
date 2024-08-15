package com.msb.bpm.approval.appr.model.dto.cic.report;

import lombok.Data;

@Data
public class DiemTD {

  private String id;

  private String cicID;

  private String ngayHoiTin;

  private String ngayTraLoi;

  private String diem;

  private String hang;

  private String ngayCham;

  private String xepHangKH;

  private String moTaXepHangKH;

  private String clientQuestionId;
}
