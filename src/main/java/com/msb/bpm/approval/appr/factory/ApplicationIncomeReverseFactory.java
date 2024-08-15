package com.msb.bpm.approval.appr.factory;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.EXCHANGE;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.exception.DomainCode;
import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.factory.builder.income.GetActuallyIncomeReverseBuilder;
import com.msb.bpm.approval.appr.factory.builder.income.GetExchangeIncomeReverseBuilder;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;

public interface ApplicationIncomeReverseFactory {

  static ObjectBuilder<ApplicationIncomeEntity, ApplicationIncomeDTO> getApplicationIncomeReverse(
      String incomeRecognitionMethod) {
    switch (incomeRecognitionMethod) {
      case ACTUALLY:
        return new GetActuallyIncomeReverseBuilder();
      case EXCHANGE:
        return new GetExchangeIncomeReverseBuilder();
      default:
        throw new ApprovalException(DomainCode.TYPE_UNSUPPORTED);
    }
  }
}
