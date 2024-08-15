package com.msb.bpm.approval.appr.mapper.formtemplate;

import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.ACTUALLY;
import static com.msb.bpm.approval.appr.constant.ApplicationConstant.ApplicationIncomeConstant.EXCHANGE;
import static com.msb.bpm.approval.appr.enums.application.ConversionMethod.TOTAL_ASSET_METHOD;

import com.msb.bpm.approval.appr.constant.IntegrationConstant;
import com.msb.bpm.approval.appr.model.dto.RentalIncomeDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateCustomerTransactionDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateIncomeEvaluation;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateIncomeItem;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateIncomesDTO;
import com.msb.bpm.approval.appr.model.dto.formtemplate.FormTemplateTotalAssetIncome;
import com.msb.bpm.approval.appr.model.entity.ApplicationIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.CustomerTransactionIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.IncomeEvaluationEntity;
import com.msb.bpm.approval.appr.model.entity.IndividualEnterpriseIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.OtherIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.PropertyBusinessIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.RentalIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.SalaryIncomeEntity;
import com.msb.bpm.approval.appr.model.entity.TotalAssetIncomeEntity;
import com.msb.bpm.approval.appr.util.DateUtils;
import com.msb.bpm.approval.appr.util.MathUtil;
import com.msb.bpm.approval.appr.util.Util;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = FormTemplateApplicationDtoMapper.class)
public interface FormTemplateIncomeMapper {

  Set<FormTemplateIncomesDTO> transformIncome(List<ApplicationIncomeEntity> income);

  @Mapping(
      target = "conversionMethodValue",
      source = "conversionMethodValue",
      defaultValue = "null")
  @Mapping(target = "explanation", source = "explanation", defaultValue = "null")
  @Mapping(
      target = "recognizedIncome",
      source = "recognizedIncome",
      qualifiedByName = "toNumberFormat")
  FormTemplateIncomesDTO applicationIncomeEntityToFormTemplateIncomesDTO(
      ApplicationIncomeEntity applicationIncomeEntity);

  @AfterMapping
  default void transformIncomeDetail(
      ApplicationIncomeEntity incomeEntity, @MappingTarget FormTemplateIncomesDTO dto) {
    if (!Util.isNullOrEmpty(incomeEntity.getOtherIncomes())) {
      dto.getIncomeItems().addAll(fromOtherIncomeEntity(incomeEntity.getOtherIncomes()));
    }
    if (!Util.isNullOrEmpty(incomeEntity.getRentalIncomes())) {
      dto.getIncomeItems().addAll(fromRentalIncomeEntity(incomeEntity.getRentalIncomes()));
    }
    if (!Util.isNullOrEmpty(incomeEntity.getIndividualEnterpriseIncomes())) {
      dto.getIncomeItems()
          .addAll(fromEnterpriseIncomeEntity(incomeEntity.getIndividualEnterpriseIncomes()));
    }
    if (!Util.isNullOrEmpty(incomeEntity.getSalaryIncomes())) {
      dto.getIncomeItems().addAll(fromSalaryIncomeEntity(incomeEntity.getSalaryIncomes()));
    }
    if (!Util.isNullOrEmpty(incomeEntity.getPropertyBusinessIncomes())) {
      dto.getIncomeItems().addAll(fromPropertyBusinessIncomeEntity(incomeEntity.getPropertyBusinessIncomes()));
    }
    if (EXCHANGE.equalsIgnoreCase(dto.getIncomeRecognitionMethod())) {
      dto.setExchangeRecognizedIncome(MathUtil.toNumberFormat(dto.getRecognizedIncome()));
      if (TOTAL_ASSET_METHOD.getValue().equalsIgnoreCase(dto.getConversionMethod())) {
        dto.setCheckTotalAssetMethod(true);
      }
    } else if (ACTUALLY.equalsIgnoreCase(dto.getRecognizedIncome())) {
      dto.setIncomeRecognitionMethod(dto.getRecognizedIncome());
    }
  }
  @Mapping(target = "totalOutstandingDebt", source = "totalOutstandingDebt", qualifiedByName = "toNumberFormat")
  @Mapping(target = "estimatedIncome", source = "estimatedIncome", qualifiedByName = "toNumberFormat")
  @Mapping(target = "totalAccumulatedAssetValue", source = "totalAccumulatedAssetValue", qualifiedByName = "toNumberFormat")
  FormTemplateIncomeEvaluation incomeEvaluationEntityToDTO(IncomeEvaluationEntity incomeEvaluation);
  @Mapping(target = "assetValue", source = "assetValue", qualifiedByName = "toNumberFormat")
  FormTemplateTotalAssetIncome totalAssetIncomeEntityToDTO(TotalAssetIncomeEntity totalAssetIncome);
  List<FormTemplateIncomeItem> fromSalaryIncomeEntity(Set<SalaryIncomeEntity> salaryIncomes);

  List<FormTemplateIncomeItem> fromEnterpriseIncomeEntity(
      Set<IndividualEnterpriseIncomeEntity> enterpriseIncomes);

  List<FormTemplateIncomeItem> fromRentalIncomeEntity(Set<RentalIncomeEntity> rentalIncomes);

  List<FormTemplateIncomeItem> fromOtherIncomeEntity(Set<OtherIncomeEntity> otherIncomes);

  List<FormTemplateIncomeItem> fromPropertyBusinessIncomeEntity(Set<PropertyBusinessIncomeEntity> propertyBusinessIncomes);
  List<FormTemplateCustomerTransactionDTO> fromCustomerTransactionIncomeEntity(Set<CustomerTransactionIncomeEntity> customerTransactionIncomes);

