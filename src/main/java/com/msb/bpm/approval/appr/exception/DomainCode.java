package com.msb.bpm.approval.appr.exception;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum DomainCode {
  SUCCESS("AS-000", "Success", HttpStatus.OK.value()),
  INVALID_PARAMETER("AS-001", "Invalid parameter: %s", HttpStatus.BAD_REQUEST.value()),
  INTERNAL_SERVICE_ERROR("AS-002", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  EXTERNAL_SERVICE_CLIENT_ERROR("AS-003", "External service error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  EXTERNAL_SERVICE_SERVER_ERROR("AS-004", "External service error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  TIMEOUT_ERROR("AS-005", "Timeout error", HttpStatus.GATEWAY_TIMEOUT.value()),
  FORBIDDEN("AS-006", "Forbidden", HttpStatus.FORBIDDEN.value()),
  TOKEN_EXPIRED("400004", "TOKEN_EXPIRED: %s", HttpStatus.UNAUTHORIZED.value()),
  TYPE_UNSUPPORTED("AS-007", "This type is unsupported", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
  NOT_FOUND_APPLICATION("AS-008", "Not found application", HttpStatus.NOT_FOUND.value()),
  NOT_FOUND_CUSTOMER("AS-009", "Not found customer", HttpStatus.NOT_FOUND.value()),
  NOT_FOUND_APPLICATION_FIELD_INFORMATION("AS-010", "Not found application field information", HttpStatus.NOT_FOUND.value()),
  REQUEST_TYPE_NOT_ACCEPTED("AS-011", "Request type not accepted", HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()),
  CAN_NOT_TRANSFER_TO_HANDLING_OFFICER("AS-012", "The application status is invalid, can't be transferred to Handling Officer", HttpStatus.BAD_GATEWAY.value()),
  CAN_NOT_TRANSFER_TO_COORDINATOR("AS-013", "The application status is invalid, can't be transferred to Coordinator", HttpStatus.BAD_GATEWAY.value()),
  SAVE_DRAFT_INFO("AS-100", "Save draft", HttpStatus.OK.value()),
  APPLICATION_NOT_COMPLETE("AS-016", "Application has not entered complete information", HttpStatus.BAD_REQUEST.value()),
  LIMIT_TOTAL_LOAN_UN_MATCH("AS-017", "Total loan amount do not match the credit limit", HttpStatus.BAD_REQUEST.value()),
  NOT_FOUND_PREV_HISTORY_APPROVAL("AS-018", "No previous approval history information found", HttpStatus.NOT_FOUND.value()),
  WRONG_USER("AS-019", "User without permission is processing records", HttpStatus.BAD_REQUEST.value()),
  NOT_FOUND_ID_FEEDBACK("AS-020", "Not feedback found", HttpStatus.NOT_FOUND.value()),
  APPLICATION_PREVIOUSLY_CLOSED("AS-500", "Application closed", HttpStatus.BAD_REQUEST.value()),
  APPLICATION_CLOSED("AS-0099", "Application has been closed at Digi-Lending system, please check again", HttpStatus.BAD_REQUEST.value()),
  APPLICATION_APPROVED("AS-9999", "Application has been completed at Digi-Lending system, please check again", HttpStatus.BAD_REQUEST.value()),
  INVALID_BPM_CIF_LIST_ERROR("AS-021", "Danh sách bpmCifs không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  FORM_TEMPLATE_GENERATE_NOT_DONE("AS-022", "Form creation in progress, please wait a few minutes", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  CAN_NOT_ASSIGN_SAME_USER("AS-023", "Application has been assigned to officer, please choose another officer to reassign the application", HttpStatus.BAD_REQUEST.value()),
  CAN_NOT_SEND_FEEDBACK("AS-024", "You can not send feedback to customer", HttpStatus.BAD_REQUEST.value()),
  NOT_FOUND_APPLICATION_DRAFT("AS-025", "Not found application draft", HttpStatus.NOT_FOUND.value()),
  REGULATORY_CODE_EMPTY("AS-026", "You have to enter regulatory code", HttpStatus.BAD_REQUEST.value()),
  RM_NOT_CHECK_ORIGINAL("AS-027", "RM not check original", HttpStatus.BAD_REQUEST.value()),
  INVALID_REGULATORY("AS-028", "You must check the regulatory code again", HttpStatus.BAD_REQUEST.value()),
  CUSTOM_INVALID_PARAMETER("AS-029", "Invalid parameter: %s", HttpStatus.BAD_REQUEST.value()),
  /**
   * CIC, AML, OPRISK, CSS, USER_MANAGER, OTP, CHECKLIST, CMS, CUSTOMER error AS-200 -> AS-300
   */
  CIC_SERVICE_ERROR("AS-200", "Lỗi hệ thống CIC: %s", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  NOT_FOUND_CIC("AS-201", "Not found CIC", HttpStatus.NOT_FOUND.value()),
  ERROR_SEARCH_CIC("AS-202", "Error search CIC code", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  NOT_FOUND_AML_OPR("AS-203", "Not found AML|OPR", HttpStatus.NOT_FOUND.value()),
  OPRISK_SERVICE_ERROR("AS-204", "Lỗi hệ thống OPRISK", HttpStatus.BAD_GATEWAY.value()),
  AML_SERVICE_ERROR("AS-207", "Lỗi hệ thống AML", HttpStatus.BAD_GATEWAY.value()),
  INVALID_INPUT_AML_OPRISK_ERROR("AS-228", "Đầu vào truy vấn AML & OPRISK không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  CIC_NOT_IMPLEMENT("AS-208", "Tính năng tra cứu CIC đang phát triển", HttpStatus.BAD_REQUEST.value()),
  CIC_IDENTIFY_NOT_BLANK("AS-250", "Mã định danh không được để trống", HttpStatus.BAD_REQUEST.value()),
  CIC_REF_CUSTOMER_ID_NOT_NULL("AS-251", "Ref_customer_id được để trống", HttpStatus.BAD_REQUEST.value()),
  CIC_DUPLICATE_IDENTIFY("AS-252", "Mã định danh bị trùng", HttpStatus.BAD_REQUEST.value()),
  SYNCING_CIC("AS-253", "Hệ thống đang đồng bộ thông tin CIC", HttpStatus.BAD_REQUEST.value()),
  ERROR_CIC("AS-255", "%s", HttpStatus.BAD_REQUEST.value()),
  /**
   * CSS error
   */
  NOT_FOUND_CREDIT_RATING_DTL("AS-205", "Not found credit rating detail", HttpStatus.NOT_FOUND.value()),
  NOT_FOUND_CREDIT_RATING("AS-206", "Not found credit rating", HttpStatus.NOT_FOUND.value()),
  CANNOT_AUTHEN("DS50000", "Request thiếu thông tin authen", HttpStatus.UNAUTHORIZED.value()),
  AUTHEN_FAILURE("CSS_002", "Lỗi xác thực", HttpStatus.UNAUTHORIZED.value()),
  MISSING_INFO("CSS_003", "Thiếu thông tin", HttpStatus.BAD_REQUEST.value()),
  INVALID_INPUT("CSS_004", "Thông tin không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  DUPLICATE_REQUEST_TIME("CSS_005", "Trùng request time", HttpStatus.BAD_REQUEST.value()),
  REQUEST_TIME_WRONG_DATA_FORMAT("CSS_006", "Request time sai định dạng", HttpStatus.BAD_REQUEST.value()),
  INVALID_LENGTH("CSS_007", "Độ dài không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  NO_SCORE("CSS_011", "Chưa có điểm", HttpStatus.BAD_REQUEST.value()),
  CLOSED_PROFILE("CSS_012", "Hồ sơ đã bị đóng", HttpStatus.BAD_REQUEST.value()),
  ID_NOT_EXIST("CSS_013", "Id không tồn tại", HttpStatus.NOT_FOUND.value()),
  NOT_MATCH_REGIS_NUM("CSS_014", "Mã số doanh nghiệp không khớp", HttpStatus.BAD_REQUEST.value()),
  NOT_MATCH_LEGAL_NO("CSS_015", "Mã số định danh của khách hàng không khớp", HttpStatus.BAD_REQUEST.value()),
  OTHER("CSS_016", "Lỗi khác", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  CSS_SYSTEM_ERROR("CSS_999", "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  CSS_VERIFY_INVALID("CSS_998", "Chưa hoàn thành chấm điểm CSS", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  /**
   * User-Manager
   */
  GET_PERMISSION_ERROR("AS-208", "Lỗi check quyền từ user manager", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  GET_REGION_AREA_ERROR("AS-209", "Lỗi lấy thông tin vùng miền từ user manager, HttpStatus.OK.value()", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  ERR_GET_ORGANIZATION_BY_USER("AS-223","Doesn't exist or has more than one user's organization", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  GET_USER_BY_ROLES_ERROR("AS-224", "Error get user by roles and organization code", HttpStatus.BAD_REQUEST.value()),
  GET_USER_BY_USERNAME_ERROR("AS-225", "Error get user by username", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  FIND_ORGANIZATION_ERROR("AS-254", "Error get organization", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  ERR_DUPLICATE_BUSINESSNUMBER("AS-256", "Mã số ĐKKD không được trùng lặp ", HttpStatus.BAD_REQUEST.value()),
  /**
   * OTP
   */
  VERIFY_OTP_ERROR("AS-210", "Lỗi verify OTP", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  /**
   * CHECK_LIST
   */
  CHECKLIST_ERROR("AS-211", "Lỗi hệ thống Checklist", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  GENERATE_CHECKLIST_ERROR("AS-212", "Lỗi sinh danh mục hồ sơ tự động", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  SAVE_CHECKLIST_ERROR("AS-213", "Lỗi cập nhật danh mục hồ sơ", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  RELOAD_CHECKLIST_ERROR("AS-214", "Lỗi tải lại danh mục", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  ADDITIONAL_DATA_ERROR("AS-215", "Lỗi cập nhật dữ liệu bổ sung danh mục", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  DELETE_CHECKLIST_GROUP_ERROR("AS-216", "Lỗi xóa nhóm danh mục", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  HISTORY_FILE_ERROR("AS-217", "Lỗi lấy lịch sử file", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  DELETE_FILE_ERROR("AS-218", "Lỗi xóa file", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  GET_GROUP_ERROR("AS-219", "Lỗi lấy danh sách nhóm danh mục", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  UPDATE_CHECKLIST_VERSION_ERROR("AS-220", "Lỗi cập nhật version danh mục", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  GET_PRE_SIGNED_ERROR("AS-221", "Lỗi lấy chữ ký file từ MinIO", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  NOT_FOUND_CHECKLIST_ERROR("AS-222", "Không tìm thấy thông tin danh mục", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  GET_BY_REQUEST_CODE("AS-223", "Lỗi lấy thông tin checklist", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  GET_SIZE_FILE_MINIO_ERROR("AS-226", "Lỗi lấy kích thước file từ minIO", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  COPY_BUCKET_FILE_MINIO_ERROR("AS-227", "Lỗi sao chép file minIO giữa các bucket", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  SEARCH_ERROR("AS-314", "Lỗi gọi checklist lấy danh sách code", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  /**
   * CMS
   */
  NOT_EXIST_FILES_IN_MINIO("AS-229", "Không tồn tại các file sau trên MinIO", HttpStatus.BAD_REQUEST.value()),
  INVALID_PATH_FILES_MINIO("AS-230", "Đường dẫn file MinIO không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  SEARCH_CUSTOMER_ERROR("AS-231", "Lỗi tìm kiếm khách hàng", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  CREATE_CUSTOMER_ERROR("AS-232", "Lỗi tạo mới khách hàng", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  SYSTEM_CUSTOMER_ERROR("AS-233", "Lỗi hệ thống Customer", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  UPDATE_CUSTOMER_ERROR("AS-234", "Lỗi cập nhật khách hàng", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  EXIST_REFID_IN_APPLICATION_BUT_CLOSE("AS-235", "Đã tồn tại refId trong hồ sơ chưa đóng", HttpStatus.BAD_REQUEST.value()),
  INVALID_INPUT_IDENTITY_NUMBER("AS-236", "Mã định danh khách hàng không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  NOT_FOUND_CUSTOMER_BY_IDENTITY_NUMBER("AS-237", "Không tìm thấy thông tin khách hàng theo mã định danh", HttpStatus.BAD_REQUEST.value()),
  INVALID_INPUT_UNSECURED_LOAN("AS-238", "Khoản vay không tài sản bảo đảm có giá trị khoản vay không hợp lệ", HttpStatus.BAD_REQUEST.value()),
  LEAST_ONE_CIC_LOOKUP("AS-239", "At least one CIC lookup message is required, please double check before completing the request", HttpStatus.BAD_REQUEST.value()),
  EXIST_CIC_NO_RESULT("AS-240", "There is a CIC lookup message with no results, please check again before completing the request", HttpStatus.BAD_REQUEST.value()),
  NOT_FOUND_APPLICATION_BY_REFID("AS-241", "Không tìm thấy thông tin hồ sơ theo refId", HttpStatus.BAD_REQUEST.value()),
  NOT_FOUND_CUSTOMER_BY_REFCUSID("AS-242", "Không tìm thấy thông tin khách hàng theo refCusId", HttpStatus.BAD_REQUEST.value()),
  UPDATE_APPLICATION_ERROR_BY_LDP_STATUS("AS-243", "Lỗi update thông tin hồ sơ theo ldpStatus", HttpStatus.BAD_REQUEST.value()),
  NUMBER_OF_CONTACT_ERROR("AS-312", "Vượt quá số lượng người liên hệ", HttpStatus.BAD_REQUEST.value()),
  /**
   * Authority error AS-300 -> AS-400
   */
  AUTHORITY_SERVICE_ERROR("AS-300", "Lỗi hệ thống Authority", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  AUTHORITY_APPLICATION_ERROR("AS-313", "Không có thẩm quyền phù hợp cho hồ sơ này. Xin vui lòng kiểm tra lại thông tin hồ sơ!", HttpStatus.BAD_REQUEST.value()),

  /**
   * Rule error
   */
  RULE_MANAGEMENT_ERROR("RMS-500","Calling rule management occurred exception", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  NOT_FOUND_AUTHORITY("AS-301", "Not found authority for credit", HttpStatus.UNAUTHORIZED.value()),
  AUTHORITY_NOT_MATCHING_WITH_LOAN("AS-302", "Invalid loan approval authority", HttpStatus.FORBIDDEN.value()),
  NOT_FOUND_RULE_CONDITION_TRANSITIONS("AS-303", "Not found decision of the transition condition", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  RULE_CODE_MUST_NOT_NULL("AS-304", "Rule code must not null", HttpStatus.BAD_REQUEST.value()),
  NOT_FOUND_AUTHORITY_FOR_CURRENT_USER("AS-305", "Not found authority for current user", HttpStatus.NOT_FOUND.value()),
  INSUFFICIENT_AUTHORITY_TO_APPROVE("AS-306", "User insufficient authority to approve the application", HttpStatus.FORBIDDEN.value()),
  NOT_FOUND_USER_RECEPTION("AS-307", "Not found user reception information", HttpStatus.BAD_REQUEST.value()),
  USER_RECEPTION_NOT_ACTIVE("AS-308", "User reception not active", HttpStatus.BAD_REQUEST.value()),
  USER_DONT_HAVE_PERMISSION("AS-309", "User do not have permission", HttpStatus.FORBIDDEN.value()),
  NOT_FOUND_USER("AS-310", "User does not exist on the Digi-Lending system, please check again", HttpStatus.BAD_REQUEST.value()),
  NUMBER_OF_CUSTOMER_IS_INVALID("AS-193", "Một trong số các danh sách định danh của Khách hàng/NLQ: %s đã được khai báo là định danh của khách hàng khác trên hệ thống Digi-Lending. Vui lòng kiểm tra lại!",
      HttpStatus.BAD_REQUEST.value()),
  /**
   * Camunda error
   */
  CAMUNDA_SYSTEM_ERROR("CMD-999", "Camunda system error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  NOT_FOUND_CAMUNDA_PROCESS("CMD-001", "Not found camunda process", HttpStatus.NOT_FOUND.value()),
  NOT_FOUND_CAMUNDA_TASK_WAIT_MINUTE("CMD-002", "Not found camunda task, please wait a minute", HttpStatus.NOT_FOUND.value()),
  CAMUNDA_STATUS_ERROR("CMD-003", "Current status of profile is invalid, please contact system administrator!", HttpStatus.BAD_REQUEST.value()),

  /**
   * Matrix workflow
   */
  DONT_ALLOW_STEP_TRANSITIONS("WFL-001", "Don't allow step transitions", HttpStatus.BAD_REQUEST.value()),


  /**
   * way4
   */
  CREATE_OR_RETRY_CARD_ERROR("CARD-001", "Create or retry card error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  CREATE_OR_RETRY_CLIENT_ERROR("CARD-002", "Create or retry client error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  COMPOSE_CARD_INFO_ERROR("CARD-003", "Compose card info error", HttpStatus.INTERNAL_SERVER_ERROR.value()),


  DONT_ALLOW_CJBO_SOURCE("INS-001", "Don't allow CJBO source", HttpStatus.BAD_REQUEST.value()),

  /**
   * Historic Integration
   */
  NOT_FOUND_HISTORIC_INTEGRATION("HI-001", "Integration information not found", HttpStatus.NOT_FOUND.value()),
  INVALID_DATA_SIZE("HI-002", "Invalid data size", HttpStatus.BAD_REQUEST.value()),
  BLANK_SEARCH_INFORMATION("HI-003", "Blank search information", HttpStatus.BAD_REQUEST.value()),
  ERROR_DATA_RETRY("HI-004", "Error information retry", HttpStatus.BAD_REQUEST.value()),

  /**
   * ESB Core Integration
   */
  ESB_CORE_CLIENT_ERROR("CIT-001", "Core client error", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  ESB_ACCOUNT_INFO_ERROR("CIT-002", "Account info error", HttpStatus.BAD_REQUEST.value()),


  NOT_BELONG_TO_PILOT_SCOPE("AS-311", "Hồ sơ không thuộc phạm vi Pilot", HttpStatus.BAD_REQUEST.value()),
  BELONG_TO_PILOT_SCOPE("AS-312", "Hồ sơ thuộc phạm vi Pilot", HttpStatus.BAD_REQUEST.value()),
  RM_IS_NOT_EXIST("AS-310", "RM không tồn tại trên hệ thống Digi-Lending", HttpStatus.BAD_REQUEST.value()),
  RM_IS_INACTIVE("AS-308", "RM không ở trạng thái hoạt động", HttpStatus.BAD_REQUEST.value()),
  RM_DOESNT_HAVE_PERMISSION("AS-403", "RM không được phân quyền xử lý hồ sơ", HttpStatus.BAD_REQUEST.value()),
  RM_HAVE_A_LOT_OF_BUSINESS_UNIT("AS-404", "RM thuộc nhiều hơn một ĐVKD", HttpStatus.BAD_REQUEST.value()),

  /**
   * Account Info
   */
  INVALID_CIF_VALUE("AI-001", "Yêu cầu đồng bộ thông tin CIF từ core", HttpStatus.BAD_REQUEST.value()),
  NOT_FOUND_CUSTOMER_FROM_AI("AI-002", "Không tìm thấy khách hàng hoặc người liên quan từ hệ thống customer", HttpStatus.BAD_REQUEST.value()),
  /**
   * Collateral
   */
  CMS_CREATE_ASSET_ERROR("COL-001",
      "The collateral system encountered a problem and could not create asset information. Something went wrong. Please try again later.",
      HttpStatus.BAD_REQUEST.value()),

  ALLOCATION_ASSET_ERROR("COL-002",
          "Allocation invalid!",
          HttpStatus.BAD_REQUEST.value()),

  ALLOCATION_GET_ASSET_ERROR("COL-003",
          "Get allocation error!",
          HttpStatus.INTERNAL_SERVER_ERROR.value()),

  ALLOCATION_SAVE_ASSET_ERROR("COL-004",
          "Save allocation info error!",
          HttpStatus.INTERNAL_SERVER_ERROR.value()),

  ALLOCATION_SAVE_VALUATION_ERROR("COL-005",
          "Save allocation valuation error!",
          HttpStatus.INTERNAL_SERVER_ERROR.value()),
  GET_ASSET_RULE_CHECKLIST_ERROR("COL-006",
          "Get asset rule checklist error",
          HttpStatus.INTERNAL_SERVER_ERROR.value()),
  ASSET_INVALID_ERROR("COL-007",
          "%s",
          HttpStatus.OK.value()),
  ASSET_ERROR("COL-008",
          "%s",
          HttpStatus.OK.value()),
  /**
   * CAS
   */
  CAS_SUCCESS("LI-000", "Success", HttpStatus.OK.value()),
  CAS_SYSTEM_ERROR("LI-001", "CAS Error", HttpStatus.BAD_REQUEST.value()),

  ERROR_CREATE_CUSTOMER("AS-315", "Error create customer", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  ERROR_UPDATE_CUSTOMER("AS-316", "Error update customer", HttpStatus.INTERNAL_SERVER_ERROR.value()),

  SEND_NOTIFICATION_ERROR("NO-000", "Send notification Error", HttpStatus.BAD_REQUEST.value()),

  ERROR_CUSTOMER_VERSION_NOT_FOUND("CBT-001", "Notification for CBT: Not found customer with version", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  CIFI_NOT_COMPLETE("CBT-002", "Not submit application when CIFI not complete", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  CIFI_ERROR("CBT-003", "Call CIFI fail", HttpStatus.INTERNAL_SERVER_ERROR.value()),

  ERROR_CREATE_ENTERPRISE_CUSTOMER_RELATIONSHIP("AS-405", "Response of api create enterprise customer relationship is null", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  NOT_FOUND_RELATION_GROUP("AS-406", "Not found relationship group with relationDetail {}", HttpStatus.INTERNAL_SERVER_ERROR.value()),
  INVALID_LOAN_PURPOSE("AS-407", "Invalid loan purpose", HttpStatus.BAD_REQUEST.value()),
  ;

  private final String code;
  private final String message;
  private final int status;

  public static DomainCode get(String code) {
    return Arrays.stream(DomainCode.values()).filter(e -> StringUtils.equals(e.getCode(), code))
            .findFirst().orElse(null);
  }
}
