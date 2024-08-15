package com.msb.bpm.approval.appr.model.dto.formtemplate;

import com.msb.bpm.approval.appr.model.dto.AddressDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// @Builder
public class FormTemplateIncomeItem implements Serializable {
  private static final long serialVersionUID = -1004740360713819530L;

  private String id = "null";
  private String salaryRecognizedIncome = "null";
  private String rentalRecognizedIncome = "null";
  private String businessRecognizedIncome = "null";
  private String companyRecognizedIncome = "null";
  private String incomeType = "null";
  private String incomeTypeValue = "null";
  private String customerId = "null";
  private String incomeOwner = "null";
  private String incomeOwnerValue = "null";
  private String incomeOwnerName = "null";
  private String salaryRegistrationNumber = "null";
  private String assetType = "null";
  private String assetTypeValue = "null";
  private String assetOwner = "null";
  private String assetAddress = "null";
  private String renter = "null";
  private String renterPhone = "null";
  private String rentalPurpose = "null";
  private String rentalPurposeValue = "null";
  private String rentalPrice = "null";
  private String loanExplanation = "null";
  private String assetExplanation = "null";
  private String otherExplanation = "null";
  private String taxCode = "null";
  private String socialInsuranceCode = "null";
  private String rankType = "null";
  private String rankTypeValue = "null";
  private String kpiRating = "null";
  private String kpiRatingValue = "null";
  private String payType = "null";
  private String payTypeValue = "null";
  private String laborType = "null";
  private String laborTypeValue = "null";
  private String businessRegistrationNumber = "null";
  private String groupOfWorking = "null";
  private String salaryCompanyName = "null";
  private String businessCompanyName = "null";
  private String position = "null";
  private String startWorkingDay = "null";
  private String mainBusinessSector = "null";
  private String productionProcess = "null";
  private String recordMethod = "null";
  private String input = "null";
  private String output = "null";
  private String businessScale = "null";
  private String inventory = "null";
  private String evaluationPeriod = "null";
  private String evaluationPeriodValue = "null";
  private String incomeMonthly = "null";
  private String expenseMonthly = "null";
  private String profitMonthly = "null";
  private String profitMargin = "null";
  private String evaluateResult = "null";
  private String businessEvaluateResult = "null";
  private String capitalContributionRate = "null";
  private String businessPlaceOwnership = "null";
  private String businessPlaceOwnershipValue = "null";
  private String businessExperience = "null";
  private boolean incomeFuture = false;
  private String incomeInfo = "null";
  private String incomeDetail = "null";
  private String incomeDetailValue = "null";
  private String jurisdictionTitle = "null"; //title "Hồ sơ pháp lý"
  private String activityInfor = "null"; //title "Thông tin hoạt động"
  private String businessResultTitle = "null"; //title "Hồ sơ pháp lý"

  private FormTemplateAddressDTO address;
  // address = new FormTemplateAddressDTO

  //bds gom xay
  private String propertyBusinessExperience = "null";
  private String propertyBusinessScale = "null";
  private String propertyAccumulateAsset = "null";
  private String propertyIncomeBase = "null";
  private String propertyBasisIncome = "null";
  private String propertyIncomeAssessment = "null";
  private String propertyBusinessPlan = "null";
  private List<FormTemplateCustomerTransactionDTO> customerTransactionIncomes = new ArrayList<>();
}
