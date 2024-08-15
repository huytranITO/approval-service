package com.msb.bpm.approval.appr.enums.asset;

import lombok.Getter;

@Getter
public enum CollateralEndpoint {
  GET_ASSET_RULE_CHECKLIST("get-asset-rule-checklist"),
  GET_ASSET_DATA("get-asset-data"),
  APPLICATION_DRAFT_REFRESH_STATUS("applicationDraft-refreshStatus"),
  VALID_OPR("valid-opr"),
  CHECK_APPLICATION_DRAFT("check-application-draft"),
  CREATE_CMS_ASSET("create-asset-data"),
  CREATE_BPM_ASSET("bpm-create-asset-data"),
  GET_REQUEST_DATA_OPERATION("get-request-data-operation"),
  FORM_TEMPLATE("form-templates"),
  UPSERT_ASSET("upsert-assets"),
  COMMON_GET_ASSET("get-asset-checklist"),
  COMMON_GET_ASSET_BY_APPLICATION("get-asset-info-by-application"),
  GET_OPR_ASSET("get-opr-asset"),
  GET_ASSET_INFO("get-asset-info"),
  ;

  private final String value;

  CollateralEndpoint(String value) {
    this.value = value;
  }
}
