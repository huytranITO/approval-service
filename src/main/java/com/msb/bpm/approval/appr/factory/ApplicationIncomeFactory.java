package com.msb.bpm.approval.appr.factory;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.EXCHANGE;
import static com.msb.bpm.approval.appr.exception.DomainCode.TYPE_UNSUPPORTED;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.factory.builder.income.GetActuallyIncomeBuilder;
import com.msb.bpm.approval.appr.factory.builder.income.GetExchangeIncomeBuilder;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;

public interface ApplicationIncomeFactory {

  static ObjectBuilder<ApplicationIncomeDTO, ApplicationIncomeEntity> getApplicationIncome(
      String incomeRecognitionMethod) {
    switch (incomeRecognitionMethod) {
      case EXCHANGE:
        return new GetExchangeIncomeBuilder();
      case ACTUALLY:
        return new GetActuallyIncomeBuilder();
      default:
        throw new ApprovalException(TYPE_UNSUPPORTED);
    }
  }
}
