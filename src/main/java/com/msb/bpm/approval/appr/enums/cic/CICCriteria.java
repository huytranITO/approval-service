package com.msb.bpm.approval.appr.enums.cic;

import lombok.Getter;

@Getter
public enum CICCriteria {

  DEBT_GROUP_CURRENT("debt_group_current"),
  DEBT_GROUP_LAST_12("debt_group_last_12"),
  DEBT_GROUP_LAST_24("debt_group_last_24"),
  TOTAL_LOAN_COLLATERAL("total_loan_collateral"),
  TOTAL_LOAN_COLLATERAL_USD("total_loan_collateral_usd"),
  TOTAL_UNSECURE_COLLATERAL("total_unsecure_loan"),
  TOTAL_UNSECURE_COLLATERAL_USD("total_unsecure_loan_usd"),
  TOTAL_CREDIT_CARD_LIMIT("total_credit_card_limit"),
  TOTAL_DEBT_CARD_LIMIT("total_debt_card_limit"),
  ;

  private final String value;

  CICCriteria(String value) {
    this.value = value;
  }

}
