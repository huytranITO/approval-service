package com.msb.bpm.approval.appr.mapper;

import com.msb.bpm.approval.appr.enums.application.CreditType;
import com.msb.bpm.approval.appr.enums.application.GuaranteeForm;
import com.msb.bpm.approval.appr.enums.bpm.*;
import com.msb.bpm.approval.appr.model.dto.bpm.operation.ApplicantBuild;
import com.msb.bpm.approval.appr.model.dto.bpm.operation.LoanBuild;
import com.msb.bpm.approval.appr.model.entity.*;
import com.msb.bpm.approval.appr.model.request.bpm.operation.ApplicantInfo;
import com.msb.bpm.approval.appr.model.request.bpm.operation.IncomeInfo;
import com.msb.bpm.approval.appr.model.request.bpm.operation.LoanInfo;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Mapper
public interface BpmOperationMapper {
    BpmOperationMapper INSTANCE = Mappers.getMapper(BpmOperationMapper.class);

    String V001 = "V001";
    String V002 = "V002";
    String V003 = "V003";
    String V004 = "V004";
    String PREFIX_LOAN_NAME = "Khoản vay";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Mapping(target = "approvalApplicantId", source = "cust.refCustomerId")
    @Mapping(target = "address", source = "address.addressLine")
    @Mapping(target = "cif", source = "cust.cif")
    @Mapping(target = "city", source = "address.cityValue")
    @Mapping(target = "district", source = "address.districtValue")
    @Mapping(target = "ward", source = "address.wardValue")
    @Mapping(target = "dob", source = "individual.dateOfBirth", qualifiedByName = "buildFormatDate")
    @Mapping(target = "email", source = "individual.email")
    @Mapping(target = "employeeId", source = "individual.employeeCode")
    @Mapping(target = "fullName", source = "individual", qualifiedByName = "buildFullName")
    @Mapping(target = "gender", source = "individual", qualifiedByName = "buildGender")
    @Mapping(target = "icNumber", source = "identity.identifierCode")
    @Mapping(target = "icType", source = "identity.documentTypeValue")
    @Mapping(target = "issueAt", source = "identity.issuedAt", qualifiedByName = "buildFormatDate")
    @Mapping(target = "issuePlace", source = "identity.issuedPlaceValue")
    @Mapping(target = "issueBy", source = "identity.issuedByValue")
    @Mapping(target = "lastName", source = "individual.lastName")
    @Mapping(target = "maritalStatus", source = "individual.martialStatus")
    @Mapping(target = "nationality", source = "individual.nationValue")
    @Mapping(target = "owner", source = "owner")
    @Mapping(target = "phoneNumber", source = "individual.phoneNumber")
    @Mapping(target = "segment", source = "app", qualifiedByName = "buildSegment")
    @Mapping(target = "shortName", source = "individual.firstName")
    @Mapping(source = "applicantBuild", target = "incomes", qualifiedByName = "buildIncomes")
    @Mapping(target = "orderDisplay", source = "cust.orderDisplay")
    @Mapping(target = "version", source = "cust.version")
    ApplicantInfo mapApplicantInfo(ApplicantBuild applicantBuild);


    @Mapping(target = "amount", source = "credit.loanAmount")
    @Mapping(target = "cif", source = "customer.cif")
    @Mapping(target = "approvalLoanId", source = "credit.id")
    @Mapping(target = "kunnTime", source = "creditLoan.kunnPeriod")
    @Mapping(target = "limitAmount", source = "credit.loanAmount")
    @Mapping(target = "products", expression = "java(products(loanBuild.getCredit()))")
    @Mapping(target = "purpose", source = "creditLoan", qualifiedByName ="buildLoanPurpose")
    @Mapping(target = "requestDrafting", source = "requestDrafting")
    @Mapping(target = "tenorOfDebtContract", source = "creditLoan.loanPeriod")
    @Mapping(target = "type", source = "credit", qualifiedByName = "buildLoanType")
    @Mapping(target = "typeOfCreditOffer", source = "creditLoan.creditForm", qualifiedByName = "buildTypeOfCreditOffer")
    @Mapping(target = "typeOfDisbursements", source = "creditLoan.disburseMethod", qualifiedByName = "buildTypeOfDisbursements")
    @Mapping(target = "approvalMinutesActiveTime", source = "app.effectivePeriod")
    @Mapping(target = "principalPaymentFrequency", source = "creditLoan", qualifiedByName = "buildPrincipalLoanPaymentFrequency")
    @Mapping(target = "interestPaymentFrequency", source = "creditLoan", qualifiedByName = "buildInterestPaymentFrequency")
    @Mapping(target = "graceTime", source = "creditLoan.originalPeriod")
    @Mapping(target = "index", source = "index")
    @Mapping(target = "name", expression = "java(buildLoanName(loanBuild.getIndex()))")

