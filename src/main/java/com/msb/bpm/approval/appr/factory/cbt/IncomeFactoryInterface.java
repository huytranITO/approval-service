package com.msb.bpm.approval.appr.factory.cbt;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.EXCHANGE;
import static com.msb.bpm.approval.appr.exception.DomainCode.TYPE_UNSUPPORTED;

import com.msb.bpm.approval.appr.exception.ApprovalException;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import java.util.Set;

public interface IncomeFactoryInterface {

  static ObjectGenericBuilder<ApplicationIncomeDTO, ApplicationIncomeEntity, Set<Long>> getApplicationIncome(
      String incomeRecognitionMethod) {
    switch (incomeRecognitionMethod) {
      case EXCHANGE:
        return new GetExchangeIncomeGenericBuilder();
      case ACTUALLY:
        return new GetActuallyIncomeGenericBuilder();
      default:
        throw new ApprovalException(TYPE_UNSUPPORTED);
    }
  }

}
