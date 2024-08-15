package com.msb.bpm.approval.appr.constant;

import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access= AccessLevel.PRIVATE)
public final class ApplicationConstant {

  public static final String DEFAULT_SOURCE = "BPM";

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class Customer {

    public static final String RB = "RB";
    public static final String EB = "EB";
  }

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class ApplicationCredit {
    public static final String LOAN = "V001";
    public static final String OVERDRAFT = "V002";
    public static final String CARD = "V003";
    public static final BigDecimal LIMIT_CARD_AMOUNT = new BigDecimal("2000000000");
  }

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class PostBaseRequest {

    public static final String TYPE = "type";
    public static final String POST_CREATE_APPLICATION = "POST_CREATE_APPLICATION";
    public static final String POST_INITIALIZE_INFO = "POST_INITIALIZE_INFO";
    public static final String POST_FIELD_INFO = "POST_FIELD_INFO";
    public static final String POST_DEBT_INFO = "POST_DEBT_INFO";
    public static final String ASSIGN_APPLICATION = "ASSIGN_APPLICATION";
    public static final String POST_CLOSE_APP = "POST_CLOSE_APP";
    public static final String POST_REWORK_APPLICATION = "POST_REWORK_APPLICATION";
    public static final String POST_SUBMIT_APP = "POST_SUBMIT_APP";
    public static final String POST_CHECK_LIST_TAB = "POST_CHECK_LIST_TAB";
    public static final String POST_ASSIGN_TO_TL = "POST_ASSIGN_TO_TL";
    public static final String POST_CREDIT_RATING = "POST_CREDIT_RATING";
    public static final String POST_CREDIT_RATING_V2 = "POST_CREDIT_RATING_V2";
    public static final String POST_QUERY_APPLICATION_BY_CUSTOMER = "POST_QUERY_APPLICATION_BY_CUSTOMER";
    public static final String POST_QUERY_CIC_INFO = "POST_QUERY_CIC_INFO";
    public static final String POST_SYNC_AML_OPR_INFO = "POST_SYNC_AML_OPR_INFO";
    public static final String POST_COMPLETE = "POST_COMPLETE";
    public static final String GET_PREVIEW_FORM = "GET_PREVIEW_FORM";
    public static final String CMS_CREATE_APPLICATION = "CMS_CREATE_APPLICATION";
    public static final String CMS_PUSH_DOCUMENTS = "CMS_PUSH_DOCUMENTS";
    public static final String POST_ASSET_INFO = "POST_ASSET_INFO";
    public static final String POST_SYNC_OPR_ASSET = "POST_SYNC_OPR_ASSET";
    public static final String GET_OPR_DETAIL = "GET_OPR_DETAIL";
  }

  @NoArgsConstructor(access = AccessLevel.PRIVATE)
  public static final class ApplicationIncomeConstant {

    public static final String ACTUALLY = "ACTUALLY";
    public static final String EXCHANGE = "EXCHANGE";
  }

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class ActuallyIncomeDTO {

    public static final String INCOME_TYPE = "incomeType";
    public static final String SALARY = "V001";
    public static final String RENTAL = "V002";
    public static final String INDIVIDUAL_BUSINESS = "V003";
    public static final String ENTERPRISE_BUSINESS = "V004";
    public static final String OTHER = "V005";
    public static final String PROPERTY_BUSINESS = "V006";
  }

  public static final String APPLICATION_TAG = "APPLICATION";

  public static final String INCOME_TAG = "INCOME";
  public static final String CUSTOMER_TAG = "CUSTOMER";
  public static final String CUSTOMER_RELATIONS_TAG = "CUSTOMER_RELATIONS";
  public static final String ENTERPRISE_RELATIONS_TAG = "ENTERPRISE_RELATIONS";
  public static final String AML_TAG = "AML";
  public static final String OPR_TAG = "OPR";
  public static final String OPR_ASSET_TAG = "OPR_ASSET";


  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class TabCode {

    public static final String INITIALIZE_INFO = "initialize_info";
    public static final String FIELD_INFO = "field_info";
    public static final String DEBT_INFO = "debt_info";
    public static final String DOCUMENTS_INFO = "documents_info";
    public static final String ASSET_INFO = "asset_info";
    public static final String CHECKLIST_ASSET_INFO = "checklist_asset_info";

    @NoArgsConstructor(access= AccessLevel.PRIVATE)
    public static final class BlockKey {

      public static final String OTHER_REVIEW = "other_review";
      public static final String FORM_TEMPLATE = "form_template";
    }
  }

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class ApplicationDraftStatus {

    public static final int FINISHED = 1;
    public static final int UNFINISHED = 0;
  }

