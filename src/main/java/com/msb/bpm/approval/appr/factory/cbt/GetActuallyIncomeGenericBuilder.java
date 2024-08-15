package com.msb.bpm.approval.appr.factory.cbt;

import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.model.dto.ActuallyIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RentalIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;
import java.util.HashSet;
import java.util.Set;

public class GetActuallyIncomeGenericBuilder  implements
    ObjectGenericBuilder<ApplicationIncomeDTO, ApplicationIncomeEntity, Set<Long>> {

  @Override
  public ActuallyIncomeDTO build(ApplicationIncomeEntity applicationIncome, Set<Long> ids) {
    ActuallyIncomeDTO actuallyIncomeDTO = ApplicationIncomeMapper.INSTANCE.toActuallyIncomeDTO(
        applicationIncome);

    Set<BaseIncomeDTO> incomes = new HashSet<>();
    Set<SalaryIncomeEntity> salaryIncomes = applicationIncome.getSalaryIncomes();
    salaryIncomes.forEach(salaryIncome -> {
      if (ids.contains(salaryIncome.getCustomerId())) {
        incomes.add(
            ApplicationIncomeMapper.INSTANCE.toSalaryIncomeDTO(salaryIncome));
      }
    });


    Set<RentalIncomeEntity> rentalIncomes = applicationIncome.getRentalIncomes();
    rentalIncomes.forEach(rentalIncome -> {
      if (ids.contains(rentalIncome.getCustomerId())) {
        incomes.add(
            ApplicationIncomeMapper.INSTANCE.toRentalIncomeDTO(rentalIncome));
      }
    });

    Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes = applicationIncome.getIndividualEnterpriseIncomes();
    individualEnterpriseIncomes.forEach(individualEnterpriseIncome -> {
      if (ids.contains(individualEnterpriseIncome.getCustomerId())) {
        incomes.add(
            ApplicationIncomeMapper.INSTANCE.toBizIncomeDTO(individualEnterpriseIncome));
      }
    });

    if (incomes.size() >0) {
      actuallyIncomeDTO.setIncomeItems(incomes);
      actuallyIncomeDTO.getIncomeItems().forEach(item -> item.setRecognizedIncome(applicationIncome.getRecognizedIncome()));
      return actuallyIncomeDTO;
    }
    return null;
  }

}
