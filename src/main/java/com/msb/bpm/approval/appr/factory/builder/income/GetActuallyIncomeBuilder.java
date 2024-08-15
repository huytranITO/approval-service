package com.msb.bpm.approval.appr.factory.builder.income;

import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.model.dto.ActuallyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import java.util.Collections;
import java.util.Set;

public class GetActuallyIncomeBuilder implements
    ObjectBuilder<ApplicationIncomeDTO, ApplicationIncomeEntity> {

  @Override
  public ActuallyIncomeDTO build(ApplicationIncomeEntity applicationIncome) {
    ActuallyIncomeDTO actuallyIncomeDTO = ApplicationIncomeMapper.INSTANCE.toActuallyIncomeDTO(
        applicationIncome);

    applicationIncome.getSalaryIncomes()
        .stream()
        .findFirst()
        .ifPresent(salaryIncome -> actuallyIncomeDTO.setIncomeItems(Collections.singleton(
            ApplicationIncomeMapper.INSTANCE.toSalaryIncomeDTO(salaryIncome))));

    applicationIncome.getRentalIncomes()
        .stream()
        .findFirst()
        .ifPresent(rentalIncome -> actuallyIncomeDTO.setIncomeItems(Collections.singleton(
            ApplicationIncomeMapper.INSTANCE.toRentalIncomeDTO(rentalIncome))));

    applicationIncome.getIndividualEnterpriseIncomes()
        .stream()
        .findFirst()
        .ifPresent(individualEnterpriseIncome -> actuallyIncomeDTO.setIncomeItems(
            Collections.singleton(
                ApplicationIncomeMapper.INSTANCE.toBizIncomeDTO(individualEnterpriseIncome))));

    applicationIncome.getOtherIncomes()
        .stream()
        .findFirst()
        .ifPresent(otherIncome -> actuallyIncomeDTO.setIncomeItems(Collections.singleton(
            ApplicationIncomeMapper.INSTANCE.toOtherIncomeDTO(otherIncome))));

    applicationIncome.getPropertyBusinessIncomes()
            .stream()
            .findFirst()
            .ifPresent(propertyBusinessIncome -> actuallyIncomeDTO.setIncomeItems(Collections.singleton(
                    ApplicationIncomeMapper.INSTANCE.toPropertyBusinessIncomeDTO(propertyBusinessIncome))));


    actuallyIncomeDTO.getIncomeItems()
        .forEach(item -> item.setRecognizedIncome(applicationIncome.getRecognizedIncome()));

    return actuallyIncomeDTO;
  }

}
