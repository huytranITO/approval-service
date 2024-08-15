package com.msb.bpm.approval.appr.factory;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.CARD;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.LOAN;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationCredit.OVERDRAFT;
import static com.msb.bpm.approval.appr.exception.DomainCode.TYPE_UNSUPPORTED;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCmsCreditCardReverseBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCmsCreditLoanReverseBuilder;
import com.msb.bpm.approval.appr.factory.builder.credit.GetCmsCreditOverdraftReverseBuilder;
import com.msb.bpm.approval.appr.model.dto.cms.v2.CmsBaseCreditDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationCreditEntity;

/**
 * @author : Manh Nguyen Van (CN-SHQLQT)
 * @mailto : manhnv8@msb.com.vn
 * @created : 17/8/2023, Thursday
 **/
public interface CmsApplicationCreditFactory {

  static ObjectBuilder<ApplicationCreditEntity, CmsBaseCreditDTO> getApplicationCreditReverse(String creditType) {
    switch (creditType) {
      case LOAN:
        return new GetCmsCreditLoanReverseBuilder();
      case OVERDRAFT:
        return new GetCmsCreditOverdraftReverseBuilder();
      case CARD:
        return new GetCmsCreditCardReverseBuilder();
      default:
        throw new ApprovalException(TYPE_UNSUPPORTED);
    }
  }
}
