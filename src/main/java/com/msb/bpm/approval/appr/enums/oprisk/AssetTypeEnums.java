package com.msb.bpm.approval.appr.enums.oprisk;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum AssetTypeEnums {

  ACCOUNT_BALANCE("V101","Số dư tài khoản tại MSB"),
  CASH("V102","Các loại tiền mặt"),

  DEPOSITS("V201","Tiền gửi"),
  CONDOMINIUM("V202","Séc"),
  STOCK("V203","Cổ phiếu"),
  VALUABLE_PAPERS_OTHER("V204","Khác"),

  STOCK_MSB("V301","Do MSB phát hành"),
  STOCK_NHNN("V302","Do CP/BTC/NHNN phát hành"),
  STOCK_TCTD("V303","Do TCTD khác phát hành"),
  STOCK_DN("V304","Do Tổ chức kinh tế/Doanh nghiệp phát hành"),
  STOCK_LOCAL("V305","Do chính quyền địa phương phát hành"),

  LANDSCAPE("V401","Đất ở"),
  MIX_LAND("V402","Đất hỗn hợp"),
  PROJECT_LAND("V403","Đất dự án"),
  APARTMENT("V404","Căn hộ chung cư"),
  PRODUCTIVE_LAND("V405","Đất sản xuất kinh doanh"),
  AGRICULTURAL_LAND("V406","Đất nông nghiệp"),

  RIGHT_TO_BUSINESS_ADDRESS("V501","Quyền sở hữu/sử dụng địa điểm kinh doanh"),
  PROPERTY_RIGHTS_ARISE("V502","Quyền tài sản phát sinh từ HĐMB/chuyển nhượng/thuê dài hạn…"),

  TOURIST_CAR("V601","Xe du lịch"),
  TRUCK_CAR("V602","Xe tải"),
  SPECIAL_VEHICLES("V603","Xe chuyên dùng"),
  SHIP("V604","Tàu biển"),
  OTHER_CAR("V605","PTVT khác"),

  PRODUCTION_LINE("V701","Dây chuyền sản xuất"),
  CONSTRUCTION_EQUIPMENT("V702","Phương tiện thi công"),
  MACHINERY_EQUIPMENT("V703","Máy móc thiết bị riêng lẻ"),

  DREAM_ASSET("V801","Nhà ở hình thành trong tương lai của chủ đầu tư dự án xây dựng nhà ở"),


  LABOR_CONTRACT_RIGHTS("V881","Quyền hợp đồng lao động"),

  OTHER_ASSET("V901","Tài sản khác"),
  GOODS_ASSET("V902","Hàng hóa"),
  RECEIVABLE_ASSET("V903","Khoản phải thu, đòi nợ"),
  GOLD_ASSET("V904","Vàng miếng/kim loại quý"),
  CONTRIBUTION_ASSET("V905","Phần vốn góp");




  private final String code;
  private final String name;
  AssetTypeEnums(String code, String name) {
    this.code = code;
    this.name = name;
  }
  public static boolean validCode(String code) {
    for (AssetTypeEnums a : AssetTypeEnums.values()) {
      if (a.getCode().equals(code)) {
        return true;
      }
    }
    return false;
  }

  public static String getAssetTypeName(String code){
    return
        Arrays.stream(AssetTypeEnums.values())
            .filter(a -> a.getCode().equals(code))
            .findFirst()
            .map(AssetTypeEnums::getName)
            .orElse(null);
  }
}

