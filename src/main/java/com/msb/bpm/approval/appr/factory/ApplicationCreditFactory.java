package com.msb.bpm.approval.appr.factory;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.CARD;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LOAN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.OVERDRAFT;
import static com.msb.bpm.approval.appr.exception.DomainCode.TYPE_UNSUPPORTED;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCreditCardBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCreditLoanBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCreditOverdraftBuilder;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;

public interface ApplicationCreditFactory {

  static ObjectBuilder<ApplicationCreditDTO, ApplicationCreditEntity> getApplicationCredit(String creditType) {
    switch (creditType) {
      case LOAN:
        return new GetCreditLoanBuilder();
      case OVERDRAFT:
        return new GetCreditOverdraftBuilder();
      case CARD:
        return new GetCreditCardBuilder();
      default:
        throw new ApprovalException(TYPE_UNSUPPORTED);
    }
  }
}