  public static final String NOT_ON_THE_LIST = "Không thuộc danh sách";
  public static final String ON_THE_LIST =  "Thuộc danh sách";
  public static final String RESULT_QUERY_ERROR =  "Hỏi tin lỗi";
  public static final String QUERY_ERROR_CODE =  "-1";
  public static final String NOT_FOUND_INFO = "Không có thông tin";

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class TopicKafka {
    public static final String GENERAL_INFO_KEY = "general-info";
    public static final String BPM_APPROVAL_RB_INFO_KEY = "bpm-approval-rb-info";
    public static final String BPM_FEEDBACK_APPLICATION_CMS = "bpm-feedback-application-cms";
    public static final String BPM_LEAD_INSURANCE = "bpm_lead_insurances";
    public static final String BPM_NOTIFICATION = "bpm-notification";
  }

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class OtpSignRequest {
    public static final String OTP_SIGN_SUBMIT_APP = "OTP_SIGN_SUBMIT_APP";

  }

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class MetaDataKey {
    public static final String APPLICATION = "application";
    public static final String APPLICATION_BPM_ID = "applicationBpmId";
    public static final String POST_BASE_REQUEST = "postBaseRequest";
    public static final String TOKEN = "token";
    public static final String REASONS = "reasons";
    public static final String APPLICATION_BPM_IDS = "applicationBpmIds";
    public static final String ASSIGNEE = "assignee";
    public static final String LDP_CONFIRM_STATUS = "LdpConfirmStatus";
    public static final String LDP_REASON = "LdpReason";
    public static final String CUSTOMER_NAME = "CustomerName";
  }

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class ParamsEmailCommon {
    public static final String CUSTOMER_NAME = "CUSTOMER_NAME";
    public static final String BPM_ID = "BPM_ID";
    public static final String ASSIGNEE = "ASSIGNEE";
    public static final String REASON = "REASON";
    public static final String ASSIGNER = "ASSIGNER";
    public static final String DOCUMENT_CODE = "DOCUMENT_CODE";
    public static final String APPLICATION_SOURCE = "ApplicationSource";
    public static final String APPLICATION_BPM_ID = "ApplicationBpmId";
    public static final String NAME_OF_CUSTOMER = "CustomerName";
    public static final String LDP_CONFIRM_STATUS = "LdpConfirmStatus";
    public static final String LDP_REASON = "LdpReason";
  }
  public static final String SEPARATOR = "/";

  public static final String BUSINESS_NAME = "Phê duyệt RB";
  public static final String CIC_17 = "CIC_17";
  public static final String CIC_18 = "CIC_18";
  public static final String CIC_19 = "CIC_19";
  public static final String CIC_21 = "CIC_21";
  public static final String CIC_24 = "CIC_24";
  public static final String CIC_40 = "CIC_40";
  public static final String CIC_TIME = "CIC_TIME";
  public static final String RB_BPM_CHECKLIST = "0409";

  public static final String CBT_RUN_PROCESS = "CBT_RUN_PROCESS";
  public static final String CBT_RUN_PROCESS_STATUS = "ON";
  public static final String TIME_LOCAL_FORMAT = "yyyy-MM-dd HH:mm:ss.S";
  public static final String TIME_CIC_FORMAT = "dd/MM/yyyy";

  @NoArgsConstructor(access= AccessLevel.PRIVATE)
  public static final class CBT_DEFAULT_ADDRESS {
    public static final String ADDRESS_TYPE = "V004";
    public static final String ADDRESS_TYPE_VALUE = "Địa chỉ tài sản cho thuê";
  }
  public static final String REAL_ESTATE = "V001";
}
