package com.msb.bpm.approval.appr.model.dto.cic.report;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

@Slf4j
@Data
public abstract class CICReportData {
  private CICCustomer cus;

  private String ngayTraTin;
  private String note;
  private String ngayInBC;
  private String note1;
  private String note2;
  private String note3;
  private String msgNgayCapNhat;
  private String ngayCapNhat;
  private String ngayHoiTin;
  private List<String> list12Thang;
  private String sercurity;
  private String message;
  private String soLieuThang;

  private String ngayTraLoi;

  public Map<String, Object> parameters() {
    Map<String, Object> parameters = new HashMap<>();

    Field[] fields = this.getClass().getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      try {
        parameters.put(field.getName(), field.get(this));
      } catch (IllegalAccessException e) {
        log.error("Lỗi khi lấy giá trị trường " + field.getName(), e);
      }
    }

    Class supperClass = this.getClass().getSuperclass();
    if (supperClass == null) {
      return parameters;
    }

    fields = supperClass.getDeclaredFields();
    for (Field field : fields) {
      field.setAccessible(true);
      try {
        parameters.put(field.getName(), field.get(this));
      } catch (IllegalAccessException e) {
        log.error("Lỗi khi lấy giá trị trường " + field.getName(), e);
      }
    }

    return parameters;
  }
}
