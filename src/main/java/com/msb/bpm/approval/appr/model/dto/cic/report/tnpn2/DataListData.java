package com.msb.bpm.approval.appr.model.dto.cic.report.tnpn2;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.msb.bpm.approval.appr.constant.Constant;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

@Data
public class DataListData {

  private String bienPhapQuanLyTaiSan;
  private String chuSoHuu;
  private String cicID;
  private String clientQuestionId;
  private String cmndChuSoHuuTSBD;
  private String dkkdChuSoHuuTSBD;
  private BigDecimal giaTriTaiSanUSD;
  private BigDecimal giaTriTaiSanVND;
  private String loaiTaiSan;
  private String maSoTaiSan;
  private String moTaTaiSan;

  @JsonFormat(pattern = Constant.MMM_DD_YYYY_HH_MM_SS_A_FORMAT, timezone = Constant.BANGKOK_TIME_ZONE)
  private Date ngayGiaiChap;

  @JsonFormat(pattern = Constant.MMM_DD_YYYY_HH_MM_SS_A_FORMAT, timezone = Constant.BANGKOK_TIME_ZONE)
  private Date ngayHoiTin;

  @JsonFormat(pattern = Constant.MMM_DD_YYYY_HH_MM_SS_A_FORMAT, timezone = Constant.BANGKOK_TIME_ZONE)
  private Date ngaySl;

  @JsonFormat(pattern = Constant.MMM_DD_YYYY_HH_MM_SS_A_FORMAT, timezone = Constant.BANGKOK_TIME_ZONE)
  private Date ngayTheChap;

  @JsonFormat(pattern = Constant.MMM_DD_YYYY_HH_MM_SS_A_FORMAT, timezone = Constant.BANGKOK_TIME_ZONE)
  private Date ngayTraLoi;
  private String num;
  private String tenTCTD;
  private String thongTinKhac;

  private String loaiDuNo;
  private String stt;
  private String tsbdNo;
  private String tsbdYes;
  private String usd;
  private String vnd;
  private String maTcTD;
}