    @Mapping(target = " disbursementsNo", source = "creditLoan.disburseFrequency", qualifiedByName = "buildDisbursementsNo")
    @Mapping(target = "purposeCode",source ="creditLoan.loanPurpose")
    @Mapping(target = "productCode",source ="creditLoan.productCode")
    @Mapping(target = "productName",source ="creditLoan.productName")
    @Mapping(target = "productInfo",source ="creditLoan.productInfoCode")
    @Mapping(target = "productInfoValue",source ="creditLoan.productInfoName")
    @Mapping(target = "paymentMethod",source ="creditLoan.debtPayMethod")
    @Mapping(target = "paymentMethodValue",source ="creditLoan.debtPayMethodValue")
    LoanInfo mapLoanInfo(LoanBuild loanBuild);

    @Mapping(target = "amount", source = "credit.loanAmount")
    @Mapping(target = "cif", source = "customer.cif")
    @Mapping(target = "approvalLoanId", source = "credit.id")
    @Mapping(target = "limitAmount", source = "credit.loanAmount")
    @Mapping(target = "products", expression = "java(products(loanBuild.getCredit()))")
    @Mapping(target = "purpose", source = "creditOverdraft.loanPurposeValue")
    @Mapping(target = "tenorOfDebtContract", source = "creditOverdraft.limitSustentivePeriod")
    @Mapping(target = "principalPaymentFrequency", source = "creditOverdraft", qualifiedByName = "buildOverdraftPaymentFrequency")
    @Mapping(target = "interestPaymentFrequency", source = "creditOverdraft", qualifiedByName = "buildOverdraftPaymentFrequency")
    @Mapping(target = "requestDrafting", source = "requestDrafting")
    @Mapping(target = "type", source = "credit", qualifiedByName = "buildLoanType")
    @Mapping(target = "holdOnTime", source = "creditOverdraft.limitSustentivePeriod")
    @Mapping(target = "approvalMinutesActiveTime", source = "app.effectivePeriod")
    @Mapping(target = "index", source = "index")
    @Mapping(target = "name", expression = "java(buildLoanName(loanBuild.getIndex()))")
    @Mapping(target = "purposeCode",source ="creditOverdraft.loanPurpose")
    @Mapping(target = "productCode",source ="creditOverdraft.interestRateCode")
    @Mapping(target = "productName",source ="creditOverdraft.productName")
    @Mapping(target = "productInfo",source ="creditOverdraft.productInfoCode")
    @Mapping(target = "productInfoValue",source ="creditOverdraft.productInfoName")
    @Mapping(target = "paymentMethod",source ="creditOverdraft.debtPayMethod")
    @Mapping(target = "paymentMethodValue",source ="creditOverdraft.debtPayMethodValue")
    LoanInfo mapOverdraftInfo(LoanBuild loanBuild);

    default List<String> products(ApplicationCreditEntity credit) {
        if (credit != null && credit.getDocumentCode() != null) {
            return Arrays.asList(credit.getDocumentCode().replaceAll("QD", "QĐ").replaceAll("_", "."));
        }
        return Arrays.asList(credit.getDocumentCode());
    }

    default String buildLoanName(Integer index) {
        return PREFIX_LOAN_NAME.concat(" ").concat(String.valueOf(index + 1));
    }

