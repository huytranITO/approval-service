package com.msb.bpm.approval.appr.factory.builder.income;

import com.msb.bpm.approval.appr.enums.application.ConversionMethod;
import com.msb.bpm.approval.appr.factory.builder.ObjectBuilder;
import com.msb.bpm.approval.appr.mapper.ApplicationIncomeMapper;
import com.msb.bpm.approval.appr.mapper.IncomeEvaluationMapper;
import com.msb.bpm.approval.appr.model.dto.ApplicationIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.BaseIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.ExchangeIncomeDTO;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.OtherIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RentalIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;

import java.util.HashSet;
import java.util.Set;

public class GetExchangeIncomeBuilder implements
    ObjectBuilder<ApplicationIncomeDTO, ApplicationIncomeEntity> {

  @Override
  public ExchangeIncomeDTO build(ApplicationIncomeEntity applicationIncome) {
    Set<BaseIncomeDTO> incomes = new HashSet<>();

    Set<SalaryIncomeEntity> salaryIncomes = applicationIncome.getSalaryIncomes();
    salaryIncomes.forEach(salaryIncome -> incomes.add(
        ApplicationIncomeMapper.INSTANCE.toSalaryIncomeDTO(salaryIncome)));

    Set<RentalIncomeEntity> rentalIncomes = applicationIncome.getRentalIncomes();
    rentalIncomes.forEach(rentalIncome -> incomes.add(
        ApplicationIncomeMapper.INSTANCE.toRentalIncomeDTO(rentalIncome)));

    Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes = applicationIncome.getIndividualEnterpriseIncomes();
    individualEnterpriseIncomes.forEach(individualEnterpriseIncome -> incomes.add(
        ApplicationIncomeMapper.INSTANCE.toBizIncomeDTO(individualEnterpriseIncome)));

    Set<OtherIncomeEntity> otherIncomes = applicationIncome.getOtherIncomes();
    otherIncomes.forEach(
        otherIncome -> incomes.add(ApplicationIncomeMapper.INSTANCE.toOtherIncomeDTO(otherIncome)));

    ExchangeIncomeDTO exchangeIncome = ApplicationIncomeMapper.INSTANCE.toExchangeIncomeDTO(
        applicationIncome);
    // Set income evaluation khi lựa chọn PP ghi nhận thu nhập = Quy đổi và PP quy đổi = Tổng tài sản
    if (ConversionMethod.TOTAL_ASSET_METHOD.getValue().equalsIgnoreCase(exchangeIncome.getConversionMethod())) {
      exchangeIncome.setIncomeEvaluation(IncomeEvaluationMapper.INSTANCE.toIncomeEvaluationDTO(applicationIncome.getIncomeEvaluation()));
    }
    exchangeIncome.setIncomeItems(incomes);
    return exchangeIncome;
  }



}
