package com.msb.bpm.approval.appr.enums.loanproposal;

import lombok.Getter;

@Getter
public enum LoanProposalEndpoint {
  COMPARATOR_APPLICATION("comparator-application"),
  REPLACE_DATA_APPLICATION("replace-data-application")
  ;

  private final String value;

  LoanProposalEndpoint(String value) {
    this.value = value;
  }
}