    @Named("buildLoanPurpose")
    default String buildLoanPurpose(ApplicationCreditLoanEntity creditLoan){
        return StringUtils.isEmpty(creditLoan.getLoanPurposeExplanation())
                ? creditLoan.getLoanPurposeValue() : creditLoan.getLoanPurposeValue() + ". " + creditLoan.getLoanPurposeExplanation();
    }

    @Named("buildPrincipalLoanPaymentFrequency")
    default Integer buildPrincipalLoanPaymentFrequency(ApplicationCreditLoanEntity creditLoan) {
        if (V001.equals(creditLoan.getDebtPayMethod()) || V002.equals(creditLoan.getDebtPayMethod())) {
            return 1;
        } else if (V003.equals(creditLoan.getDebtPayMethod()) || V004.equals(creditLoan.getDebtPayMethod())) {

            return V001.equals(creditLoan.getCreditForm()) ? creditLoan.getLoanPeriod() :
                    V002.equals(creditLoan.getCreditForm()) ? creditLoan.getKunnPeriod() : null;
        } else {
            return creditLoan.getPrincipalPayPeriod();
        }
    }

    @Named("buildOverdraftPaymentFrequency")
    default Integer buildOverdraftPaymentFrequency(ApplicationCreditOverdraftEntity creditOverdraft) {
        if (V001.equals(creditOverdraft.getDebtPayMethod()) || V002.equals(creditOverdraft.getDebtPayMethod())) {
            return 1;
        } else if (V003.equals(creditOverdraft.getDebtPayMethod()) || V004.equals(creditOverdraft.getDebtPayMethod())) {
            return creditOverdraft.getLimitSustentivePeriod();
        }
        return null;
    }

    @Named("buildInterestPaymentFrequency")
    default Integer buildInterestPaymentFrequency(ApplicationCreditLoanEntity creditLoan) {
        if (V001.equals(creditLoan.getDebtPayMethod()) || V002.equals(creditLoan.getDebtPayMethod())) {
            return 1;
        } else if (V004.equals(creditLoan.getDebtPayMethod())) {
            return V001.equals(creditLoan.getCreditForm()) ? creditLoan.getLoanPeriod() :
                    V002.equals(creditLoan.getCreditForm()) ? creditLoan.getKunnPeriod() : null;
        } else {
            return creditLoan.getInterestPayPeriod();
        }
    }

    @Named("buildIncomes")
    default List<IncomeInfo> locationToLocationDto(ApplicantBuild applicantBuild) {
        List<IncomeInfo> incomeInfos = new ArrayList<>();
        Set<ApplicationIncomeEntity> incomes = applicantBuild.getApp().getIncomes();
        CustomerEntity cust = applicantBuild.getCust();
    incomes.stream()
        .forEach(
            income -> {
              Set<RentalIncomeEntity> rentalIncomes =
                  Optional.ofNullable(income.getRentalIncomes()).orElseGet(Collections::emptySet);

              if (checkOwnerIncomeRental(rentalIncomes, cust))
                rentalIncomes.forEach(
                    rentalIncome -> incomeInfos.add(buildIncome(IncomeType.RENTAL.name(), income)));

              Set<SalaryIncomeEntity> salaryIncomes =
                  Optional.ofNullable(income.getSalaryIncomes()).orElseGet(Collections::emptySet);

              if (checkOwnerIncomeSalary(salaryIncomes, cust)) {
                salaryIncomes.forEach(
                    salaryIncome -> incomeInfos.add(buildIncome(IncomeType.SALARY.name(), income)));
              }

              Set<IndividualEnterpriseIncomeEntity> individualEnterpriseIncomes =
                  Optional.ofNullable(income.getIndividualEnterpriseIncomes())
                      .orElseGet(Collections::emptySet);

              if (checkOwnerIndividualEnterprise(individualEnterpriseIncomes, cust)) {
                individualEnterpriseIncomes.forEach(
                    individualIncome -> {
                      if (V003.equalsIgnoreCase(individualIncome.getIncomeType())) {
                        incomeInfos.add(buildIncome(IncomeType.BUSINESS.name(), income));
                      }
                      if (V004.equalsIgnoreCase(individualIncome.getIncomeType())) {
                        incomeInfos.add(buildIncome(IncomeType.ENTERPRISE.name(), income));
                      }
                    });
              }

              Set<OtherIncomeEntity> otherIncomes =
                  Optional.ofNullable(income.getOtherIncomes()).orElseGet(Collections::emptySet);

              if (checkOwnerIncomeOther(otherIncomes, cust)) {
                otherIncomes.forEach(
                    otherIncome -> incomeInfos.add(buildIncome(IncomeType.OTHER.name(), income)));
              }
            });

        return incomeInfos;

    }

