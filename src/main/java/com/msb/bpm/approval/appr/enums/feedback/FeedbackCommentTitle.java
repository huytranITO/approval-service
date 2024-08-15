package com.msb.bpm.approval.appr.enums.feedback;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedbackCommentTitle {
  CUSTOMER_AND_CUSTOMER_RELATIONSHIP_INFO("Thông tin KH và NLQ:\n"),
  INCOME_INFO("Thông tin nguồn thu:\n"),
  APPLICATION_CREDIT("Thông tin khoản vay:\n"),
  ASSET_INFO("Thông tin tài sản:\n"),
  DOCUMENT_COMMENT_DATA("Danh mục hồ sơ:\n"),
  ;
  private final String value;
}