  @Mapping(target = "address.salaryCityCode", source = "cityCode")
  @Mapping(target = "address.salaryCityValue", source = "cityValue")
  @Mapping(target = "address.salaryDistrictCode", source = "districtCode")
  @Mapping(target = "address.salaryDistrictValue", source = "districtValue")
  @Mapping(target = "address.salaryWardCode", source = "wardCode")
  @Mapping(target = "address.salaryWardValue", source = "wardValue")
  @Mapping(target = "address.salaryAddressLine", source = "addressLine")
  @Mapping(target = "loanExplanation", source = "explanation")
  @Mapping(target = "salaryCompanyName", source = "companyName")
  @Mapping(target = "businessRegistrationNumber", constant = "null")
  @Mapping(target = "salaryRegistrationNumber", source = "businessRegistrationNumber")
  @Mapping(target = "startWorkingDay", source = "startWorkingDay", qualifiedByName = "formatDate")
  FormTemplateIncomeItem fromSalaryIncomeEntity(SalaryIncomeEntity salaryIncome);

  @Mapping(target = "address.businessCityCode", source = "cityCode")
  @Mapping(target = "address.businessCityValue", source = "cityValue")
  @Mapping(target = "address.businessDistrictCode", source = "districtCode")
  @Mapping(target = "address.businessDistrictValue", source = "districtValue")
  @Mapping(target = "address.businessWardCode", source = "wardCode")
  @Mapping(target = "address.businessWardValue", source = "wardValue")
  @Mapping(target = "address.businessAddressLine", source = "addressLine")
  @Mapping(target = "businessCompanyName", source = "companyName")
  @Mapping(target = "incomeMonthly", source = "incomeMonthly", qualifiedByName = "toNumberFormat")
  @Mapping(target = "expenseMonthly", source = "expenseMonthly", qualifiedByName = "toNumberFormat")
  @Mapping(target = "profitMonthly", source = "profitMonthly", qualifiedByName = "toNumberFormat")
  @Mapping(target = "profitMargin", source = "profitMargin", qualifiedByName = "toNumberFormat")
  @Mapping(target = "jurisdictionTitle", constant = "Thông tin Pháp lý")
  @Mapping(target = "activityInfor", constant = "Thông tin Hoạt động")
  @Mapping(target = "businessResultTitle", constant = "Kết quả kinh doanh")
  @Mapping(target = "businessEvaluateResult", source = "evaluateResult")
  @Mapping(target = "businessExperience", source = "businessExperience")
  @Mapping(target = "businessScale", source = "businessScale")
  FormTemplateIncomeItem fromEnterpriseIncomeEntity(
      IndividualEnterpriseIncomeEntity enterpriseIncome);

  @Mapping(target = "assetExplanation", source = "explanation")
  @Mapping(target = "rentalPrice", source = "rentalPrice", qualifiedByName = "toNumberFormat")
  @Mapping(target = "assetAddress", expression = "java(populateRentalIncomeAddress(rentalIncome))")
  FormTemplateIncomeItem fromRentalIncomeEntity(RentalIncomeEntity rentalIncome);

  @Mapping(target = "otherExplanation", source = "explanation")
  FormTemplateIncomeItem fromOtherIncomeEntity(OtherIncomeEntity otherIncome);

  @Mapping(target = "propertyBusinessExperience", source = "businessExperience")
  @Mapping(target = "propertyAccumulateAsset", source = "accumulateAsset")
  @Mapping(target = "propertyBusinessScale", source = "businessScale")
  @Mapping(target = "propertyIncomeBase", source = "incomeBase")
  @Mapping(target = "propertyBasisIncome", source = "basisIncome")
  @Mapping(target = "propertyIncomeAssessment", source = "incomeAssessment")
  @Mapping(target = "propertyBusinessPlan", source = "businessPlan")
  @Mapping(target = "customerTransactionIncomes", source = "customerTransactionIncomes")
  @Mapping(target = "businessExperience",  ignore = true)
  @Mapping(target = "businessScale", ignore = true)
  FormTemplateIncomeItem fromPropertyBusinessIncomeEntity(PropertyBusinessIncomeEntity propertyBusinessIncome);

  @Mapping(target = "propertyTransactionTime", source = "transactionTime", qualifiedByName = "formatDate")
  @Mapping(target = "propertyAsset", source = "asset")
  @Mapping(target = "propertyTransactionValue", source = "transactionValue", qualifiedByName = "toNumberFormat")
  @Mapping(target = "propertyPurchaseCost", source = "purchaseCost", qualifiedByName = "toNumberFormat")
  @Mapping(target = "propertyBrokerageCost", source = "brokerageCost", qualifiedByName = "toNumberFormat")
  @Mapping(target = "propertyTransferNameCost", source = "transferNameCost", qualifiedByName = "toNumberFormat")
  @Mapping(target = "propertyProfit", source = "profit", qualifiedByName = "toNumberFormat")
  FormTemplateCustomerTransactionDTO fromCustomerTransactionIncomeEntity(CustomerTransactionIncomeEntity customerTransaction);

  @Named("formatDate")
  default String formatDate(LocalDate date) {
    return DateUtils.format(date, "dd/MM/yyyy");
  }

  default String populateRentalIncomeAddress(RentalIncomeEntity rentalIncome) {
    if (IntegrationConstant.V001.equalsIgnoreCase(rentalIncome.getAssetType())) {
      return StringUtils.join(
              StringUtils.defaultIfBlank(rentalIncome.getAssetAddress(), ""),
              ", ",
              StringUtils.defaultIfBlank(rentalIncome.getWardValue(), ""),
              ", ",
              StringUtils.defaultIfBlank(rentalIncome.getDistrictValue(), ""),
              ", ",
              StringUtils.defaultIfBlank(rentalIncome.getCityValue(), ""));
    }
    return rentalIncome.getAssetAddress();
  }
}
