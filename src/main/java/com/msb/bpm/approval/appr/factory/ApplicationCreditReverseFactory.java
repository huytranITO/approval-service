package com.msb.bpm.approval.appr.factory;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.CARD;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LOAN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.OVERDRAFT;
import static com.msb.bpm.approval.appr.exception.DomainCode.TYPE_UNSUPPORTED;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCreditCardReverseBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCreditLoanReverseBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCreditOverdraftReverseBuilder;
import com.msb.bpm.approval.appr.model.dto.ApplicationCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;

public interface ApplicationCreditReverseFactory {

  static ObjectBuilder<ApplicationCreditEntity, ApplicationCreditDTO> getApplicationCredit(String creditType) {
    switch (creditType) {
      case LOAN:
        return new GetCreditLoanReverseBuilder();
      case OVERDRAFT:
        return new GetCreditOverdraftReverseBuilder();
      case CARD:
        return new GetCreditCardReverseBuilder();
      default:
        throw new ApprovalException(TYPE_UNSUPPORTED);
    }
  }
}
