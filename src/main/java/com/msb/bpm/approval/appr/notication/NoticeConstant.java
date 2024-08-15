package com.msb.bpm.approval.appr.notication;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

/*
* @author: BaoNV2
* @since: 16/10/2023 12:46 PM
* @description:  
* @update:
*
* */
@Component
public class  NoticeConstant {
  public final static String BIZ_TYPE = "BIZ";
  public final static String PD_NEW = "PD_NEW";
  public final static String CIC = "CIC";
  public final static String SYSTEM = "SYSTEM";
  public final static String PD_RB = "mercury_rb";
  public final static String USER = "USER";
  public final static String LDP_SUCCESS_TITLE = "LDP_SUCCESS_TITLE";
  public final static String FORM_TEMPLATE_SUCCESS_TITLE = "FORM_TEMPLATE_SUCCESS_TITLE";
  public final static String FORM_TEMPLATE_ERROR_TITLE = "FORM_TEMPLATE_ERROR_TITLE";
  public final static String CAS_SUCCESS_TITLE = "CAS_SUCCESS_TITLE";
  public final static String CIC_PDF_TITLE = "CIC_PDF_TITLE";
  public final static String CIC_RATIO_TITLE = "CIC_RATIO_TITLE";
  public final static String LDP_SUCCESS_CONTENT = "LDP_SUCCESS_CONTENT";
  public final static String FORM_TEMPLATE_SUCCESS_CONTENT = "FORM_TEMPLATE_SUCCESS_CONTENT";
  public final static String FORM_TEMPLATE_ERROR_CONTENT = "FORM_TEMPLATE_ERROR_CONTENT";
  public final static String CAS_SUCCESS_CONTENT = "CAS_SUCCESS_CONTENT";
  public final static String CIC_PDF_CONTENT = "CIC_PDF_CONTENT";
  public final static String CIC_RATIO_CONTENT = "CIC_RATIO_CONTENT";
  public final static String CIC_RATIO_CONTENT_UPDATE = "CIC_RATIO_CONTENT_UPDATE";
  public static Map<String, String> titles = new HashMap<>();
  public static Map<String, String> contents = new HashMap<String, String>();

  @PostConstruct
  void init() {
    titles.put(LDP_SUCCESS_TITLE, "[Digi-lending - %s- Hồ sơ mới từ LDP]");
    titles.put(FORM_TEMPLATE_SUCCESS_TITLE, "[Digi-lending - %s- Sinh mẫu biểu thành công]");
    titles.put(FORM_TEMPLATE_ERROR_TITLE, "[Digi-lending - %s - Sinh mẫu biểu thất bại]");
    titles.put(CAS_SUCCESS_TITLE, "[Digi-lending - %s- Lấy dữ liệu thành công]");
    titles.put(CIC_PDF_TITLE, "[Digi-lending - %s - Lấy bản tin CIC thành công]");
    titles.put(CIC_RATIO_TITLE, "[Digi-lending - %s - Lấy dữ liệu chỉ tiêu CIC thành công]");

    contents.put(LDP_SUCCESS_CONTENT, "Thông báo hệ thống vừa tiếp nhận hồ sơ mới hoặc khách hàng bổ sung hồ sơ từ Lending-Page của khách hàng [%s]");
    contents.put(FORM_TEMPLATE_SUCCESS_CONTENT, "Thông báo hệ thống đã sinh mẫu biểu thành công cho hồ sơ [%s]");
    contents.put(FORM_TEMPLATE_ERROR_CONTENT, "Thông báo hệ thống đã sinh mẫu biểu thất bại");
    contents.put(CAS_SUCCESS_CONTENT, "Thông báo đã có kết quả tra cứu xếp hạng tín dụng của khách hàng với ID xếp hạng = [%s]. Bạn vui lòng tải lại trang để lấy dữ liệu mới nhất");
    contents.put(CIC_PDF_CONTENT, "Thông báo đã có kết quả tra cứu bản tin CIC của khách hàng. Vui lòng đồng bộ để đính kèm bản tin PDF");
    contents.put(CIC_RATIO_CONTENT, "Thông báo đã có kết quả tra cứu chỉ tiêu CIC của khách hàng");
    contents.put(CIC_RATIO_CONTENT_UPDATE, "Thông báo đã có kết quả tra cứu bản tin CIC của khách hàng. Vui lòng kiểm tra kết quả và thao tác tạo lại mẫu biểu (nếu có)");
  }
}
