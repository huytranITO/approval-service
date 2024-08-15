package com.msb.bpm.approval.appr.enums.email;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : Hoang Anh Tuan (CN-SHQLQT)
 * @mailto : tuanha13@msb.com.vn
 * @created : 19/7/2023, Wednesday
 **/
@Getter
@RequiredArgsConstructor
public enum ReasonReturnType {
  RB0001("Thiếu hồ sơ pháp lý"),
  RB0002("Thiếu hồ sơ phương án vay vốn"),
  RB0003("Thiếu hồ sơ nguồn trả nợ"),
  RB0004("Thiếu hồ sơ tài sản đảm bảo"),
  RB0005("Thiếu hồ sơ ngân hàng"),
  RB0006("Thiếu hồ sơ vay tại các TCTD"),
  RB0007("Thiếu thông tin XMTĐ + Hình ảnh xác minh"),
  RB0008("Thiếu hồ sơ theo chương trình PDT"),
  RB0009("Thiếu phê duyệt bằng email/văn bản"),
  RB0010("Hồ sơ upload không đúng yêu cầu"),
  RB0011("Hồ sơ pháp lý không phù hợp"),
  RB0012("Hồ sơ nguồn thu không đáp ứng/thỏa mãn"),
  RB0013("Hồ sơ phương án vay không phù hợp"),
  RB0014("Hồ sơ TSBĐ không phù hợp quy định"),
  RB0015("Hồ sơ lịch sử tín dụng không phù hợp quy định"),
  RB0016("KH không thỏa tiêu chí cấp tín dụng"),
  RB0017("Hình ảnh thực địa không phù hợp quy định"),
  RB0018("Không liên lạc được KH,các bên liên quan theo SĐT cung cấp"),
  RB0019("Công ty/người liên hệ không đồng ý xác nhận hoặc xác nhận sai thông tin"),
  RB0020("Onhold hồ sơ, chờ ĐVKD liên lạc với KH/người liên quan"),
  RB0021("Chưa sắp xếp được lịch thẩm định thực tế"),
  RB0022("Thông tin nhập liệu trường dữ liệu chưa đầy đủ/chưa chính xác"),
  RB0023("Chấm sai xếp hạng tín dụng/Không thỏa xếp hạng tín dụng"),
  RB0024("Điều chỉnh, bổ sung đặc thù/ngoại lệ"),
  RB0025("ĐVKD xác định sai đối tượng cho vay/chủ sở hữu tài sản"),
  RB0026("ĐVKD trình không đúng luồng"),
  RB0027("ĐVKD gửi sai hồ sơ KH/trùng lặp hồ sơ"),
  RB0028("ĐVKD rút hồ sơ, không trình"),
  RB0029("ĐVKD tư vấn lại hồ sơ vay cho khách hàng"),
  RB0030("ĐVKD chờ tư vấn của các bộ phận liên quan"),
  RB0031("ĐVKD cập nhật thông tin trên BDS/Way4"),
  RB0032("KH không đồng ý phối hợp thực địa"),
  RB0033("KH không còn nhu cầu vay vốn/giải ngân"),
  RB0034("KH không đồng ý phối hợp thực địa"),
  RB0035("Chờ kết quả phê duyệt khoản vay khác"),
  RB0036("Cập nhật thông tin trên BDS/Way4"),
  RB0037("Lý do khác"),
  ;

  private final String value;

  public static String getListReasonByCode(List<String> names) {
    List<String> lstReasons = new ArrayList<>();
    names.forEach(e -> {
      ReasonReturnType reasonReturnType = ReasonReturnType.valueOf(e);
      lstReasons.add(reasonReturnType.getValue());
    });
    return String.join(";", lstReasons);
  }

}