    default boolean checkOwnerIncomeRental(Set<RentalIncomeEntity> rentalIncomes, CustomerEntity cust) {
        return rentalIncomes.stream()
                .anyMatch(rental -> cust.getId().equals(rental.getCustomerId()));
    }

    default boolean checkOwnerIncomeSalary(Set<SalaryIncomeEntity> salaryIncomes, CustomerEntity cust) {
        return salaryIncomes.stream()
                .anyMatch(salary -> cust.getId().equals(salary.getCustomerId()));
    }

    default boolean checkOwnerIndividualEnterprise(Set<IndividualEnterpriseIncomeEntity> individualEnterprise, CustomerEntity cust) {
        return individualEnterprise.stream()
                .anyMatch(enterprise -> cust.getId().equals(enterprise.getCustomerId()));
    }

    default boolean checkOwnerIncomeOther(Set<OtherIncomeEntity> otherIncomes, CustomerEntity cust) {
        return otherIncomes.stream()
                .anyMatch(otherIncome -> cust.getId().equals(otherIncome.getCustomerId()));
    }

    @Named("buildTypeOfCreditOffer")
    default String buildTypeOfCreditOffer(String type) {
        return CreditForm.getValue(type);
    }

    @Named("buildDisbursementsNo")
    default String buildDisbursementsNo(String type) {
        if(StringUtils.isNotEmpty(type)) {
            if (Objects.equals(type, V001)) {
                return "ONE";
            } else if (Objects.equals(type, V002)) {
                return "MANY";
            }
        }
        return "";
    }

    @Named("buildTypeOfDisbursements")
    default String buildTypeOfDisbursements(String type) {
        return DisbursementMethod.getDisbursementByCode(type);
    }

    @Named("buildLoanType")
    default String buildLoanType(ApplicationCreditEntity credit) {
        if (credit.getCreditType().equals(CreditType.LOAN.getCode())) {
            return GuaranteeForm.COLLATERAL.getCode().equals(credit.getGuaranteeForm()) ?
                    "THE_CHAP" : "TIN_CHAP";
        } else if (credit.getCreditType().equals(CreditType.OVERDRAFT.getCode())) {
            return GuaranteeForm.COLLATERAL.getCode().equals(credit.getGuaranteeForm()) ?
                    "THAU_CHI_CO_TSBD" : "THAU_CHI_KHONG_TSDB";
        }
        return null;
    }


    @Named("buildFullName")
    default String buildFullName(IndividualCustomerEntity individual) {
        return individual.getLastName() + " " + individual.getFirstName();
    }

    @Named("buildSegment")
    default String buildSegment(ApplicationEntity app) {
        return Segment.getValue(app.getSegment());
    }

    @Named("buildFormatDate")
    default String buildFormatDate(LocalDate date) {
        return date.format(formatter);
    }

    @Named("buildGender")
    default Gender buildGender(IndividualCustomerEntity individual) {
        return "M".equalsIgnoreCase(individual.getGender()) ? Gender.MALE : Gender.FEMALE;
    }

    @Mapping(target = "type", source = "type")
    @Mapping(target = "currency", source = "income.currency")
    @Mapping(target = "recognizedIncome", source = "income.recognizedIncome")
    @Mapping(target = "explanation", source = "income.explanation")
    @Mapping(target = "incomeRecognitionMethod", source = "income.incomeRecognitionMethod")
    @Mapping(target = "approvalIncomeId", source = "income.id")
    IncomeInfo buildIncome(String type, ApplicationIncomeEntity income);
}
