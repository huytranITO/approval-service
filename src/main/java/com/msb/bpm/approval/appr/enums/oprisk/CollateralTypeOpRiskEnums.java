package com.msb.bpm.approval.appr.enums.oprisk;

import lombok.Getter;

@Getter
public enum CollateralTypeOpRiskEnums {

  REAL_ESTATE_ASSET("V001", "1", "Bất động sản"),
  TRANSPORT_ASSET("V002", "2", "Phương tiện vận tải và xe chuyên dùng"),
  MACHINES_ASSET("V003", "10", "Máy móc thiết bị, dây chuyền sản xuất"),
  VALUABLE_PAPERS_ASSET("V004", "3", "Giấy tờ có giá"),
  OTHER_ASSET("V006", "7", "Tài sản khác"),
  GOODS_ASSET("V007", "5", "Hàng hóa"),
  MONEY_ASSET("V011", "3", "Tiền")
  ;
  private final String groupType;
  private final String name;
  private final String opRiskType;

  CollateralTypeOpRiskEnums(String groupType, String opRiskType, String name) {
    this.groupType = groupType;
    this.name = name;
    this.opRiskType = opRiskType;
  }

  public static String getCollateralType(String code) {
    for (CollateralTypeOpRiskEnums a : CollateralTypeOpRiskEnums.values()) {
      if (a.getGroupType().equals(code)) {
        return a.getOpRiskType();
      }
    }
    return OTHER_ASSET.getOpRiskType();
  }
}
