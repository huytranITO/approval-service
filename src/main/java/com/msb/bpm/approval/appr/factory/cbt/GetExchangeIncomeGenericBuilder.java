package com.msb.bpm.approval.appr.factory.cbt;

import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.ExchangeIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RentalIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;
import java.util.HashSet;
import java.util.Set;

public class GetExchangeIncomeGenericBuilder  implements
    ObjectGenericBuilder<ApplicationIncomeDTO, ApplicationIncomeEntity, Set<Long>> {

  @Override
  public ExchangeIncomeDTO build(ApplicationIncomeEntity applicationIncome, Set<Long> ids) {
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

    if (incomes.size() > 0) {
      ExchangeIncomeDTO exchangeIncome = ApplicationIncomeMapper.INSTANCE.toExchangeIncomeDTO(applicationIncome);
      exchangeIncome.setIncomeItems(incomes);
      return exchangeIncome;
    }
    return null;
  }
}
