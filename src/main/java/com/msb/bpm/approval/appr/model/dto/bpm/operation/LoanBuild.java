package com.msb.bpm.approval.appr.model.dto.bpm.operation;

import com.msb.bpm.approval.appr.model.entity.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@AllArgsConstructor
@NoArgsConstructor
@With
public class LoanBuild {
  private ApplicationEntity app;
  private CustomerEntity customer;
  private ApplicationCreditEntity credit;
  private ApplicationCreditLoanEntity creditLoan;
  private ApplicationCreditOverdraftEntity creditOverdraft;
  private Boolean requestDrafting = true;
  private Integer index;

}