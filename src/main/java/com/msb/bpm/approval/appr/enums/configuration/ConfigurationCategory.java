package com.msb.bpm.approval.appr.enums.configuration;

import lombok.Getter;

@Getter
public enum ConfigurationCategory {

  CIC_BRANCH_DATA("CIC_BRANCH_DATA"),
  PROCESSING_STEP("MD014") ,           // Bước xử lý
  LOAN_PURPOSE("MD066"),              // Mục đích vay
  RELATIONSHIP("RELATIONSHIP_DETAIL"),
  ISSUE_BY("MD009"), // Cấp bởi
  SEGMENT("MD002"), // Phân khúc khách hàng
  GENDER("MD001"), // Giới tính
  MARTIAL_STATUS("MD011"), // Tình trạng hôn nhân
  TYPE_OF_CUSTOMER("MD074"), // Đối tượng khách hàng
  DOCUMENT_TYPE("RB_MD008"), // Loại thông tin định danh (CCCD/CMND/Hộ chiếu....)
  RB_ADDRESS_TYPE_V001("RB_MD078"), // Hộ khẩu thường trú
  RB_ADDRESS_TYPE_V002("MD082"), // Nơi ở hiện tại
  EB_ADDRESS_TYPE_V001("EB_MD078"), // Địa chỉ trụ sở chính
  CREDIT_TYPE("MD034"), // Loại thông tin khoản vay
  GUARANTEE_FORM("MD042"), // Hình thức bảo đảm
  CARD_TYPE("MD046"), // Loại thẻ tín dụng
  AUTO_DEDUCT_RATE("MD044"), // Tỷ lệ thanh toán
  CREDIT_FORM("MD036"), // Hình thức cấp tín dụng
  CREDIT_CONDITION_GROUP("COND_GRP"), // Hình thức cấp tín dụng
  TIME_CONTROL_CREDIT("MD075"), // Hình thức cấp tín dụng
  CONTROL_CREDIT_UNIT("MD077"), // Hình thức cấp tín dụng
  PROCESS_FLOW("MD055"),    // Luồng trình
  SUBMISSION_PURPOSE("MD007"),    // Mục đích trình
  NATIONAL("nationality"),    // Quốc tịch,
  CARD_FORM("MD079"),   // hình thức thẻ
  CARD_RECEIVE_ADDRESS("MD080"),     // Dia chi nhan the
  CONVERSION_METHOD("MD023"),        // Phuong phap quy doi
  INCOME_TYPE("MD021"),     // Loai nguon thu
  PAY_TYPE("MD028"),      // hinh thuc tra luong
  LABOR_TYPE("MD029"),      // Loai hinh hop dong lao dong
  DETAIL_INCOME_OTHER("MD064"),   // Chi tiet nguon thu khac
  INCOME_METHOD("INCOME_METHOD"),   // Chi tiet nguon thu khac
  BUSINESS_TYPE("MD061"),       // Lọại hình kinh doanh
  LITERACY("LITERACY"),         // Trình độ học vấn
  BUSINESS_OWNER_STATUS("MD033"),   // Tình trạng sở hữu địa điểm kinh doanh
  ASSET_TYPE("MD030"),              // Loại tài sản
  RENTAL_PURPOSE("MD031"),          // Mục đích thuê
  CARD_BRANCH_MAPPING("CARD_BRANCH_MAPPING"),          // Mapping chi nhánh way4
  BRANCH_RECEIVE("BRANCH_RECEIVE"),
  MOCK_GENERATE("MOCK_GENERATE"),
  DOCUMENT_CODE("MD040"),  //Mã văn bản
  PILOT_SOURCE_MAPPING("PILOT_SOURCE_MAPPING"),
  RETURN_REASON_CODE("MD058"), //Lý do trả yêu cầu
  CLOSE_REASON_CODE("MD059") //Lý do đóng yêu cầu
  ;

  private final String code;

  ConfigurationCategory(String code) {
    this.code = code;
  }
}
