package com.msb.bpm.approval.appr.factory.builder.income;

import com.msb.bpm.approval.appr.enums.application.ConversionMethod;
import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.mapper.IncomeEvaluationMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.ExchangeIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;
import org.apache.commons.lang3.ObjectUtils;

public class GetExchangeIncomeReverseBuilder implements
    ObjectBuilder<ApplicationIncomeEntity, ApplicationIncomeDTO> {

  @Override
  public ApplicationIncomeEntity build(ApplicationIncomeDTO applicationIncomeDTO) {
    ExchangeIncomeDTO exchangeIncome = (ExchangeIncomeDTO) applicationIncomeDTO;
    Set<BaseIncomeDTO> incomeItems = exchangeIncome.getIncomeItems();

    ApplicationIncomeEntity applicationIncomeEntity = ApplicationIncomeMapper.INSTANCE.toExchangeIncomeEntity(
        exchangeIncome);
    // Set income evaluation khi lựa chọn PP ghi nhận thu nhập = Quy đổi và PP quy đổi = Tổng tài sản
     if (ConversionMethod.TOTAL_ASSET_METHOD.getValue().equalsIgnoreCase(exchangeIncome.getConversionMethod()) &&
         ObjectUtils.isNotEmpty(applicationIncomeEntity.getIncomeEvaluation()) &&
         CollectionUtils.isNotEmpty(applicationIncomeEntity.getIncomeEvaluation().getTotalAssetIncomes())) {
       applicationIncomeEntity.setIncomeEvaluation(IncomeEvaluationMapper.INSTANCE.toIncomeEvaluationEntity(exchangeIncome.getIncomeEvaluation()));
       applicationIncomeEntity.getIncomeEvaluation().getTotalAssetIncomes()
           .forEach(entity -> entity.setIncomeEvaluation(applicationIncomeEntity.getIncomeEvaluation()));
     }
    if (CollectionUtils.isEmpty(incomeItems)) {
      return applicationIncomeEntity;
    }

    incomeItems.forEach(incomeItem -> referenceBaseIncomeToEntity(applicationIncomeEntity, incomeItem));

    return applicationIncomeEntity;
  }
}
