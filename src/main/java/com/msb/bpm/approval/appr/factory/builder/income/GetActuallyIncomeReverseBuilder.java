package com.msb.bpm.approval.appr.factory.builder.income;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.model.dto.ActuallyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;

public class GetActuallyIncomeReverseBuilder implements
    ObjectBuilder<ApplicationIncomeEntity, ApplicationIncomeDTO> {

  @Override
  public ApplicationIncomeEntity build(ApplicationIncomeDTO applicationIncomeDTO) {
    ActuallyIncomeDTO actuallyIncomeDTO = (ActuallyIncomeDTO) applicationIncomeDTO;
    BaseIncomeDTO baseIncome = actuallyIncomeDTO.getIncomeItems()
        .stream()
        .findFirst()
        .orElse(null);

    ApplicationIncomeEntity applicationIncomeEntity = ApplicationIncomeMapper.INSTANCE.toActuallyIncomeEntity(
        actuallyIncomeDTO);

    if (baseIncome == null) {
      return applicationIncomeEntity;
    }

    applicationIncomeEntity.setRecognizedIncome(baseIncome.getRecognizedIncome());
    referenceBaseIncomeToEntity(applicationIncomeEntity, baseIncome);

    return applicationIncomeEntity;
  }
}
