package com.msb.bpm.approval.appr.enums.card;

import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum IntegrationStatusDetail {
  NEW("Chờ tích hợp"),
  INPROGESS_CLIENT("Chờ tạo client"),
  INPROGESS_CARD("Chờ tạo thẻ chính"),
  INPROGESS_SUB_CARD("Chờ tạo thẻ phụ"),
  ERR_CLIENT("Lỗi tạo client"),
  ERR_CARD("Lỗi tạo thẻ chính"),
  ERR_SUB_CARD("Lỗi tạo thẻ phụ"),
  ANOTHER_ERROR("Lỗi tích hợp khác"),
  CIF_ERR("Lỗi chưa có CIF"),
  TIMEOUT("Lỗi Timeout"),
  SUCESSFULL("Hoàn thành");

  private String value;

  IntegrationStatusDetail(String value) {
    this.value = value;
  }

  public static IntegrationStatusDetail decode(final String value) {
    return Stream.of(IntegrationStatusDetail.values())
        .filter(targetEnum -> targetEnum.value.equals(value)).findFirst().orElse(null);
  }
}
